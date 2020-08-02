package com.project.json2sql.service;

import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.model.OwnerDetails;


public interface ProcessService {

	String startProcess(MainJson jsonObj);

	OwnerDetails startProxyProcess(InputProxyDto inputObj);

}
