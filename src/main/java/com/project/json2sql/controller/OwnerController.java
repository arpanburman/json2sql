package com.project.json2sql.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.service.ProcessService;

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
}
