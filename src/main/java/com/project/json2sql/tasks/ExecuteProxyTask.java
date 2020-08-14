package com.project.json2sql.tasks;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.json2sql.helper.ProcessServiceHelper;
import com.project.json2sql.model.AuditOwnerDetails;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.Properties;

public class ExecuteProxyTask implements Callable<String> {

	private final ConfigProperties configDtoObj;
	private final Properties propObj;

	@Autowired
	ProcessServiceHelper processServiceHelper;

	public static final Logger logger = LoggerFactory.getLogger(ExecuteProxyTask.class);

	public ExecuteProxyTask(ConfigProperties configDtoObj, Properties propObj) {
		super();
		this.configDtoObj = configDtoObj;
		this.propObj = propObj;
	}

	@Override
	public String call() throws Exception {
		Thread.sleep(Long.parseLong(configDtoObj.getTime()));
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		AuditOwnerDetails auditOwnerDetails = new AuditOwnerDetails();
		String status = "N";
		try {
			if (null != this.configDtoObj) {

				status = processServiceHelper.doExecuteProxy(this.propObj, this.configDtoObj, ownerDetailsObj,
						auditOwnerDetails, status);
			}

		} catch (Exception e) {
			status = "N";
			logger.error("Error in Proxy Service" + e);
		}
		return status;
	}

}
