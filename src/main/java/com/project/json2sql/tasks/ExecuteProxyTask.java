package com.project.json2sql.tasks;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.json2sql.helper.ProcessServiceHelper;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerProcess;
import com.project.json2sql.model.Properties;
import com.project.json2sql.repository.OwnerDetailsRepository;
import com.project.json2sql.repository.OwnerProcessRepository;
import com.project.json2sql.util.DateUtil;

public class ExecuteProxyTask implements Callable<String> {

	private final ConfigProperties configDtoObj;
	private final Properties propObj;
	private final ProcessServiceHelper processServiceHelper;
	
	private OwnerProcessRepository ownerProcessRepository;

	public static final Logger logger = LoggerFactory.getLogger(ExecuteProxyTask.class);

	public ExecuteProxyTask(ConfigProperties configDtoObj, Properties propObj,
			ProcessServiceHelper processServiceHelper) {
		super();
		this.configDtoObj = configDtoObj;
		this.propObj = propObj;
		this.processServiceHelper = processServiceHelper;
	}

	@Override
	public String call() throws Exception {
		Thread.sleep(Long.parseLong(configDtoObj.getFrequency()));
		logger.info("Thread is started");
		String status = "N";
		OwnerProcess ownerProcessObj = new OwnerProcess();
		ownerProcessObj.setCreatedBy("System");
		ownerProcessObj.setIsProcess(status);
		ownerProcessObj.setId(propObj.getId());
		ownerProcessObj.setCreatedDate(DateUtil.getCurrentDateTime());
		try {
			if (null != this.configDtoObj) {
				status = processServiceHelper.doExecuteProxy(propObj, configDtoObj, status);
				ownerProcessObj.setIsProcess(status);
			}
		} catch (Exception e) {
			status = "N";
			ownerProcessObj.setIsProcess(status);
			ownerProcessObj.setProcessError(e.getMessage());
			processServiceHelper.saveFailedData(ownerProcessObj);
			logger.error("Error in Proxy Service " + e.getMessage());
		}
		return status;
	}

}
