package com.project.json2sql.helper;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.json2sql.dto.ConfigProxyRequestDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.dto.PagesDto;
import com.project.json2sql.dto.PropertyDto;
import com.project.json2sql.dto.RequestSearchStringDto;
import com.project.json2sql.dto.SummaryDto;
import com.project.json2sql.model.AuditProperties;
import com.project.json2sql.model.PageStringDetails;
import com.project.json2sql.model.PageTransaction;
import com.project.json2sql.model.Properties;
import com.project.json2sql.model.Summary;
import com.project.json2sql.repository.AuditPropertiesRepository;
import com.project.json2sql.repository.PageStringDetailsRepository;
import com.project.json2sql.repository.PageTransactionRepository;
import com.project.json2sql.repository.PropertiesRepository;
import com.project.json2sql.repository.SearchStringRecordsRepository;
import com.project.json2sql.repository.SearchStringRepository;
import com.project.json2sql.repository.SummaryRepository;
import com.project.json2sql.util.DateUtil;
import com.project.json2sql.util.ServiceUtil;

@Component
public class SearchStringServiceHelper {
	
	@Value("${jsonPath}")
    private String jsonPath;

	@Autowired
	SummaryRepository summaryRepository;

	@Autowired
	PropertiesRepository propertiesRepository;
	
	@Autowired
	AuditPropertiesRepository auditPropertiesRepository;
	
	@Autowired
	SearchStringRepository searchStringRepository;
	
	@Autowired
	SearchStringRecordsRepository searchStringRecordsRepository;
	
	@Autowired
	PageTransactionRepository pageTransactionRepository;
	
	@Autowired
	PageStringDetailsRepository pageStringDetailsRepository;
	
	private ExecutorService service;
	
	public static final Logger logger = LoggerFactory.getLogger(SearchStringServiceHelper.class);

	public String doExecuteProxy(ConfigProxyRequestDto configProxyRequestDto, String status) throws Exception {
		String isProcess = "N";
		MainJson mainJsonProxyObj = null;
		if(configProxyRequestDto.getKey().equalsIgnoreCase("BULK")) {
			Map<String, Integer> time=ServiceUtil.currentHour();
			if(time.get("hour") >= Integer.parseInt(configProxyRequestDto.getStarttime()) && time.get("hour") < Integer.parseInt(configProxyRequestDto.getEndtime())) {
				mainJsonProxyObj = callProxy(configProxyRequestDto);
				if(null != mainJsonProxyObj) {
					//isProcess = savePropertiesData(mainJsonProxyObj);
					searchStringRecordsRepository.updateSearchString(configProxyRequestDto.getSearchString(), mainJsonProxyObj.getResponse().getResult().getPages().getCurrentPage());
				}
			}else{
				logger.info("Tme over for proxy fetch:: Currently Fetch proxy data (Properties) is in store process in database");
				service.shutdownNow();
			}
		}else {
			mainJsonProxyObj = callProxy(configProxyRequestDto);
			if(null != mainJsonProxyObj) {
				isProcess = savePropertiesData(mainJsonProxyObj);
				searchStringRecordsRepository.updateSearchString(configProxyRequestDto.getSearchString(), mainJsonProxyObj.getResponse().getResult().getPages().getCurrentPage());
			}
		}
		return isProcess;
	}
	
	public MainJson callProxy(ConfigProxyRequestDto configProxyRequestDto)
			throws Exception {
		MainJson mainJsonObj = new MainJson();
		logger.info("Proxy Execution Start:");
		long page = Long.parseLong(configProxyRequestDto.getPageNumber());
		long maxResult = Long.parseLong(configProxyRequestDto.getMaxResult());
		int count = pageTransactionRepository.fetchByActiveSearchString(configProxyRequestDto.getSearchString(), "N", page, maxResult);
		logger.info("If this page available for Nor run before : "+count);
		int availablCount = pageTransactionRepository.fetchByNotAvailableSearchString(configProxyRequestDto.getSearchString(), page, maxResult);
		logger.info("If this page available In transaction : "+availablCount);
		if(count > 1 || availablCount == 0) {
			if(count > 1) {
				pageTransactionRepository.deletePageTrans(configProxyRequestDto.getSearchString(), page, maxResult);
			}
			PageTransaction PageTransactionObj = new PageTransaction();
			PageTransactionObj.setSearchString(configProxyRequestDto.getSearchString());
			PageTransactionObj.setPageNumber(Integer.parseInt(configProxyRequestDto.getPageNumber()));
			PageTransactionObj.setPageSize(Integer.parseInt(configProxyRequestDto.getPageSize()));
			PageTransactionObj.setMaxResult(Integer.parseInt(configProxyRequestDto.getMaxResult()));
			
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
				conn.setRequestMethod("POST");
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
				requestSearchStringDto.setMaxResult(Integer.parseInt(configProxyRequestDto.getMaxResult()));
				requestSearchStringDto.setPageNumber(Integer.parseInt(configProxyRequestDto.getPageNumber()));
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
				logger.info("Obj Print page :::::::::::::::"+Integer.parseInt(configProxyRequestDto.getPageSize())+"::::"+configProxyRequestDto.getSearchString());
				logger.info("Obj Print Paze :::::::::::::::"+result);
				Gson g = new Gson();
	
				logger.info(conn.getResponseCode() + " : " + conn.getResponseMessage());
				PageTransactionObj.setProcessCode(conn.getResponseCode());
				PageTransactionObj.setErrorLog(conn.getResponseMessage());
	
				if (conn.getResponseCode() != 200) {
					PageTransactionObj.setIsProcess("N");
					logger.info("Failed ::Response Code:: "+conn.getResponseCode()+" ::String:: "+configProxyRequestDto.getSearchString());
				}else {
					logger.info("String Process for :::::: "+configProxyRequestDto.getSearchString());
					mainJsonObj = g.fromJson(result, MainJson.class);
					savePropertiesInFile(result, configProxyRequestDto.getSearchString(), page, maxResult);
					PageTransactionObj.setIsProcess("Y");
				}
	
				logger.info("Using proxy:" + conn.usingProxy());
				
			} catch (Exception e) {
				PageTransactionObj.setProcessCode(conn.getResponseCode());
				PageTransactionObj.setErrorLog(conn.getResponseMessage());
				PageTransactionObj.setIsProcess("N");
				logger.error("Error:: Not Process for ::"+configProxyRequestDto.getSearchString()+"::Page::"+configProxyRequestDto.getPageNumber());
			} finally {
				PageTransactionObj.setUsingProxy(conn.usingProxy());
				PageTransactionObj.setCreatedBy("System");
				PageTransactionObj.setCreatedDate(DateUtil.getCurrentDateTime());
				pageTransactionRepository.save(PageTransactionObj);
				
				//Save in Page Details
				if(!configProxyRequestDto.getKey().equalsIgnoreCase("FAILED")) {
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
				conn.disconnect();
			}
		}
		return mainJsonObj;
	}
	
	private void savePropertiesInFile(String result, String searchString, long page, long maxResult) {
		String fileName = jsonPath+"/"+searchString+"_"+page+"_"+maxResult+".txt";
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		    writer.write(result);
		    writer.close();
		} catch (Exception e) {
			logger.error("Error: File not stored ::"+e);
		}
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
