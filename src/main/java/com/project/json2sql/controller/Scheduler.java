package com.project.json2sql.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.json2sql.helper.ProcessServiceHelper;
import com.project.json2sql.model.AuditOwnerDetails;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.OwnerProcess;
import com.project.json2sql.model.Properties;
import com.project.json2sql.repository.ConfigPropertiesRepository;
import com.project.json2sql.repository.OwnerProcessRepository;
import com.project.json2sql.repository.PropertiesJpaRepository;
import com.project.json2sql.service.OwnerSchedulerService;
import com.project.json2sql.service.ProcessService;
import com.project.json2sql.service.SchedulerService;
import com.project.json2sql.tasks.ExecuteProxyTask;
import com.project.json2sql.util.DateUtil;
import com.project.json2sql.util.ServiceUtil;

@Component
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

	public static final Logger logger = LoggerFactory.getLogger(ExecuteProxyTask.class);

	@Scheduled(cron = "0 0/1 * * * ?")
	public void cronJobSch() {
		System.out.println("Properties cron job expression:: " + DateUtil.getCurrentDateTime());
		schedulerService.processJsonUploadJob();
	}

	/*
	 * @Scheduled(cron="0/1 * 10-17 * * ?") public void cronJobSchOwner() {
	 * System.out.println("Owner cron job expression:: " +
	 * DateUtil.getCurrentDateTime()); ownerSchedulerService.ownerJsonUploadJob(); }
	 */

	//@Scheduled(cron = "0/1 * 10-17 * * ?")
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
		
		
		/*try {
			if (null != configPropertiesObj) {
				List<Properties> propObjList = propertiesJpaRepository.findAll();
				if (propObjList.size() > 0) {
					for (Properties propObj : propObjList) {
						//List<OwnerProcess> ownerObjList = ownerProcessRepository.fetchById(propObj.getId());
						//if (ownerObjList.size() == 0) {
							this.processServiceHelper.doExecuteProxy(propObj, configPropertiesObj, "cronJob");
						//}
					}
				}
			}

		} catch (Exception e) {
			logger.error("Error in Proxy Service" + e);
		}*/

	}

}
