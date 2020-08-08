package com.project.json2sql.controller;

import java.io.InputStreamReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.service.ProcessService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController

@RequestMapping("/json2sql")
public class OwnerController {
	
	public static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

	@Autowired
	ProcessService processService;
	
	@GetMapping("/configDetails")
	@ResponseBody
	public ResponseEntity<?> configDetails() {
		ConfigProperties cofigObj = new ConfigProperties();
		try {
			cofigObj = processService.fetchConfigProp();
			return ResponseEntity.status(HttpStatus.OK).body(cofigObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/setConfigDetails")
	@ResponseBody
	public ResponseEntity<?> setConfigDetails(@RequestBody ConfigProperties configObj) {
		ConfigProperties cofigObj = new ConfigProperties();
		try {
			cofigObj = processService.setConfigDetails(configObj);
			return ResponseEntity.status(HttpStatus.OK).body(cofigObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/uploadOwnerFile")
	public ResponseEntity<?> uploadOwnerFile(@RequestParam("file") MultipartFile file) {
		JSONParser parser = new JSONParser();
		ObjectMapper objectMapper = new ObjectMapper();
        JSONObject obj;
        String isProcess = "N";
        OwnerDetails ownerDetailsObj = new OwnerDetails();
		try {
			logger.info("Owner Xml processed");
			obj = (JSONObject )parser.parse(new InputStreamReader(file.getInputStream(), "UTF-8"));
			if(obj != null) {
				InputProxyDto jsonObj = objectMapper.readValue(obj.toString(), InputProxyDto.class); 
				ownerDetailsObj = processService.startProxyProcess(jsonObj);
			}
			if(null != ownerDetailsObj)
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Success\"}");
			else
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Failure\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while File Uplaod");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@ApiOperation(value = "Store Proxy Data from JSON to SQL")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully Stored"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	}
	)
	@PostMapping("/getProxyDataStoreSql")
	@ResponseBody
	public ResponseEntity<?> getProxyData(@RequestBody InputProxyDto inputObj) {
		logger.info("Process Start");
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		try {
			ownerDetailsObj = processService.startProxyProcess(inputObj);
			return ResponseEntity.status(HttpStatus.OK).body(ownerDetailsObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
}
