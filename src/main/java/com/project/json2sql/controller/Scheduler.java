package com.project.json2sql.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.project.json2sql.service.SchedulerService;
import com.project.json2sql.util.DateUtil;

@Component
public class Scheduler {

	@Autowired
	SchedulerService schedulerService;
	
	@Scheduled(cron="0 0/1 * * * ?")
	public void cronJobSch() {
      System.out.println("Java cron job expression:: " + DateUtil.getCurrentDateTime());
      schedulerService.processJsonUploadJob();
   }
}
