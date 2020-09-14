package com.project.json2sql.service.impl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.json2sql.dto.ConfigProxyRequestDto;
import com.project.json2sql.dto.ConfigurationDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.dto.PagesDto;
import com.project.json2sql.dto.PropertyDto;
import com.project.json2sql.dto.RequestSearchStringDto;
import com.project.json2sql.dto.SummaryDto;
import com.project.json2sql.helper.SearchStringServiceHelper;
import com.project.json2sql.model.AuditProperties;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OffsetBasedPageRequest;
import com.project.json2sql.model.PageSkipTransaction;
import com.project.json2sql.model.PageStringDetails;
import com.project.json2sql.model.PageTransaction;
import com.project.json2sql.model.Properties;
import com.project.json2sql.model.SearchString;
import com.project.json2sql.model.SearchStringFile;
import com.project.json2sql.model.Summary;
import com.project.json2sql.repository.AuditPropertiesRepository;
import com.project.json2sql.repository.ConfigPropertiesRepository;
import com.project.json2sql.repository.PageSkipTransactionRepository;
import com.project.json2sql.repository.PageStringDetailsRepository;
import com.project.json2sql.repository.PageTransactionRepository;
import com.project.json2sql.repository.PropertiesRepository;
import com.project.json2sql.repository.SearchStringRecordsRepository;
import com.project.json2sql.repository.SearchStringRepository;
import com.project.json2sql.repository.SummaryRepository;
import com.project.json2sql.service.SearchStringService;
import com.project.json2sql.tasks.ExecuteStringProxyTask;
import com.project.json2sql.util.DateUtil;
import com.project.json2sql.util.ProxyCall;
import com.project.json2sql.util.ServiceUtil;

@Service
public class SearchStringServiceImpl implements SearchStringService{

	public static final Logger logger = LoggerFactory.getLogger(SearchStringServiceImpl.class);

	private static final int THREAD_POOL_VALUE = 1;
	
	private static final int ALL_THREAD_POOL_VALUE = 10;

	@Value("${localStringUploadPath}")
	private String localStringUploadPath;

	@Value("${processJsonPath}")
	private String processJsonPath;
	
	@Value("${jsonPath}")
    private String jsonPath;

	@Autowired
	SearchStringRepository searchStringRepository;

	@Autowired
	SearchStringRecordsRepository searchStringRecordsRepository;

	@Autowired
	ConfigPropertiesRepository configPropertiesRepository;

	@Autowired
	SearchStringServiceHelper searchStringServiceHelper;

	@Autowired
	PageTransactionRepository pageTransactionRepository;

	@Autowired
	PageSkipTransactionRepository pageSkipTransactionRepository;

	@Autowired
	SummaryRepository summaryRepository;

	@Autowired
	PropertiesRepository propertiesRepository;

	@Autowired
	AuditPropertiesRepository auditPropertiesRepository;
	
	@Autowired
	PageStringDetailsRepository pageStringDetailsRepository;


	private ExecutorService service;

	@Override
	public String uploadFile(MultipartFile file) {
		String msg = null;
		String status = null;
		String fileName = null;
		String filePath = null;
		SearchStringFile searchStringFileObj = new SearchStringFile();
		try {
			if (file.isEmpty()) {
				msg = "Failed to store empty file";
			}else {
				fileName = file.getOriginalFilename();
				filePath = localStringUploadPath + fileName;
				InputStream is = file.getInputStream();

				Files.copy(is, Paths.get(filePath),
						StandardCopyOption.REPLACE_EXISTING);
				status = "Y";
				msg = "File has been Stored";
			}
		} catch (IOException e) {
			status = "N";
			msg = String.format("Failed to store file %f", file.getName());
		}

		searchStringFileObj.setStatus(status);
		searchStringFileObj.setMsg(msg);
		searchStringFileObj.setIsProcess("N");
		searchStringFileObj.setFilePath(filePath);
		searchStringFileObj.setFileName(fileName);
		searchStringFileObj.setCreatedBy("System");
		searchStringFileObj.setCreatedDate(DateUtil.getCurrentDateTime());
		searchStringRepository.save(searchStringFileObj);

		if(status == "Y") {
			processSearchStringJob();
		}

		return status;
	}

	@Override
	public List<SearchStringFile> getSearchString(int pageLimit, int offset) {
		List<SearchStringFile> searchStringObj = new ArrayList<>();
		try {
			logger.info("Get all Search String File with limit {} and offset {}", pageLimit, offset);
			Pageable pageable = new OffsetBasedPageRequest(pageLimit, offset);
			searchStringObj = searchStringRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Search String File Fetch Service" + e);
		}
		return searchStringObj;
	}

	public void pageProcessSearchStringJob() {
		List<SearchString> searchStringObj = new ArrayList<>();
		try {
			long endPage = 0;
			searchStringObj = searchStringRecordsRepository.fetchStringActive("N", endPage);
			if(searchStringObj.size()>0) {
				for(SearchString fileObj : searchStringObj) {
					//Check Page count
					ConfigProperties cofigObj = configPropertiesRepository.fetchById(2);
					MainJson mainJsonObj = pageCheck(cofigObj, fileObj.getSearchString());

					if(null != mainJsonObj) {
						logger.info("Proxy execution for Search String :: "+fileObj.getSearchString());
						long totalPage = mainJsonObj.getResponse().getResult().getPages().getTotalPages();
						searchStringRecordsRepository.updateSearchStringTotalPage(fileObj.getSearchString(), totalPage, "Y");
					}
				}
			}
		}catch (Exception e) {

		}

	}

	@Override
	public void processSearchStringJob() {
		List<SearchStringFile> searchStringObj = new ArrayList<>();
		try {
			searchStringObj = searchStringRepository.fetchActiveString("Y", "N");
			if(searchStringObj.size()>0) {
				for(SearchStringFile fileObj : searchStringObj) {
					readFromExcel(fileObj);
				}
			}
		} catch (Exception e) {

		}

	}

