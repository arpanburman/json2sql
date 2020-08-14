package com.project.json2sql.service.impl;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.MapDifference;
import com.google.common.collect.MapDifference.ValueDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.project.json2sql.dto.ConfigurationDto;
import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.dto.PagesDto;
import com.project.json2sql.dto.PropertyDto;
import com.project.json2sql.dto.Root;
import com.project.json2sql.dto.SummaryDto;
import com.project.json2sql.model.AuditOwnerDetails;
import com.project.json2sql.model.AuditProperties;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OffsetBasedPageRequest;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.OwnerProcess;
import com.project.json2sql.model.ProcessJson;
import com.project.json2sql.model.ProcessScheduleJson;
import com.project.json2sql.model.Properties;
import com.project.json2sql.model.Summary;
import com.project.json2sql.repository.AuditOwnerDetailsRepository;
import com.project.json2sql.repository.AuditPropertiesRepository;
import com.project.json2sql.repository.ConfigPropertiesRepository;
import com.project.json2sql.repository.OwnerDetailsRepository;
import com.project.json2sql.repository.OwnerProcessRepository;
import com.project.json2sql.repository.ProcessRepository;
import com.project.json2sql.repository.PropertiesJpaRepository;
import com.project.json2sql.repository.PropertiesRepository;
import com.project.json2sql.repository.SchedulerRepository;
import com.project.json2sql.repository.SummaryRepository;
import com.project.json2sql.service.ProcessService;
import com.project.json2sql.tasks.ExecuteProxyTask;
import com.project.json2sql.util.DateUtil;
import com.project.json2sql.util.ProxyCall;

@Service
public class ProcessServiceImpl implements ProcessService {

	private static final int THREAD_POOL_VALUE = 1;

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

	@Autowired
	SchedulerRepository schedulerRepository;

	@Autowired
	AuditOwnerDetailsRepository auditOwnerDetailsRepository;

	@Autowired
	AuditPropertiesRepository auditPropertiesRepository;

	@Autowired
	OwnerProcessRepository ownerProcessRepository;

	private ExecutorService service;

