package com.project.json2sql.service;

import java.util.List;

import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.model.AuditProperties;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.ProcessScheduleJson;
import com.project.json2sql.model.Properties;


public interface ProcessService {

	String startProcess(MainJson jsonObj);

	OwnerDetails startProxyProcess(InputProxyDto inputObj);

	List<Properties> fetchProperties();

	List<Properties> getPropertiesById(String id);

	int getPropertiesCount();

	List<Properties> getPropertiesPager(String offset);

	List<Properties> getAllProperties(int limit, int offset);

	ConfigProperties fetchConfigProp();

	ConfigProperties setConfigDetails(ConfigProperties configObj);

	List<ProcessScheduleJson> getAllFileList(int pageLimit, int offset);

	List<AuditProperties> getAllAuditProperties(int pageLimit, int offset);

	List<AuditProperties> getAuditPropertiesById(String id);

}