	private void readFromExcel(SearchStringFile fileObj) {
		String file = fileObj.getFilePath();
		String processPath = null;
		try {
			FileInputStream fis=new FileInputStream(new File(file));
			XSSFWorkbook wb=new XSSFWorkbook(fis);  
			Sheet sheet=wb.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			while (rowIterator.hasNext()) { 
				Row row = rowIterator.next(); 
				// For each row, iterate through each columns 
				Iterator<Cell> cellIterator = row.cellIterator(); 
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next(); 
					switch (cell.getCellType()) { 
					case Cell.CELL_TYPE_STRING: 
						logger.info(cell.getStringCellValue()); 
						saveSearchString(cell.getStringCellValue());
						break; 
					case Cell.CELL_TYPE_NUMERIC: 
						logger.info(cell.getNumericCellValue()+""); 
						saveSearchString(cell.getNumericCellValue()+"");
						break; 
					case Cell.CELL_TYPE_BOOLEAN: 
						logger.info(cell.getBooleanCellValue()+""); 
						break; 
					default : 
					}
				}

			}

			fileObj.setIsProcess("Y");
			searchStringRepository.save(fileObj);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void saveSearchString(String stringCellValue) {
		logger.info("Search String Save Start:::: "+stringCellValue);
		SearchString searchStringObj = new SearchString();
		try {
			if(null != stringCellValue) {
				if(!stringCellValue.trim().equalsIgnoreCase("searchString")) {
					int count = searchStringRecordsRepository.getStringCount(stringCellValue);
					if(count == 0) {
						//Check Page count
						/*ConfigProperties cofigObj = configPropertiesRepository.fetchById(2);
						MainJson mainJsonObj = pageCheck(cofigObj, stringCellValue);
						 */

						searchStringObj.setSearchString(stringCellValue);
						searchStringObj.setCreatedDate(DateUtil.getCurrentDateTime());
						searchStringObj.setIsProcess("N");
						searchStringObj.setStartPage(1);
						searchStringObj.setEndPage(0);
						searchStringObj.setLastReadPage(0);

						searchStringRecordsRepository.save(searchStringObj);
					}else {
						logger.info("Search String already Available::: "+stringCellValue);
						List<SearchString> searchStringListObj = searchStringRecordsRepository.fetchStringById(stringCellValue);
						if(searchStringListObj.size()>0) {
							if(searchStringListObj.get(0).getEndPage()==0) {
								searchStringRecordsRepository.deleteSearchString(stringCellValue);
								//ConfigProperties cofigObj = configPropertiesRepository.fetchById(2);
								//MainJson mainJsonObj = pageCheck(cofigObj, stringCellValue);

								searchStringObj.setSearchString(stringCellValue);
								searchStringObj.setCreatedDate(DateUtil.getCurrentDateTime());
								searchStringObj.setIsProcess("N");
								searchStringObj.setStartPage(1);
								searchStringObj.setEndPage(0);
								searchStringObj.setLastReadPage(0);

								searchStringRecordsRepository.save(searchStringObj);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			searchStringObj.setSearchString(stringCellValue);
			searchStringObj.setCreatedDate(DateUtil.getCurrentDateTime());
			searchStringObj.setIsProcess("N");
			searchStringObj.setStartPage(0);
			searchStringObj.setEndPage(0);
			searchStringObj.setLastReadPage(0);

			searchStringRecordsRepository.save(searchStringObj);
			logger.error("String not save::::"+e.getMessage());
		}

	}


	@Override
	public List<SearchString> getSearchStringList(int pageLimit, int offset) {
		List<SearchString> searchStringObj = new ArrayList<>();
		try {
			logger.info("Get all Search String File with limit {} and offset {}", pageLimit, offset);
			Pageable pageable = new OffsetBasedPageRequest(pageLimit, offset);
			searchStringObj = searchStringRecordsRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Search String File Fetch Service" + e);
		}
		return searchStringObj;
	}

	@Override
	public List<SearchString> getSearchStringById(String id) {
		List<SearchString> searchStringObj = new ArrayList<>();
		try {
			if(null != id) {
				searchStringObj=searchStringRecordsRepository.fetchStringById(id);
			}
		} catch (Exception e) {
			logger.error("Error during fetch:: "+e.getMessage());
		}
		return searchStringObj;
	}

	@Override
	public int countSearchString() {
		int count = searchStringRecordsRepository.countSearchString();
		return count;
	}

	@Override
	public ConfigProperties setConfigDetails(ConfigurationDto configDtoObj) {
		ConfigProperties cofigPropObj = new ConfigProperties();
		try {
			cofigPropObj.setId(2);
			cofigPropObj.setOp(configDtoObj.getOp());
			cofigPropObj.setSid(configDtoObj.getSid());
			cofigPropObj.setUid(configDtoObj.getUid());
			cofigPropObj.setIp(configDtoObj.getIp());
			cofigPropObj.setPort(configDtoObj.getPort());
			cofigPropObj.setAuthorization(configDtoObj.getAuthorization());
			cofigPropObj.setFrequency(configDtoObj.getFrequency());
			cofigPropObj.setStarttime(configDtoObj.getStarttime());
			cofigPropObj.setEndtime(configDtoObj.getEndtime());
			cofigPropObj.setUrl(configDtoObj.getUrl());
			cofigPropObj.setLoc(configDtoObj.getLoc());
			cofigPropObj.setAppcode(configDtoObj.getAppCode());
			cofigPropObj.setMaxResult(configDtoObj.getMaxResult());
			cofigPropObj = configPropertiesRepository.save(cofigPropObj);
		} catch (Exception e) {
			logger.error("Error in Config prop Save Service" + e);
		}
		return cofigPropObj;
	}

	@Override
	public ConfigProperties fetchConfigProp() {
		ConfigProperties cofigObj = new ConfigProperties();
		try {
			cofigObj = configPropertiesRepository.fetchById(2);
		} catch (Exception e) {
			logger.error("Error in Config prop Fetch Service" + e);
		}
		return cofigObj;
	}

	@Override
	public String testProxy(ConfigurationDto configDtoObj) throws Exception {
		String responceCode = ProxyCall.testProxy(configDtoObj);
		return responceCode;
	}

	@Override
	public void multiThreadExecuteProxy(ConfigProxyRequestDto configProxyRequestDto) {
		try {
			service = Executors.newFixedThreadPool(THREAD_POOL_VALUE);
			List<Future<String>> resultList = null;
			List<ExecuteStringProxyTask> taskList = new ArrayList<ExecuteStringProxyTask>();
			//configPropertiesRepository.updateStopConfigDetails(2,null);
			if(null != configProxyRequestDto.getSearchString() && null != configProxyRequestDto.getPageNumber()) {
				int skipCount = pageSkipTransactionRepository.countSkipSearchString(configProxyRequestDto.getSearchString());
				if(skipCount ==  0) {
					String isProcess = stringSingleHit(configProxyRequestDto);
				}
			}else if(null != configProxyRequestDto.getSearchString()) {
				int skipCount = pageSkipTransactionRepository.countSkipSearchString(configProxyRequestDto.getSearchString());
				if(skipCount ==  0) {
					createTaskListProcess(configProxyRequestDto, taskList);
				}
			}
			else {
				List<SearchString> searchStringObjList = new ArrayList<>();
				searchStringObjList = searchStringRecordsRepository.findAll();
				createAllTaskListProcess(configProxyRequestDto, searchStringObjList, taskList);
			}
			//resultList = service.invokeAll(taskList);
		} catch (Exception e) {
			logger.error("Error in multiThreadExecuteProxy Service" + e);
		} finally {
			service.shutdown();
		}
	}

	private void createAllTaskListProcess(ConfigProxyRequestDto configProxyRequestDto,
			List<SearchString> searchStringObjList, List<ExecuteStringProxyTask> taskList) {
		String isProcess="N";
		service = Executors.newFixedThreadPool(ALL_THREAD_POOL_VALUE);
		try {
			if(searchStringObjList.size()>0) {
				for(SearchString searchStringObj : searchStringObjList) {
					String searchString = searchStringObj.getSearchString();
					List<Future<String>> resultList = null;
					logger.info("Search String Process For ::::: "+searchString+" With MaxResult ::"+configProxyRequestDto.getMaxResult());
					//Check Search String process
					int skipCount = pageSkipTransactionRepository.countSkipSearchString(searchString);
					if(skipCount ==  0) {
						//Check Available page count
						PageStringDetails pageStringDetailsObj = pageStringDetailsRepository.getSearchStringDetails(searchString);
						if(null != pageStringDetailsObj) {
							long currentPage = ServiceUtil.pageCalculate(pageStringDetailsObj.getPageNumber(), 
									pageStringDetailsObj.getPageSize(), Long.parseLong(configProxyRequestDto.getMaxResult()), pageStringDetailsObj.getMaxResult());
							logger.info(":::::::::::::::::::::::::Current Page Is :::::::::::::::::::::::::::::::::::"+currentPage);
							
							long totalPage=ServiceUtil.totalPageCalculate(pageStringDetailsObj.getPageNumber(), 
									pageStringDetailsObj.getPageSize(), Long.parseLong(configProxyRequestDto.getMaxResult()), pageStringDetailsObj.getMaxResult());

							if(totalPage>0) {
								int page = 0;
								List<ConfigProxyRequestDto> configProxyRequestDtoList = new ArrayList<>();
								for(page=Integer.parseInt(currentPage+"");page<=Integer.parseInt(totalPage+"");page++) {
									ConfigProxyRequestDto configProxyRequestDtoObj = new ConfigProxyRequestDto();
									configProxyRequestDtoObj.setAuthorization(configProxyRequestDto.getAuthorization());
									configProxyRequestDtoObj.setOp(configProxyRequestDto.getOp());
									configProxyRequestDtoObj.setSid(configProxyRequestDto.getSid());
									configProxyRequestDtoObj.setUid(configProxyRequestDto.getUid());
									configProxyRequestDtoObj.setUrl(configProxyRequestDto.getUrl());
									configProxyRequestDtoObj.setIp(configProxyRequestDto.getIp());
									configProxyRequestDtoObj.setPort(configProxyRequestDto.getPort());
									configProxyRequestDtoObj.setFrequency(configProxyRequestDto.getFrequency());
									configProxyRequestDtoObj.setLoc(configProxyRequestDto.getLoc());
									configProxyRequestDtoObj.setAppCode(configProxyRequestDto.getAppCode());
									configProxyRequestDtoObj.setSearchString(searchString);
									configProxyRequestDtoObj.setMaxResult(configProxyRequestDto.getMaxResult());

									configProxyRequestDtoObj.setPageNumber(page+"");
									configProxyRequestDtoObj.setPageSize(totalPage+"");
									configProxyRequestDtoObj.setEndtime(configProxyRequestDto.getEndtime());
									configProxyRequestDtoObj.setStarttime(configProxyRequestDto.getStarttime());
									configProxyRequestDtoObj.setKey("BULK");
									configProxyRequestDtoList.add(configProxyRequestDtoObj);
									ExecuteStringProxyTask proxyTask = new ExecuteStringProxyTask(configProxyRequestDtoObj, searchStringServiceHelper);
									taskList.add(proxyTask);
								}
								logger.info("Total Object Size:::"+taskList.size());
								resultList = service.invokeAll(taskList);
							}
							
						}else {
							configProxyRequestDto.setSearchString(searchString);
							configProxyRequestDto.setPageNumber(1+"");
							configProxyRequestDto.setPageSize(1+"");Map<String, Integer> time=ServiceUtil.currentHour();
							if(time.get("hour") >= Integer.parseInt(configProxyRequestDto.getStarttime()) && time.get("hour") < Integer.parseInt(configProxyRequestDto.getEndtime())) {
								MainJson mainJsonObj = callProxyForStringPageSize(configProxyRequestDto);
							
								if(null != mainJsonObj && null !=mainJsonObj.getResponse()) {
									if(mainJsonObj.getResponse().getStatus().equalsIgnoreCase("success")) {
										long totalPage=mainJsonObj.getResponse().getResult().getPages().getTotalPages();
										long currentPage=mainJsonObj.getResponse().getResult().getPages().getCurrentPage();
			
										if(totalPage>0) {
											int page = 0;
											List<ConfigProxyRequestDto> configProxyRequestDtoList = new ArrayList<>();
											for(page=Integer.parseInt(currentPage+"");page<=Integer.parseInt(totalPage+"");page++) {
												ConfigProxyRequestDto configProxyRequestDtoObj = new ConfigProxyRequestDto();
												configProxyRequestDtoObj.setAuthorization(configProxyRequestDto.getAuthorization());
												configProxyRequestDtoObj.setOp(configProxyRequestDto.getOp());
												configProxyRequestDtoObj.setSid(configProxyRequestDto.getSid());
												configProxyRequestDtoObj.setUid(configProxyRequestDto.getUid());
												configProxyRequestDtoObj.setUrl(configProxyRequestDto.getUrl());
												configProxyRequestDtoObj.setIp(configProxyRequestDto.getIp());
												configProxyRequestDtoObj.setPort(configProxyRequestDto.getPort());
												configProxyRequestDtoObj.setFrequency(configProxyRequestDto.getFrequency());
												configProxyRequestDtoObj.setLoc(configProxyRequestDto.getLoc());
												configProxyRequestDtoObj.setAppCode(configProxyRequestDto.getAppCode());
												configProxyRequestDtoObj.setSearchString(searchString);
												configProxyRequestDtoObj.setMaxResult(configProxyRequestDto.getMaxResult());
			
												configProxyRequestDtoObj.setPageNumber(page+"");
												configProxyRequestDtoObj.setPageSize(totalPage+"");
												configProxyRequestDtoObj.setEndtime(configProxyRequestDto.getEndtime());
												configProxyRequestDtoObj.setStarttime(configProxyRequestDto.getStarttime());
												configProxyRequestDtoObj.setKey("BULK");
												configProxyRequestDtoList.add(configProxyRequestDtoObj);
												ExecuteStringProxyTask proxyTask = new ExecuteStringProxyTask(configProxyRequestDtoObj, searchStringServiceHelper);
												taskList.add(proxyTask);
											}
											logger.info("Total Object Size:::"+taskList.size());
											resultList = service.invokeAll(taskList);
										}
									}else {
										logger.info("Data Not Available for ::::::::::::::::::::::::::"+configProxyRequestDto.getSearchString());
									}
								}
							}else{
								service.shutdownNow();
							}
						}
					}
 				}
			}
		} catch (Exception e) {
			logger.info("Error in All Task:::"+e.getMessage());
		}

	}
	private void createTaskListProcess(ConfigProxyRequestDto configProxyRequestDto, List<ExecuteStringProxyTask> taskList) {
		try {
			service = Executors.newFixedThreadPool(2);
			List<Future<String>> resultList = null;
			String searchString = configProxyRequestDto.getSearchString();
			configProxyRequestDto.setSearchString(searchString);
			configProxyRequestDto.setPageNumber(0+"");
			configProxyRequestDto.setPageSize(1+"");
			MainJson mainJsonObj = callProxyForStringPageSize(configProxyRequestDto);
			if(null != mainJsonObj && null !=mainJsonObj.getResponse()) {
				if(mainJsonObj.getResponse().getStatus().equalsIgnoreCase("success")) {
					int totalPageSize = mainJsonObj.getResponse().getResult().getPages().getTotalPages();
					int currentPage = mainJsonObj.getResponse().getResult().getPages().getCurrentPage();

					if(totalPageSize>0) {
						int page = 0;
						for(page=Integer.parseInt(currentPage+"");page<=Integer.parseInt(totalPageSize+"");page++) {
							ConfigProxyRequestDto configProxyRequestDtoObj = new ConfigProxyRequestDto();
							configProxyRequestDtoObj.setAuthorization(configProxyRequestDto.getAuthorization());
							configProxyRequestDtoObj.setOp(configProxyRequestDto.getOp());
							configProxyRequestDtoObj.setSid(configProxyRequestDto.getSid());
							configProxyRequestDtoObj.setUid(configProxyRequestDto.getUid());
							configProxyRequestDtoObj.setUrl(configProxyRequestDto.getUrl());
							configProxyRequestDtoObj.setIp(configProxyRequestDto.getIp());
							configProxyRequestDtoObj.setPort(configProxyRequestDto.getPort());
							configProxyRequestDtoObj.setFrequency(configProxyRequestDto.getFrequency());
							configProxyRequestDtoObj.setLoc(configProxyRequestDto.getLoc());
							configProxyRequestDtoObj.setAppCode(configProxyRequestDto.getAppCode());
							configProxyRequestDtoObj.setSearchString(searchString);
							configProxyRequestDtoObj.setMaxResult(configProxyRequestDto.getMaxResult());

							configProxyRequestDtoObj.setPageNumber(page+"");
							configProxyRequestDtoObj.setPageSize(totalPageSize+"");
							configProxyRequestDtoObj.setKey("SINGLE");
							//configProxyRequestDtoList.add(configProxyRequestDtoObj);
							pageTransactionRepository.deletePageTrans(configProxyRequestDto.getSearchString(), page, Long.parseLong(configProxyRequestDto.getMaxResult()));
							ExecuteStringProxyTask proxyTask = new ExecuteStringProxyTask(configProxyRequestDtoObj, searchStringServiceHelper);
							taskList.add(proxyTask);
						}
						logger.info("Total Object Size:::"+taskList.size());
						resultList = service.invokeAll(taskList);
					}
				}else {
					logger.info("Data Not Available for ::::::::::::::::::::::::::"+configProxyRequestDto.getSearchString());
				}
			}
		} catch (Exception e) {
			logger.info("Error in Task:::"+e.getMessage());
		}
	}

	@Override
	public void stopMultiThreadExecuteProxy() throws InterruptedException {
		configPropertiesRepository.updateStopConfigDetails(2,null);
		
		if (null != service && !service.isShutdown()) {
			service.shutdownNow();
			System.exit(0);
		}
		
	}

	@Override
	public List<PageSkipTransaction> skipSearchString() {
		List<PageSkipTransaction> PageSkipTransactionListObj = new ArrayList<>();
		try {
			PageSkipTransactionListObj = pageSkipTransactionRepository.fetchBySkipString("Y");
		} catch (Exception e) {
			logger.error("Error: During Fetch Skip Pages:: "+e.getMessage());
		}
		return PageSkipTransactionListObj;
	}

	@Override
	public List<PageSkipTransaction> addSkipSearchString(PageSkipTransaction pageSkipTransaction) {
		List<PageSkipTransaction> PageSkipTransactionListObj = new ArrayList<>();
		try {
			pageSkipTransactionRepository.save(pageSkipTransaction);
			PageSkipTransactionListObj = pageSkipTransactionRepository.fetchBySkipString("Y");
		} catch (Exception e) {
			logger.error("Error: During Add Skip Pages:: "+e.getMessage());
		}
		return PageSkipTransactionListObj;
	}

	@Override
	public List<PageSkipTransaction> deleteSkipSearchString(PageSkipTransaction pageSkipTransaction) {
		List<PageSkipTransaction> PageSkipTransactionListObj = new ArrayList<>();
		try {
			if(0 != pageSkipTransaction.getPageNumber()) {
				pageSkipTransactionRepository.deleteSearchStringPage(pageSkipTransaction.getSearchString(), pageSkipTransaction.getPageNumber());
			}else {
				pageSkipTransactionRepository.deleteSearchString(pageSkipTransaction.getSearchString());
			}
			PageSkipTransactionListObj = pageSkipTransactionRepository.fetchBySkipString("Y");
		} catch (Exception e) {
			logger.error("Error: During Delete Skip Pages:: "+e.getMessage());
		}
		return PageSkipTransactionListObj;
	}


	@Override
	public List<PageTransaction> processTransactionDetails(int limit, int offset) {
		List<PageTransaction> PageTransactionObjList = new ArrayList<>();
		try {
			logger.info("Get all Search String Transaction with limit {} and offset {}", limit, offset);
			Pageable pageable = new OffsetBasedPageRequest(limit, offset);
			PageTransactionObjList = pageTransactionRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Search String File Fetch Service" + e);
		}
		return PageTransactionObjList;
	}

	@Override
	public String startReRunProxyString(ConfigProxyRequestDto configProxyRequestDto) {
		String isProcess="N";
		//configPropertiesRepository.updateStopConfigDetails(2,null);
		ConfigProperties cofigObj = configPropertiesRepository.fetchById(2);
		if(null != configProxyRequestDto.getSearchString() && null != configProxyRequestDto.getPageNumber()) {
			ConfigProxyRequestDto configProxyReqDto = new ConfigProxyRequestDto();
			
			configProxyReqDto.setAuthorization(cofigObj.getAuthorization());
			configProxyReqDto.setOp(cofigObj.getOp());
			configProxyReqDto.setSid(cofigObj.getSid());
			configProxyReqDto.setUid(cofigObj.getUid());
			configProxyReqDto.setUrl(cofigObj.getUrl());
			configProxyReqDto.setIp(cofigObj.getIp());
			configProxyReqDto.setPort(cofigObj.getPort());
			configProxyReqDto.setFrequency(cofigObj.getFrequency());
			configProxyReqDto.setLoc(cofigObj.getLoc());
			configProxyReqDto.setAppCode(cofigObj.getAppcode());
			configProxyReqDto.setSearchString(configProxyRequestDto.getSearchString());
			configProxyReqDto.setMaxResult(configProxyRequestDto.getMaxResult());
			configProxyReqDto.setPageNumber(configProxyRequestDto.getPageNumber());
			configProxyReqDto.setPageSize(configProxyRequestDto.getPageSize());
			configProxyReqDto.setKey("RERUNFAILED");
			
			isProcess = stringSingleHit(configProxyReqDto);
			if(isProcess == "Y") {
				isProcess = "Success";
			}else {
				isProcess = "Failed";
			}
		}
		return isProcess;
	}


	@Override
	public void multiThreadFailedRunProxy() {
		try {
			service = Executors.newFixedThreadPool(THREAD_POOL_VALUE);
			List<Future<String>> resultList = null;
			List<ExecuteStringProxyTask> taskList = new ArrayList<ExecuteStringProxyTask>();
			ConfigProperties cofigObj = configPropertiesRepository.fetchById(2);
			ConfigProxyRequestDto configProxyReqDto = new ConfigProxyRequestDto();
			if(null != cofigObj) {
				configProxyReqDto.setAuthorization(cofigObj.getAuthorization());
				configProxyReqDto.setOp(cofigObj.getOp());
				configProxyReqDto.setSid(cofigObj.getSid());
				configProxyReqDto.setUid(cofigObj.getUid());
				configProxyReqDto.setUrl(cofigObj.getUrl());
				configProxyReqDto.setIp(cofigObj.getIp());
				configProxyReqDto.setPort(cofigObj.getPort());
				configProxyReqDto.setFrequency(cofigObj.getFrequency());
				configProxyReqDto.setLoc(cofigObj.getLoc());
				configProxyReqDto.setAppCode(cofigObj.getAppcode());
				configProxyReqDto.setMaxResult(cofigObj.getMaxResult());
			}
			createFailedTaskListProcess(configProxyReqDto, taskList);
		} catch (Exception e) {
			logger.error("Error in multiThreadExecuteProxy Service" + e);
		} finally {
			service.shutdown();
		}
	}

	private void createFailedTaskListProcess(ConfigProxyRequestDto configProxyRequestDto,
			List<ExecuteStringProxyTask> taskList) {
		try {
			List<Future<String>> resultList = null;
			logger.info("Fetch All Failed Process");
			List<PageTransaction> pageTransObjList = pageTransactionRepository.fetchByTransFlag("N");
			if(pageTransObjList.size()>0) {
				for(PageTransaction pageTransObj : pageTransObjList) {
					ConfigProxyRequestDto configProxyRequestDtoObj = new ConfigProxyRequestDto();
					configProxyRequestDtoObj.setAuthorization(configProxyRequestDto.getAuthorization());
					configProxyRequestDtoObj.setOp(configProxyRequestDto.getOp());
					configProxyRequestDtoObj.setSid(configProxyRequestDto.getSid());
					configProxyRequestDtoObj.setUid(configProxyRequestDto.getUid());
					configProxyRequestDtoObj.setUrl(configProxyRequestDto.getUrl());
					configProxyRequestDtoObj.setIp(configProxyRequestDto.getIp());
					configProxyRequestDtoObj.setPort(configProxyRequestDto.getPort());
					configProxyRequestDtoObj.setFrequency(configProxyRequestDto.getFrequency());
					configProxyRequestDtoObj.setLoc(configProxyRequestDto.getLoc());
					configProxyRequestDtoObj.setAppCode(configProxyRequestDto.getAppCode());
					configProxyRequestDtoObj.setSearchString(pageTransObj.getSearchString());
					configProxyRequestDtoObj.setMaxResult(configProxyRequestDto.getMaxResult());

					configProxyRequestDtoObj.setPageNumber(pageTransObj.getPageNumber()+"");
					configProxyRequestDtoObj.setPageSize(pageTransObj.getPageSize()+"");
					configProxyRequestDtoObj.setKey("FAILED");
					//configProxyRequestDtoList.add(configProxyRequestDtoObj);
					pageTransactionRepository.deleteById(pageTransObj.getId());
					ExecuteStringProxyTask proxyTask = new ExecuteStringProxyTask(configProxyRequestDtoObj, searchStringServiceHelper);
					taskList.add(proxyTask);
				}
				logger.info("Total Object Size:::"+taskList.size());
				resultList = service.invokeAll(taskList);
			}else {
					logger.info("Data Not Available for ::::::::::::::::::::::::::"+configProxyRequestDto.getSearchString());
			}
		} catch (Exception e) {
			logger.info("Error in Task:::"+e.getMessage());
		}
		
	}

	@Override
	public int countTransactionDetails() {
		int count = pageTransactionRepository.countTransactionDetails();
		return count;
	}















	
	
	/****************************************Util Check **********************************************************/

	public MainJson pageCheck(ConfigProperties cofigObj, String stringCellValue) {
		MainJson mainJsonObj = new MainJson();
		ConfigProxyRequestDto configProxyRequestDto = new ConfigProxyRequestDto();
		try {
			configProxyRequestDto.setAuthorization(cofigObj.getAuthorization());
			configProxyRequestDto.setOp(cofigObj.getOp());
			configProxyRequestDto.setSid(cofigObj.getSid());
			configProxyRequestDto.setUid(cofigObj.getUid());
			configProxyRequestDto.setUrl(cofigObj.getUrl());
			configProxyRequestDto.setIp(cofigObj.getIp());
			configProxyRequestDto.setPort(cofigObj.getPort());
			configProxyRequestDto.setFrequency(cofigObj.getFrequency());
			configProxyRequestDto.setLoc(cofigObj.getLoc());
			configProxyRequestDto.setAppCode(cofigObj.getAppcode());
			configProxyRequestDto.setSearchString(stringCellValue);
			configProxyRequestDto.setMaxResult(cofigObj.getMaxResult());
			configProxyRequestDto.setPageNumber(1+"");
			configProxyRequestDto.setPageSize(1+"");

			mainJsonObj = callProxyForStringPageSize(configProxyRequestDto);
			logger.info("Search String Proxy call for Page size retrive:: Done");
		} catch (Exception e) {
			logger.error("Error: During Porxy Call");
		}
		return mainJsonObj;
	}

	public MainJson callProxyForStringPageSize(ConfigProxyRequestDto configProxyRequestDto)
			throws Exception {
		MainJson mainJsonObj = new MainJson();
		logger.info("Proxy Execution For Single Search String Start:");
		int page = Integer.parseInt(configProxyRequestDto.getPageNumber());
		int maxResult = Integer.parseInt(configProxyRequestDto.getMaxResult());
		pageTransactionRepository.deletePageTrans(configProxyRequestDto.getSearchString(), page, maxResult);
		logger.info("Old Transaction Deleted:: New Stared");
		PageTransaction PageTransactionObj = new PageTransaction();
		PageTransactionObj.setSearchString(configProxyRequestDto.getSearchString());
		PageTransactionObj.setPageNumber(page);
		PageTransactionObj.setMaxResult(maxResult);

		/*Properties systemSettings = System.getProperties();
		systemSettings.setProperty("proxySet", "true");
		systemSettings.setProperty("http.proxyHost", configProxyRequestDto.getIp());
		systemSettings.setProperty("http.proxyPort", configProxyRequestDto.getPort());
		 */
		URL u = new URL(configProxyRequestDto.getUrl());
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();

		try {
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Authorization", configProxyRequestDto.getAuthorization());
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Cache-Control", "no-cache");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);

			RequestSearchStringDto requestSearchStringDto = new RequestSearchStringDto();
			requestSearchStringDto.setOp(configProxyRequestDto.getOp());
			requestSearchStringDto.setShowAdditionalDetailElements(true);
			requestSearchStringDto.setAppCode(configProxyRequestDto.getAppCode());
			requestSearchStringDto.setLoc(configProxyRequestDto.getLoc());
			requestSearchStringDto.setSid(configProxyRequestDto.getSid());
			requestSearchStringDto.setUid(configProxyRequestDto.getUid());
			requestSearchStringDto.setSearchString(configProxyRequestDto.getSearchString());
			requestSearchStringDto.setCategory(4);
			requestSearchStringDto.setPageSize(Integer.parseInt(configProxyRequestDto.getPageSize()));
			requestSearchStringDto.setMaxResult(maxResult);
			requestSearchStringDto.setPageNumber(page);
			requestSearchStringDto.setOther(true);
			requestSearchStringDto.setCommercial(true);
			requestSearchStringDto.setHouse(true);
			requestSearchStringDto.setUnit(true);
			requestSearchStringDto.setLand(true);
			requestSearchStringDto.setRecentlySold(true);
			requestSearchStringDto.setForSale(true);
			requestSearchStringDto.setOthers(true);
			requestSearchStringDto.setForRent(true);
			requestSearchStringDto.setLandSize(0);
			requestSearchStringDto.setParking(0);
			requestSearchStringDto.setBathrooms(0);
			requestSearchStringDto.setBedrooms(0);
			requestSearchStringDto.setSortOrder("asc");
			requestSearchStringDto.setSortBy("0");

			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(requestSearchStringDto);

			OutputStream out = conn.getOutputStream();
			out.write(jsonInString.toString().getBytes());

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");

			Gson g = new Gson();

			logger.info(conn.getResponseCode() + " : " + conn.getResponseMessage());

			if (conn.getResponseCode() != 200) {
				PageTransactionObj.setIsProcess("N");
				PageTransactionObj.setPageSize(Integer.parseInt(configProxyRequestDto.getPageSize()));
				logger.info("Failed ::Response Code:: "+conn.getResponseCode()+" ::String:: "+configProxyRequestDto.getSearchString());
			}else {
				logger.info("String Process for :::::: "+configProxyRequestDto.getSearchString());
				mainJsonObj = g.fromJson(result, MainJson.class);
				//savePropertiesData(mainJsonObj);
				savePropertiesInFile(result,configProxyRequestDto.getSearchString(), page, maxResult);
				PageTransactionObj.setIsProcess("Y");
				if(null != mainJsonObj.getResponse()) 
					PageTransactionObj.setPageSize(mainJsonObj.getResponse().getResult().getPages().getTotalPages());
				
				//Save in Page Details
				if(!configProxyRequestDto.getKey().equalsIgnoreCase("RERUNFAILED")) {
					int pageDetailsCount = pageStringDetailsRepository.countSearchString(configProxyRequestDto.getSearchString());
					if(pageDetailsCount == 0) {
						PageStringDetails pageStringDetailsObj = new PageStringDetails();
						pageStringDetailsObj.setSearchString(configProxyRequestDto.getSearchString());
						pageStringDetailsObj.setPageNumber(page);
						pageStringDetailsObj.setPageSize(Long.parseLong(configProxyRequestDto.getPageSize()));
						pageStringDetailsObj.setMaxResult(maxResult);
						pageStringDetailsObj.setUpdatedDate(DateUtil.getCurrentDateTime());
						pageStringDetailsRepository.save(pageStringDetailsObj);
					}else {
						pageStringDetailsRepository.updatePageByString(configProxyRequestDto.getSearchString(), 
								page, Long.parseLong(configProxyRequestDto.getPageSize()), 
								maxResult, DateUtil.getCurrentDateTime());
					}
				}
			}

			logger.info("Using proxy:" + conn.usingProxy());
		} catch (Exception e) {
			PageTransactionObj.setIsProcess("N");
			PageTransactionObj.setPageSize(Integer.parseInt(configProxyRequestDto.getPageSize()));
			logger.error("Error:: Not Getting Data:: "+e.getMessage());
		} finally {
			PageTransactionObj.setSearchString(configProxyRequestDto.getSearchString());
			//PageTransactionObj.setPageNumber(Integer.parseInt(configProxyRequestDto.getPageNumber()));
			PageTransactionObj.setMaxResult(Integer.parseInt(configProxyRequestDto.getMaxResult()));
			PageTransactionObj.setProcessCode(conn.getResponseCode());
			PageTransactionObj.setErrorLog(conn.getResponseMessage());
			PageTransactionObj.setUsingProxy(conn.usingProxy());
			PageTransactionObj.setCreatedBy("System");
			PageTransactionObj.setCreatedDate(DateUtil.getCurrentDateTime());
			pageTransactionRepository.save(PageTransactionObj);
			conn.disconnect();
		}
		return mainJsonObj;
	}

	private void savePropertiesInFile(String result, String searchString, int page, int maxResult) {
		String fileName = jsonPath+"/"+searchString+"_"+page+"_"+maxResult+".txt";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		    writer.write(result);
		    writer.close();
		} catch (Exception e) {
			logger.error("Error: File not stored ::"+e);
		}
	}

	public String stringSingleHit(ConfigProxyRequestDto configProxyRequestDto) {
		MainJson mainJsonObj = new MainJson();
		String isProcess = "N";
		try {
			mainJsonObj = callProxyForStringPageSize(configProxyRequestDto);
			logger.info("Search String Proxy call for Single Page Hit :: Done");
			if(null != mainJsonObj) {
				//isProcess = savePropertiesData(mainJsonObj);
				isProcess = "Y";
				logger.info("Main Json Not Null:: File Processed:::");
			}
		} catch (Exception e) {
			logger.error("Error: During Porxy Call");
		}
		return isProcess;
	}

	public String savePropertiesData(MainJson mainJsonProxyObj) {
		String isProcess = "N";
		SummaryDto summaryDtoObj = mainJsonProxyObj.getResponse().getResult().getSummary();
		PagesDto pagesDtoObj = mainJsonProxyObj.getResponse().getResult().getPages();
		List<PropertyDto> propertyDtoListObj = mainJsonProxyObj.getResponse().getResult().getProperties();
		logger.info("Property Count ::" + propertyDtoListObj.size());
		try {
			Summary summaryObj = new Summary();
			summaryObj.setTotal(summaryDtoObj.getTotal().toString());
			summaryObj.setOnTheMarket(summaryDtoObj.getOnTheMarket().toString());
			summaryObj.setRecentlySold(summaryDtoObj.getRecentlySold().toString());
			summaryObj.setOther(summaryDtoObj.getOther().toString());
			summaryObj.setIsLastPage(summaryDtoObj.getIsLastPage().toString());
			summaryObj.setForRent(summaryDtoObj.getForRent().toString());
			summaryObj.setTotalPages(pagesDtoObj.getTotalPages().toString());
			summaryObj.setCurrentPage(pagesDtoObj.getCurrentPage().toString());
			summaryObj.setCreatedDate(DateUtil.getCurrentDateTime());
			summaryObj.setCreatedBy("System");
			summaryObj.setProcessId(0);

			summaryObj = summaryRepository.save(summaryObj);

			List<Properties> PropertiesListObj = new ArrayList<Properties>();
			logger.info("Property Count ::" + propertyDtoListObj.size());
			for (PropertyDto propertyDtoObj : propertyDtoListObj) {
				// Check properties already available or not
				List<Properties> propObj = getPropertiesById(propertyDtoObj.getId().toString());
				if (propObj.size() > 0) {
					logger.info(":::::::Property already available, update required::::::::");
					// Update Property table in Audit table
					AuditProperties auditPropertiesObj = new AuditProperties();

					auditPropertiesObj.setPropertiesId(propObj.get(0).getPropertiesId());
					auditPropertiesObj.setType(propObj.get(0).getType());
					auditPropertiesObj.setId(propObj.get(0).getId().toString());
					auditPropertiesObj.setLat(propObj.get(0).getLat().toString());
					auditPropertiesObj.setLon(propObj.get(0).getLon().toString());
					auditPropertiesObj.setUnitNumber(propObj.get(0).getUnitNumber());
					auditPropertiesObj.setStreetNumber(propObj.get(0).getStreetNumber());
					auditPropertiesObj.setStreetName(propObj.get(0).getStreetName());
					auditPropertiesObj.setStreetType(propObj.get(0).getStreetType());
					auditPropertiesObj.setStreetDirection(propObj.get(0).getStreetDirection());
					auditPropertiesObj.setAddress(propObj.get(0).getAddress());
					auditPropertiesObj.setSuburb(propObj.get(0).getSuburb());
					auditPropertiesObj.setPostcode(propObj.get(0).getPostcode());
					auditPropertiesObj.setState(propObj.get(0).getState());
					auditPropertiesObj.setBathrooms(propObj.get(0).getBathrooms().toString());
					auditPropertiesObj.setBedrooms(propObj.get(0).getBedrooms().toString());
					auditPropertiesObj.setParking(propObj.get(0).getParking().toString());
					auditPropertiesObj.setLandSize(propObj.get(0).getLandSize().toString());
					auditPropertiesObj.setSalePrice(propObj.get(0).getSalePrice().toString());
					auditPropertiesObj.setSaleDate(propObj.get(0).getSaleDate());
					auditPropertiesObj.setOnTheMarket(propObj.get(0).getOnTheMarket().toString());
					auditPropertiesObj.setListingDate(propObj.get(0).getListingDate());
					auditPropertiesObj.setListingPrice(propObj.get(0).getListingPrice());
					auditPropertiesObj.setListingDescription(propObj.get(0).getListingDescription());
					auditPropertiesObj.setListedType(propObj.get(0).getListedType());
					auditPropertiesObj.setAuctionDate(propObj.get(0).getAuctionDate());
					auditPropertiesObj.setAuctionTime(propObj.get(0).getAuctionTime());
					auditPropertiesObj.setRentalListingDate(propObj.get(0).getRentalListingDate());
					auditPropertiesObj.setRentalListingPeriod(propObj.get(0).getRentalListingPeriod());
					auditPropertiesObj.setRentalListingPrice(propObj.get(0).getRentalListingPrice());
					auditPropertiesObj.setREAId(propObj.get(0).getREAId());
					auditPropertiesObj.setAgentName(propObj.get(0).getAgentName());
					auditPropertiesObj.setRecentSales(propObj.get(0).getRecentSales().toString());
					auditPropertiesObj.setPhoto(propObj.get(0).getPhoto());
					auditPropertiesObj.setUcv(propObj.get(0).getUcv().toString());
					auditPropertiesObj.setUcvDate(propObj.get(0).getUcvDate());
					auditPropertiesObj.setRealPropertyDescriptor(propObj.get(0).getRealPropertyDescriptor());
					auditPropertiesObj.setLgaName(propObj.get(0).getLgaName());
					auditPropertiesObj.setLastSaleType(propObj.get(0).getLastSaleType());
					auditPropertiesObj.setLotPlan(propObj.get(0).getLotPlan());
					auditPropertiesObj.setZoning(propObj.get(0).getZoning());
					auditPropertiesObj.setIsAgentAdvised(propObj.get(0).getIsAgentAdvised().toString());
					auditPropertiesObj.setLandUsePrimary(propObj.get(0).getLandUsePrimary());
					auditPropertiesObj.setCurrentRentalPrice(propObj.get(0).getCurrentRentalPrice());
					auditPropertiesObj.setForRent(propObj.get(0).getForRent().toString());
					auditPropertiesObj.setForRentAgencyName(propObj.get(0).getForRentAgencyName());
					auditPropertiesObj.setForRentDaysOnMarket(propObj.get(0).getForRentDaysOnMarket().toString());
					auditPropertiesObj.setOccupancyType(propObj.get(0).getOccupancyType());
					auditPropertiesObj.setVolume(propObj.get(0).getVolume());
					auditPropertiesObj.setFolio(propObj.get(0).getFolio());
					auditPropertiesObj.setTitlePrefix(propObj.get(0).getTitlePrefix());
					auditPropertiesObj.setTitleSuffix(propObj.get(0).getTitleSuffix());
					auditPropertiesObj.setMapReference(propObj.get(0).getMapReference());
					auditPropertiesObj.setBlock(propObj.get(0).getBlock());
					auditPropertiesObj.setSection(propObj.get(0).getSection());
					auditPropertiesObj.setDevelopmentZone(propObj.get(0).getDevelopmentZone());
					auditPropertiesObj.setIsPriceWithheld(propObj.get(0).getIsPriceWithheld());
					auditPropertiesObj.setCreatedDate(propObj.get(0).getCreatedDate());
					auditPropertiesObj.setCreatedBy(propObj.get(0).getCreatedBy());
					auditPropertiesObj.setProcessId(propObj.get(0).getProcessId());
					auditPropertiesObj.setLastUpdated(propObj.get(0).getLastUpdated());
					auditPropertiesObj.setUpdatedBy(propObj.get(0).getUpdatedBy());
					auditPropertiesObj.setIsActive("Y");
					auditPropertiesRepository.updateStatus(propObj.get(0).getId().toString(), "N");
					auditPropertiesObj = auditPropertiesRepository.save(auditPropertiesObj);
					// Update Property table in main table
					Properties propertiesObj = new Properties();
					propertiesObj.setPropertiesId(propObj.get(0).getPropertiesId());
					propertiesObj.setType(propertyDtoObj.getType());
					propertiesObj.setId(propertyDtoObj.getId().toString());
					propertiesObj.setLat(propertyDtoObj.getLat().toString());
					propertiesObj.setLon(propertyDtoObj.getLon().toString());
					propertiesObj.setUnitNumber(propertyDtoObj.getUnitNumber());
					propertiesObj.setStreetNumber(propertyDtoObj.getStreetNumber());
					propertiesObj.setStreetName(propertyDtoObj.getStreetName());
					propertiesObj.setStreetType(propertyDtoObj.getStreetType());
					propertiesObj.setStreetDirection(propertyDtoObj.getStreetDirection());
					propertiesObj.setAddress(propertyDtoObj.getAddress());
					propertiesObj.setSuburb(propertyDtoObj.getSuburb());
					propertiesObj.setPostcode(propertyDtoObj.getPostcode());
					propertiesObj.setState(propertyDtoObj.getState());
					propertiesObj.setBathrooms(propertyDtoObj.getBathrooms().toString());
					propertiesObj.setBedrooms(propertyDtoObj.getBedrooms().toString());
					propertiesObj.setParking(propertyDtoObj.getParking().toString());
					propertiesObj.setLandSize(propertyDtoObj.getLandSize().toString());
					propertiesObj.setSalePrice(propertyDtoObj.getSalePrice().toString());
					propertiesObj.setSaleDate(propertyDtoObj.getSaleDate());
					propertiesObj.setOnTheMarket(propertyDtoObj.getOnTheMarket().toString());
					propertiesObj.setListingDate(propertyDtoObj.getListingDate());
					propertiesObj.setListingPrice(propertyDtoObj.getListingPrice());
					propertiesObj.setListingDescription(propertyDtoObj.getListingDescription());
					propertiesObj.setListedType(propertyDtoObj.getListedType());
					propertiesObj.setAuctionDate(propertyDtoObj.getAuctionDate());
					propertiesObj.setAuctionTime(propertyDtoObj.getAuctionTime());
					propertiesObj.setRentalListingDate(propertyDtoObj.getRentalListingDate());
					propertiesObj.setRentalListingPeriod(propertyDtoObj.getRentalListingPeriod());
					propertiesObj.setRentalListingPrice(propertyDtoObj.getRentalListingPrice());
					propertiesObj.setREAId(propertyDtoObj.getREAId());
					propertiesObj.setAgentName(propertyDtoObj.getAgentName());
					propertiesObj.setRecentSales(propertyDtoObj.getRecentSales().toString());
					propertiesObj.setPhoto(propertyDtoObj.getPhoto());
					propertiesObj.setUcv(propertyDtoObj.getUcv().toString());
					propertiesObj.setUcvDate(propertyDtoObj.getUcvDate());
					propertiesObj.setRealPropertyDescriptor(propertyDtoObj.getRealPropertyDescriptor());
					propertiesObj.setLgaName(propertyDtoObj.getLgaName());
					propertiesObj.setLastSaleType(propertyDtoObj.getLastSaleType());
					propertiesObj.setLotPlan(propertyDtoObj.getLotPlan());
					propertiesObj.setZoning(propertyDtoObj.getZoning());
					propertiesObj.setIsAgentAdvised(propertyDtoObj.getIsAgentAdvised().toString());
					propertiesObj.setLandUsePrimary(propertyDtoObj.getLandUsePrimary());
					propertiesObj.setCurrentRentalPrice(propertyDtoObj.getCurrentRentalPrice());
					propertiesObj.setForRent(propertyDtoObj.getForRent().toString());
					propertiesObj.setForRentAgencyName(propertyDtoObj.getForRentAgencyName());
					propertiesObj.setForRentDaysOnMarket(propertyDtoObj.getForRentDaysOnMarket().toString());
					propertiesObj.setOccupancyType(propertyDtoObj.getOccupancyType());
					propertiesObj.setVolume(propertyDtoObj.getVolume());
					propertiesObj.setFolio(propertyDtoObj.getFolio());
					propertiesObj.setTitlePrefix(propertyDtoObj.getTitlePrefix());
					propertiesObj.setTitleSuffix(propertyDtoObj.getTitleSuffix());
					propertiesObj.setMapReference(propertyDtoObj.getMapReference());
					propertiesObj.setBlock(propertyDtoObj.getBlock());
					propertiesObj.setSection(propertyDtoObj.getSection());
					propertiesObj.setDevelopmentZone(propertyDtoObj.getDevelopmentZone());
					propertiesObj.setIsPriceWithheld(propertyDtoObj.getIsPriceWithheld());
					propertiesObj.setCreatedDate(propObj.get(0).getCreatedDate());
					propertiesObj.setCreatedBy("System");
					propertiesObj.setLastUpdated(DateUtil.getCurrentDateTime());
					propertiesObj.setUpdatedBy("System");
					propertiesObj.setProcessId(0);
					propertiesObj.setIsActive("Y");
					propertiesObj.setIsProcess("N");

					propertiesObj = propertiesRepository.save(propertiesObj);

					PropertiesListObj.add(propertiesObj);
				} else {
					logger.info(":::::::Property Insert required::::::::");
					Properties propertiesObj = new Properties();
					propertiesObj.setType(propertyDtoObj.getType());
					propertiesObj.setId(propertyDtoObj.getId().toString());
					propertiesObj.setLat(propertyDtoObj.getLat().toString());
					propertiesObj.setLon(propertyDtoObj.getLon().toString());
					propertiesObj.setUnitNumber(propertyDtoObj.getUnitNumber());
					propertiesObj.setStreetNumber(propertyDtoObj.getStreetNumber());
					propertiesObj.setStreetName(propertyDtoObj.getStreetName());
					propertiesObj.setStreetType(propertyDtoObj.getStreetType());
					propertiesObj.setStreetDirection(propertyDtoObj.getStreetDirection());
					propertiesObj.setAddress(propertyDtoObj.getAddress());
					propertiesObj.setSuburb(propertyDtoObj.getSuburb());
					propertiesObj.setPostcode(propertyDtoObj.getPostcode());
					propertiesObj.setState(propertyDtoObj.getState());
					propertiesObj.setBathrooms(propertyDtoObj.getBathrooms().toString());
					propertiesObj.setBedrooms(propertyDtoObj.getBedrooms().toString());
					propertiesObj.setParking(propertyDtoObj.getParking().toString());
					propertiesObj.setLandSize(propertyDtoObj.getLandSize().toString());
					propertiesObj.setSalePrice(propertyDtoObj.getSalePrice().toString());
					propertiesObj.setSaleDate(propertyDtoObj.getSaleDate());
					propertiesObj.setOnTheMarket(propertyDtoObj.getOnTheMarket().toString());
					propertiesObj.setListingDate(propertyDtoObj.getListingDate());
					propertiesObj.setListingPrice(propertyDtoObj.getListingPrice());
					propertiesObj.setListingDescription(propertyDtoObj.getListingDescription());
					propertiesObj.setListedType(propertyDtoObj.getListedType());
					propertiesObj.setAuctionDate(propertyDtoObj.getAuctionDate());
					propertiesObj.setAuctionTime(propertyDtoObj.getAuctionTime());
					propertiesObj.setRentalListingDate(propertyDtoObj.getRentalListingDate());
					propertiesObj.setRentalListingPeriod(propertyDtoObj.getRentalListingPeriod());
					propertiesObj.setRentalListingPrice(propertyDtoObj.getRentalListingPrice());
					propertiesObj.setREAId(propertyDtoObj.getREAId());
					propertiesObj.setAgentName(propertyDtoObj.getAgentName());
					propertiesObj.setRecentSales(propertyDtoObj.getRecentSales().toString());
					propertiesObj.setPhoto(propertyDtoObj.getPhoto());
					propertiesObj.setUcv(propertyDtoObj.getUcv().toString());
					propertiesObj.setUcvDate(propertyDtoObj.getUcvDate());
					propertiesObj.setRealPropertyDescriptor(propertyDtoObj.getRealPropertyDescriptor());
					propertiesObj.setLgaName(propertyDtoObj.getLgaName());
					propertiesObj.setLastSaleType(propertyDtoObj.getLastSaleType());
					propertiesObj.setLotPlan(propertyDtoObj.getLotPlan());
					propertiesObj.setZoning(propertyDtoObj.getZoning());
					propertiesObj.setIsAgentAdvised(propertyDtoObj.getIsAgentAdvised().toString());
					propertiesObj.setLandUsePrimary(propertyDtoObj.getLandUsePrimary());
					propertiesObj.setCurrentRentalPrice(propertyDtoObj.getCurrentRentalPrice());
					propertiesObj.setForRent(propertyDtoObj.getForRent().toString());
					propertiesObj.setForRentAgencyName(propertyDtoObj.getForRentAgencyName());
					propertiesObj.setForRentDaysOnMarket(propertyDtoObj.getForRentDaysOnMarket().toString());
					propertiesObj.setOccupancyType(propertyDtoObj.getOccupancyType());
					propertiesObj.setVolume(propertyDtoObj.getVolume());
					propertiesObj.setFolio(propertyDtoObj.getFolio());
					propertiesObj.setTitlePrefix(propertyDtoObj.getTitlePrefix());
					propertiesObj.setTitleSuffix(propertyDtoObj.getTitleSuffix());
					propertiesObj.setMapReference(propertyDtoObj.getMapReference());
					propertiesObj.setBlock(propertyDtoObj.getBlock());
					propertiesObj.setSection(propertyDtoObj.getSection());
					propertiesObj.setDevelopmentZone(propertyDtoObj.getDevelopmentZone());
					propertiesObj.setIsPriceWithheld(propertyDtoObj.getIsPriceWithheld());
					propertiesObj.setCreatedDate(DateUtil.getCurrentDateTime());
					propertiesObj.setCreatedBy("System");
					propertiesObj.setProcessId(0);
					propertiesObj.setIsActive("Y");
					propertiesObj.setIsProcess("N");

					propertiesObj = propertiesRepository.save(propertiesObj);

					PropertiesListObj.add(propertiesObj);
				}
			}
			isProcess = "Y";
		} catch (Exception e) {
			logger.error("Properties Not caved form Proxy");
		}
		return isProcess;
	}

	public List<Properties> getPropertiesById(String id) {
		List<Properties> propObj = new ArrayList<>();
		try {
			propObj = (List<Properties>) propertiesRepository.fetchById(id);
		} catch (Exception e) {
			logger.error("Error in Property Upload Service" + e);
		}
		return propObj;
	}

}