	public static final Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);

	public String startProcess(MainJson jsonObj) {
		String isProcess = "N";
		try {
			ProcessJson processJson = new ProcessJson();
			String path = writeInFile(jsonObj.toString(), localUploadPath);
			logger.info("Stored File Path::" + path);
			processJson.setFilePath(path);
			processJson.setStatus(jsonObj.getResponse().getStatus());
			processJson.setCreatedDate(DateUtil.getCurrentDateTime());
			processJson.setCreatedBy("System");
			processJson = processRepository.save(processJson);
			if (processJson.getProcessId() != 0) {
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
						propertiesObj.setProcessId(processJson.getProcessId());
						propertiesObj.setIsActive("Y");

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
						propertiesObj.setProcessId(processJson.getProcessId());
						propertiesObj.setIsActive("Y");

						propertiesObj = propertiesRepository.save(propertiesObj);

						PropertiesListObj.add(propertiesObj);
					}
				}
				isProcess = "Y";
			}
		} catch (Exception e) {
			isProcess = "N";
			logger.error("File not Saved Properly::: " + e);
		}
		return isProcess;
	}

	public static String fileSuffix() {
		String fileSuffix = new SimpleDateFormat("ddMMyyyyHHmmss").format(new Date());
		return fileSuffix;
	}

	public static String writeInFile(String Value, String localUploadPath) {
		String uuid = UUID.randomUUID().toString();
		String path = localUploadPath + uuid + fileSuffix() + ".json";
		logger.info("Stored Path:: " + path);
		try {
			Files.write(Paths.get(path), Value.getBytes());
		} catch (Exception e) {
			logger.error("File can't Write:" + e);
		}
		return path;
	}

	@Override
	public OwnerDetails startProxyProcess(InputProxyDto inputObj) {
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		AuditOwnerDetails auditOwnerDetails = new AuditOwnerDetails();
		ConfigProperties configPropertiesObj = configPropertiesRepository.fetchById(1);
		try {
			if (null != configPropertiesObj) {
				Root rootObj = ProxyCall.callProxy(configPropertiesObj, inputObj);

				if (rootObj != null) {
					List<OwnerDetails> ownerDetailsListOldObj = ownerDetailsRepository.fetchById(inputObj.getId() + "");
					if (ownerDetailsListOldObj.size() > 0) {
						// Audit table transfer
						auditOwnerDetails.setFirstName(ownerDetailsListOldObj.get(0).getFirstName());
						auditOwnerDetails.setLastName(ownerDetailsListOldObj.get(0).getLastName());
						auditOwnerDetails.setOwnerAddress(ownerDetailsListOldObj.get(0).getOwnerAddress());
						auditOwnerDetails.setOwnerName(ownerDetailsListOldObj.get(0).getOwnerName());
						auditOwnerDetails.setCompanyName(ownerDetailsListOldObj.get(0).getCompanyName());
						auditOwnerDetails.setId(ownerDetailsListOldObj.get(0).getId());
						auditOwnerDetails.setCreatedBy(ownerDetailsListOldObj.get(0).getCreatedBy());
						auditOwnerDetails.setCreatedDate(ownerDetailsListOldObj.get(0).getCreatedDate());
						auditOwnerDetails.setIsActive("Y");
						auditOwnerDetailsRepository.updateStatus(ownerDetailsListOldObj.get(0).getId().toString(), "N");
						auditOwnerDetailsRepository.save(auditOwnerDetails);

						ownerDetailsObj.setOwnerId(ownerDetailsListOldObj.get(0).getOwnerId());
						ownerDetailsObj.setFirstName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getFirstName());
						ownerDetailsObj
								.setLastName(rootObj.getResponse().getResult().getOwnerDetails().get(0).getLastName());
						ownerDetailsObj.setOwnerAddress(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerAddress());
						ownerDetailsObj.setOwnerName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerName());
						ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
						ownerDetailsObj.setId(inputObj.getId().toString());
						ownerDetailsObj.setCreatedDate(DateUtil.getCurrentDateTime());
						ownerDetailsObj.setCreatedBy("System");
						ownerDetailsObj.setIsActive("Y");
						ownerDetailsRepository.save(ownerDetailsObj);
					} else {
						ownerDetailsObj.setFirstName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getFirstName());
						ownerDetailsObj
								.setLastName(rootObj.getResponse().getResult().getOwnerDetails().get(0).getLastName());
						ownerDetailsObj.setOwnerAddress(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerAddress());
						ownerDetailsObj.setOwnerName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerName());
						ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
						ownerDetailsObj.setId(inputObj.getId().toString());
						ownerDetailsObj.setCreatedDate(DateUtil.getCurrentDateTime());
						ownerDetailsObj.setCreatedBy("System");
						ownerDetailsObj.setIsActive("Y");
						ownerDetailsRepository.save(ownerDetailsObj);
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error in Proxy Service" + e);
		}
		return ownerDetailsObj;
	}

	@Override
	public List<Properties> fetchProperties() {
		List<Properties> propObj = new ArrayList<>();
		try {
			propObj = (List<Properties>) propertiesRepository.findAll();
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service" + e);
		}
		return propObj;
	}

	@Override
	public List<Properties> getPropertiesById(String id) {
		List<Properties> propObj = new ArrayList<>();
		try {
			propObj = (List<Properties>) propertiesRepository.fetchById(id);
		} catch (Exception e) {
			logger.error("Error in Property Upload Service" + e);
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
			// propObj = (List<Properties>) propertiesRepository.getPropertiesPager(offset);
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service" + e);
		}
		return propObj;
	}

	@Override
	public List<Properties> getAllProperties(int limit, int offset) {
		List<Properties> propObj = new ArrayList<>();
		try {
			logger.info("Get all Properties with limit {} and offset {}", limit, offset);
			Pageable pageable = new OffsetBasedPageRequest(limit, offset);
			propObj = propertiesJpaRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service" + e);
		}

		return propObj;
	}

	@Override
	public ConfigProperties fetchConfigProp() {
		ConfigProperties cofigObj = new ConfigProperties();
		try {
			cofigObj = configPropertiesRepository.fetchById(1);
		} catch (Exception e) {
			logger.error("Error in Config prop Fetch Service" + e);
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
			logger.error("Error in Config prop Save Service" + e);
		}
		return cofigPropObj;
	}

	@Override
	public List<ProcessScheduleJson> getAllFileList(int pageLimit, int offset) {
		List<ProcessScheduleJson> fileObjList = new ArrayList<>();
		try {
			logger.info("Get all FIles with limit {} and offset {}", pageLimit, offset);
			Pageable pageable = new OffsetBasedPageRequest(pageLimit, offset);
			fileObjList = schedulerRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service" + e);
		}
		return fileObjList;
	}

	@Override
	public List<AuditProperties> getAllAuditProperties(int pageLimit, int offset) {
		List<AuditProperties> propObj = new ArrayList<>();
		try {
			logger.info("Get all Properties with limit {} and offset {}", pageLimit, offset);
			Pageable pageable = new OffsetBasedPageRequest(pageLimit, offset);
			propObj = auditPropertiesRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service" + e);
		}
		return propObj;
	}

	@Override
	public List<AuditProperties> getAuditPropertiesById(String id) {
		List<AuditProperties> propObj = new ArrayList<>();
		try {
			propObj = (List<AuditProperties>) auditPropertiesRepository.fetchById(id, "Y");
		} catch (Exception e) {
			logger.error("Error in Property Upload Service" + e);
		}
		return propObj;
	}

	@Override
	public List<OwnerDetails> getAllOwners(int pageLimit, int offset) {
		List<OwnerDetails> ownerObjList = new ArrayList<>();
		try {
			logger.info("Get all Owners with limit {} and offset {}", pageLimit, offset);
			Pageable pageable = new OffsetBasedPageRequest(pageLimit, offset);
			ownerObjList = ownerDetailsRepository.findAll(pageable).getContent();
		} catch (Exception e) {
			logger.error("Error in Property Fetch Service" + e);
		}
		return ownerObjList;
	}

	@Override
	public List<OwnerDetails> getOwnerById(String id) {
		List<OwnerDetails> ownerObjList = new ArrayList<>();
		try {
			ownerObjList = (List<OwnerDetails>) ownerDetailsRepository.fetchById(id);
		} catch (Exception e) {
			logger.error("Error in Property Upload Service" + e);
		}
		return ownerObjList;
	}

	@Override
	public Map<Object, ValueDifference<Object>> getDiff(String id) {
		ObjectMapper mapper = new ObjectMapper();
		List<AuditProperties> auditpropObj = (List<AuditProperties>) auditPropertiesRepository.fetchById(id, "Y");
		List<Properties> propObj = getPropertiesById(id);
		Map<Object, ValueDifference<Object>> diffObj = null;
		try {
			String json1 = mapper.writeValueAsString(auditpropObj.get(0));
			String json2 = mapper.writeValueAsString(propObj.get(0));
			Gson g = new Gson();
			Type mapType = new TypeToken<Map<String, Object>>() {
			}.getType();
			Map<String, Object> firstMap = g.fromJson(json1, mapType);
			Map<String, Object> secondMap = g.fromJson(json2, mapType);
			MapDifference<Object, Object> objMap = Maps.difference(firstMap, secondMap);
			diffObj = objMap.entriesDiffering();
			System.out.println(Maps.difference(firstMap, secondMap));
			System.out.println(diffObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return diffObj;
	}

	@Override
	public int getOwnerCount() {
		int count = ownerDetailsRepository.getOwnerCount();
		return count;
	}

	@Override
	public String getExecuteProxy(ConfigurationDto configDtoObj) {
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		AuditOwnerDetails auditOwnerDetails = new AuditOwnerDetails();
		// ConfigProperties configPropertiesObj =
		// configPropertiesRepository.fetchById(1);
		String status = "N";
		try {
			if (null != configDtoObj) {
				// Set input object
				List<Properties> propObjList = propertiesJpaRepository.findAll();
				if (propObjList.size() > 0) {
					for (Properties propObj : propObjList) {
						InputProxyDto inputObj = new InputProxyDto();
						OwnerProcess ownerProcess = new OwnerProcess();

						inputObj.setOp(configDtoObj.getOp());
						inputObj.setSid(configDtoObj.getSid());
						inputObj.setUid(configDtoObj.getUid());
						inputObj.setLoc("AU");
						inputObj.setAppCode("rppandroid");
						inputObj.setId(Integer.parseInt(propObj.getId()));

						ownerProcess.setId(propObj.getId());
						ownerProcess.setCreatedDate(DateUtil.getCurrentDateTime());
						ownerProcess.setCreatedBy("System");
						ownerProcess.setIsProcess("N");

						Root rootObj = callProxy(configDtoObj, inputObj, ownerProcess);

						if (rootObj != null) {
							List<OwnerDetails> ownerDetailsListOldObj = ownerDetailsRepository
									.fetchById(inputObj.getId() + "");
							if (ownerDetailsListOldObj.size() > 0) {
								// Audit table transfer
								auditOwnerDetails.setFirstName(ownerDetailsListOldObj.get(0).getFirstName());
								auditOwnerDetails.setLastName(ownerDetailsListOldObj.get(0).getLastName());
								auditOwnerDetails.setOwnerAddress(ownerDetailsListOldObj.get(0).getOwnerAddress());
								auditOwnerDetails.setOwnerName(ownerDetailsListOldObj.get(0).getOwnerName());
								auditOwnerDetails.setCompanyName(ownerDetailsListOldObj.get(0).getCompanyName());
								auditOwnerDetails.setId(ownerDetailsListOldObj.get(0).getId());
								auditOwnerDetails.setCreatedBy(ownerDetailsListOldObj.get(0).getCreatedBy());
								auditOwnerDetails.setCreatedDate(ownerDetailsListOldObj.get(0).getCreatedDate());
								auditOwnerDetails.setIsActive("Y");
								auditOwnerDetailsRepository
										.updateStatus(ownerDetailsListOldObj.get(0).getId().toString(), "N");
								auditOwnerDetailsRepository.save(auditOwnerDetails);

								ownerDetailsObj.setOwnerId(ownerDetailsListOldObj.get(0).getOwnerId());
								ownerDetailsObj.setFirstName(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getFirstName());
								ownerDetailsObj.setLastName(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getLastName());
								ownerDetailsObj.setOwnerAddress(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerAddress());
								ownerDetailsObj.setOwnerName(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerName());
								ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
								ownerDetailsObj.setId(inputObj.getId().toString());
								ownerDetailsObj.setCreatedDate(DateUtil.getCurrentDateTime());
								ownerDetailsObj.setCreatedBy("System");
								ownerDetailsObj.setIsActive("Y");
								ownerDetailsRepository.save(ownerDetailsObj);
								status = "Y";

							} else {
								ownerDetailsObj.setFirstName(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getFirstName());
								ownerDetailsObj.setLastName(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getLastName());
								ownerDetailsObj.setOwnerAddress(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerAddress());
								ownerDetailsObj.setOwnerName(
										rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerName());
								ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
								ownerDetailsObj.setId(inputObj.getId().toString());
								ownerDetailsObj.setCreatedDate(DateUtil.getCurrentDateTime());
								ownerDetailsObj.setCreatedBy("System");
								ownerDetailsObj.setIsActive("Y");
								ownerDetailsRepository.save(ownerDetailsObj);
								status = "Y";
							}
						}
					}
				}
			}

		} catch (Exception e) {
			status = "N";
			logger.error("Error in Proxy Service" + e);
		}
		return status;
	}

	public Root callProxy(ConfigurationDto configDtoObj, InputProxyDto inputObj, OwnerProcess ownerProcess)
			throws Exception {
		Root rootObj = new Root();
		URL u = new URL(configDtoObj.getUrl());
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		OwnerProcess ownerProcessObj = ownerProcess;
		try {
			/*
			 * Properties systemSettings = System.getProperties();
			 * systemSettings.put("proxySet", "true"); systemSettings.put("http.proxyHost",
			 * configPropertiesObj.getHost()); systemSettings.put("http.proxyPort",
			 * configPropertiesObj.getPort());
			 */

			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Authorization", configDtoObj.getAuthorization());
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Cache-Control", "no-cache");
			// conn.setRequestProperty( "User-Agent", "");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);

			// conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			OutputStream out = conn.getOutputStream();
			// out.write(inputObj.toString().getBytes());
			out.write(inputObj.toString().getBytes("UTF-8"));
			out.close();

			// read the response
			/*
			 * InputStream ip = conn.getInputStream(); BufferedReader br1 = new
			 * BufferedReader(new InputStreamReader(ip));
			 */

			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");
			/*
			 * JSONParser parser = new JSONParser(); JSONObject json = (JSONObject) parser.
			 * parse(result);
			 */

			Gson g = new Gson();
			rootObj = g.fromJson(result, Root.class);

			// JSONObject jsonObject = new JSONObject(result);

			System.out.println(conn.getResponseCode() + " : " + conn.getResponseMessage());
			System.out.println(conn.getResponseCode() == HttpURLConnection.HTTP_OK);
			ownerProcessObj.setProcessCode(conn.getResponseCode());
			ownerProcessObj.setProcessError(conn.getResponseMessage());

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			System.out.println("Using proxy:" + conn.usingProxy());
			ownerProcessObj.setUsingProxy(conn.usingProxy());

			ownerProcessRepository.save(ownerProcessObj);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(false);
		} finally {
			conn.disconnect();
		}

		return rootObj;
	}

	@Override
	public void multiThreadExecuteProxy(ConfigurationDto configDtoObj) throws Exception {
		try {
			service = Executors.newFixedThreadPool(THREAD_POOL_VALUE);
			List<Properties> propObjList = propertiesJpaRepository.findAll();
			List<Future<String>> resultList = null;
			List<ExecuteProxyTask> taskList = new ArrayList<ExecuteProxyTask>();
			createTaskListProcess(configDtoObj, propObjList, taskList);
			resultList = service.invokeAll(taskList);
		} catch (Exception e) {
			logger.error("Error in multiThreadExecuteProxy Service" + e);
		} finally {
			service.shutdown();
			service.awaitTermination(1, TimeUnit.SECONDS);
		}
	}

	private void createTaskListProcess(ConfigurationDto configDtoObj, List<Properties> propObjList,
			List<ExecuteProxyTask> taskList) {
		for (Properties propObj : propObjList) {
			ExecuteProxyTask proxyTask = new ExecuteProxyTask(configDtoObj, propObj);
			taskList.add(proxyTask);
		}
	}

	@Override
	public void stopMultiThreadExecuteProxy(ConfigurationDto configDtoObj) throws InterruptedException {
		if (null != service && !service.isShutdown()) {
			service.shutdown();
			service.awaitTermination(1, TimeUnit.SECONDS);
		}

	}

}
