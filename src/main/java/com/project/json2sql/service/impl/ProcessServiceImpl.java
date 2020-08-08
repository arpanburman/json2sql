package com.project.json2sql.service.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.dto.PagesDto;
import com.project.json2sql.dto.PropertyDto;
import com.project.json2sql.dto.Root;
import com.project.json2sql.dto.SummaryDto;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OffsetBasedPageRequest;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.ProcessJson;
import com.project.json2sql.model.Properties;
import com.project.json2sql.model.Summary;
import com.project.json2sql.repository.ConfigPropertiesRepository;
import com.project.json2sql.repository.OwnerDetailsRepository;
import com.project.json2sql.repository.ProcessRepository;
import com.project.json2sql.repository.PropertiesJpaRepository;
import com.project.json2sql.repository.PropertiesRepository;
import com.project.json2sql.repository.SummaryRepository;
import com.project.json2sql.service.ProcessService;
import com.project.json2sql.util.DateUtil;
import com.project.json2sql.util.ProxyCall;

@Service
public class ProcessServiceImpl implements ProcessService{
	
	@Value("${localUploadPath}")
    private String localUploadPath;
	
	@Autowired
	ProcessRepository processRepository;
	
	@Autowired
	SummaryRepository summaryRepository;
	
	@Autowired
	PropertiesRepository propertiesRepository;

	@Autowired
	ConfigPropertiesRepository configPropertiesRepository;
	
	@Autowired
	OwnerDetailsRepository ownerDetailsRepository;
	
	@Autowired
	PropertiesJpaRepository propertiesJpaRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);

	public String startProcess(MainJson jsonObj) {
		String isProcess = "N";
		try {
			ProcessJson processJson = new ProcessJson();
			String path = writeInFile(jsonObj.toString(), localUploadPath); 
			logger.info("Stored File Path::"+path);
			processJson.setFilePath(path);
			processJson.setStatus(jsonObj.getResponse().getStatus());
			processJson.setCreatedDate(DateUtil.getCurrentDateTime());
			processJson.setCreatedBy("System");
			processJson = processRepository.save(processJson);
			if(processJson.getProcessId()!=0) {
				SummaryDto summaryDtoObj = jsonObj.getResponse().getResult().getSummary();
				PagesDto pagesDtoObj = jsonObj.getResponse().getResult().getPages();
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
				summaryObj.setProcessId(processJson.getProcessId());
				
				summaryObj = summaryRepository.save(summaryObj);
				
				List<PropertyDto> propertyDtoListObj = jsonObj.getResponse().getResult().getProperties();
				List<Properties> PropertiesListObj = new ArrayList<Properties>();
				logger.info("Property Count ::"+propertyDtoListObj.size());
				for(PropertyDto propertyDtoObj : propertyDtoListObj) {
					//Check properties already available or not
					List<Properties> propObj = getPropertiesById(propertyDtoObj.getId().toString());
					if(propObj.size()>0) {
						logger.info("Property already available, no update required");
					}else {
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
						propertiesObj.setProcessId(processJson.getProcessId());
						
						propertiesObj = propertiesRepository.save(propertiesObj);
						
						PropertiesListObj.add(propertiesObj);
					}
				}
				isProcess = "Y";
			}
		} catch (Exception e) {
			isProcess = "N";
			logger.error("File not Saved Properly::: "+e);
		}
		return isProcess;
	}
	
	
	
	public static String fileSuffix() {
		String fileSuffix = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
		return fileSuffix;
	}
	
	public static String writeInFile(String Value, String localUploadPath) {
		String uuid =  UUID.randomUUID().toString();
		String path= localUploadPath+ uuid+fileSuffix()+".json";
		logger.info("Stored Path:: "+path);
		try {
			Files.write(Paths.get(path), Value.getBytes());
		} catch (Exception e) {
			logger.error("File can't Write:"+e);
		}
		return path;
	}



	@Override
	public OwnerDetails startProxyProcess(InputProxyDto inputObj) {
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		ConfigProperties configPropertiesObj = configPropertiesRepository.fetchById(1);
		try {
			if(null != configPropertiesObj) {
				Root rootObj = ProxyCall.callProxy(configPropertiesObj, inputObj);
				
				if(rootObj != null) {
					ownerDetailsObj.setFirstName(rootObj.getResponse().getResult().getOwnerDetails().get(0).getFirstName());
					ownerDetailsObj.setLastName(rootObj.getResponse().getResult().getOwnerDetails().get(0).getLastName());
					ownerDetailsObj.setOwnerAddress(rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerAddress());
					ownerDetailsObj.setOwnerName(rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerName());
					ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
					ownerDetailsObj.setId(inputObj.getId().toString());
					ownerDetailsRepository.save(ownerDetailsObj);
				}
			}
			
		} catch (Exception e) {
			logger.error("Error in Proxy Service"+e);
		}
		return ownerDetailsObj;
	}



	@Override
	public List<Properties> fetchProperties() {
		List<Properties> propObj = new ArrayList<>();
		try {
			propObj = (List<Properties>) propertiesRepository.findAll();
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service"+e);
		}
		return propObj;
	}
	
	@Override
	public List<Properties> getPropertiesById(String id) {
		List<Properties> propObj = new ArrayList<>();
		try {
			propObj = (List<Properties>) propertiesRepository.fetchById(id);
		} catch (Exception e) {
			logger.error("Error in Property Upload Service"+e);
		}
		return propObj;
	}



	@Override
	public int getPropertiesCount() {
		int count = propertiesRepository.getPropertiesCount();
		return count;
	}



	@Override
	public List<Properties> getPropertiesPager(String offset) {
		List<Properties> propObj = new ArrayList<>();
		try {
			//propObj = (List<Properties>) propertiesRepository.getPropertiesPager(offset);
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service"+e);
		}
		return propObj;
	}
	
	@Override
    public List<Properties> getAllProperties(int limit, int offset) {
		List<Properties> propObj = new ArrayList<>();
		try {
        logger.info("Get all Employees with limit {} and offset {}", limit, offset);
        Pageable pageable = new OffsetBasedPageRequest(limit, offset);
        propObj = propertiesJpaRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service"+e);
		}
		
		return propObj;
    }



	@Override
	public ConfigProperties fetchConfigProp() {
		ConfigProperties cofigObj = new ConfigProperties();
		try {
			cofigObj = configPropertiesRepository.fetchById(1);
		} catch (Exception e) {
			logger.error("Error in Config prop Fetch Service"+e);
		}
		return cofigObj;
	}



	@Override
	public ConfigProperties setConfigDetails(ConfigProperties configObj) {
		ConfigProperties cofigPropObj = new ConfigProperties();
		try {
			cofigPropObj.setId(1);
			cofigPropObj = configPropertiesRepository.save(configObj);
		} catch (Exception e) {
			logger.error("Error in Config prop Save Service"+e);
		}
		return cofigPropObj;
	}

}
