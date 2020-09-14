package com.project.json2sql.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.json2sql.dto.ConfigProxyRequestDto;
import com.project.json2sql.helper.ProcessServiceHelper;
import com.project.json2sql.model.AuditOwnerDetails;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.repository.ConfigPropertiesRepository;
import com.project.json2sql.repository.OwnerProcessRepository;
import com.project.json2sql.repository.PropertiesJpaRepository;
import com.project.json2sql.service.OwnerSchedulerService;
import com.project.json2sql.service.ProcessService;
import com.project.json2sql.service.SchedulerService;
import com.project.json2sql.service.SearchStringService;
import com.project.json2sql.tasks.ExecuteProxyTask;
import com.project.json2sql.util.DateUtil;
import com.project.json2sql.util.ServiceUtil;

@Component
@EnableAsync
public class Scheduler {

	@Autowired
	SchedulerService schedulerService;

	@Autowired
	OwnerSchedulerService ownerSchedulerService;

	@Autowired
	ConfigPropertiesRepository configPropertiesRepository;

	@Autowired
	OwnerProcessRepository ownerProcessRepository;

	@Autowired
	PropertiesJpaRepository propertiesJpaRepository;

	@Autowired
	ProcessServiceHelper processServiceHelper;
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	SearchStringService searchStringService;

	public static final Logger logger = LoggerFactory.getLogger(ExecuteProxyTask.class);

	//@Async
	//@Scheduled(cron = "0 0/1 * * * ?")
	public void cronJobSch() {
		System.out.println("Properties cron job expression:: " + DateUtil.getCurrentDateTime());
		schedulerService.processJsonUploadJob();
	}


	//@Scheduled(cron = "0/1 * 10-17 * * ?")
	@Async
	@Scheduled(cron = "0 0/1 * * * ?")
	public void cronJobExecuteProxy() throws InterruptedException {
		System.out.println("Execute proxy job expression:: " + DateUtil.getCurrentDateTime());
		
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		AuditOwnerDetails auditOwnerDetails = new AuditOwnerDetails();
		ConfigProperties configPropertiesObj = new ConfigProperties();
		configPropertiesObj = configPropertiesRepository.fetchById(1);
		Map<String, Integer> time=ServiceUtil.currentHour();
		
		if(null != configPropertiesObj) {
			if(null !=configPropertiesObj.getStarttime() && null !=configPropertiesObj.getEndtime()) {
				if((time.get("hour")>=Integer.parseInt(configPropertiesObj.getStarttime()) && 
						time.get("hour")<=Integer.parseInt(configPropertiesObj.getEndtime()))){
					
					if(time.get("hour")==Integer.parseInt(configPropertiesObj.getEndtime()) &&
								(time.get("min")> 0)) {
						logger.info("Schedule and Job will Shut Down");
						processService.stopMultiThreadExecuteProxy();
					}else {
						try {
							logger.info("Schedule and Job will Start");
							processService.multiThreadExecuteProxy();
						} catch (Exception e) {
							logger.error("Error: in Proxy Service" + e);
						}
					}
				}
			}else {
				logger.info("Not Scheduled");
			}
		}
	}
	
	//@Async
	//@Scheduled(cron = "0 0/1 * * * ?")
	public void searchStringProcess() {
		System.out.println("Search String cron job expression:: " + DateUtil.getCurrentDateTime());
		searchStringService.pageProcessSearchStringJob();
	}
	
	//@Async
	@Scheduled(cron = "0 0/1 * * * ?")
	public void cronJobExecuteSearchStringProxy() throws InterruptedException {
		System.out.println("Execute Search String proxy job expression:: " + DateUtil.getCurrentDateTime());
		
		ConfigProperties configPropertiesObj = new ConfigProperties();
		configPropertiesObj = configPropertiesRepository.fetchById(2);
		Map<String, Integer> time=ServiceUtil.currentHour();
		
		if(null != configPropertiesObj) {
			if(null !=configPropertiesObj.getStarttime() && null !=configPropertiesObj.getEndtime()) {
				if((time.get("hour")>=Integer.parseInt(configPropertiesObj.getStarttime()) && 
						time.get("hour")<=Integer.parseInt(configPropertiesObj.getEndtime()))){
					
					if(time.get("hour")==Integer.parseInt(configPropertiesObj.getEndtime()) &&
								(time.get("min")> 0)) {
						logger.info("Schedule and Job will Shut Down");
						searchStringService.stopMultiThreadExecuteProxy();
					}else {
						try {
							logger.info("Schedule and Job will Start");
							ConfigProxyRequestDto configProxyRequestDto = new ConfigProxyRequestDto();
							configProxyRequestDto.setAuthorization(configPropertiesObj.getAuthorization());
							configProxyRequestDto.setSid(configPropertiesObj.getSid());
							configProxyRequestDto.setUid(configPropertiesObj.getUid());
							configProxyRequestDto.setOp(configPropertiesObj.getOp());
							configProxyRequestDto.setUrl(configPropertiesObj.getUrl());
							configProxyRequestDto.setIp(configPropertiesObj.getIp());
							configProxyRequestDto.setPort(configPropertiesObj.getPort());
							configProxyRequestDto.setFrequency(configPropertiesObj.getFrequency());
							configProxyRequestDto.setLoc(configPropertiesObj.getLoc());
							configProxyRequestDto.setStarttime(configPropertiesObj.getStarttime());
							configProxyRequestDto.setEndtime(configPropertiesObj.getEndtime());
							configProxyRequestDto.setAppCode(configPropertiesObj.getAppcode());
							configProxyRequestDto.setMaxResult(configPropertiesObj.getMaxResult());
							
							searchStringService.multiThreadExecuteProxy(configProxyRequestDto);
						} catch (Exception e) {
							logger.error("Error: in Proxy Service" + e);
						}
					}
				}
			}else {
				logger.info("Not Scheduled");
			}
		}
	}
	
	//@Async
	@Scheduled(cron = "0 0/1 * * * ?")
	public void searchStringStopProcess() throws InterruptedException {
		System.out.println("Search String Stop Process:: " + DateUtil.getCurrentDateTime());
		Map<String, Integer> time=ServiceUtil.currentHour();
		ConfigProperties configPropertiesObj = configPropertiesRepository.fetchById(2);
		if(null !=configPropertiesObj.getEndtime()) {
			if(time.get("hour")==Integer.parseInt(configPropertiesObj.getEndtime()) &&
					(time.get("min")> 0)) {
				logger.info("Schedule Job will be Shut Down in ::::::"+configPropertiesObj.getEndtime());
				searchStringService.stopMultiThreadExecuteProxy();
			}
		}
	}

}
