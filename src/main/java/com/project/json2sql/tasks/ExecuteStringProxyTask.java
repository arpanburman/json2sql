package com.project.json2sql.tasks;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.project.json2sql.dto.ConfigProxyRequestDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.helper.SearchStringServiceHelper;

public class ExecuteStringProxyTask implements Callable<String>{
	
	public static final Logger logger = LoggerFactory.getLogger(ExecuteStringProxyTask.class);
	
	private final ConfigProxyRequestDto configProxyRequestDto;
	private final SearchStringServiceHelper searchStringServiceHelper;

	public ExecuteStringProxyTask(ConfigProxyRequestDto configProxyRequestDto,
			SearchStringServiceHelper searchStringServiceHelper) {
		super();
		this.configProxyRequestDto = configProxyRequestDto;
		this.searchStringServiceHelper = searchStringServiceHelper;
	}

	@Override
	public String call() throws Exception {
		String status = "N";
		status = searchStringServiceHelper.doExecuteProxy(configProxyRequestDto, status);
		return status;
	}

}
