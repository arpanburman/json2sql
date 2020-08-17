package com.project.json2sql.tasks;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.json2sql.helper.ProcessServiceHelper;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.Properties;

public class ExecuteProxyTask implements Callable<String> {

	private final ConfigProperties configDtoObj;
	private final Properties propObj;
	private final ProcessServiceHelper processServiceHelper;

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
		try {
			if (null != this.configDtoObj) {
				status = processServiceHelper.doExecuteProxy(propObj, configDtoObj, status);
			}
		} catch (Exception e) {
			status = "N";
			logger.error("Error in Proxy Service " + e.getMessage());
		}
		return status;
	}

}
