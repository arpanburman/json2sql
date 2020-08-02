package com.project.json2sql.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.service.ProcessService;

@RestController
public class MainController {
	
	public static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	ProcessService processService;

	@PostMapping("/getProcess")
	@ResponseBody
	public ResponseEntity<?> getProcess(@RequestBody MainJson jsonObj) {
		logger.info("Process Start");
		String isProcess = "N";
		try {
			isProcess = processService.startProcess(jsonObj);
			if(isProcess == "Y")
				return ResponseEntity.status(HttpStatus.OK).body("{'status':'Success'}");
			else
				return ResponseEntity.status(HttpStatus.OK).body("{'status':'Failure'}");
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'status':'Failure'}");
		}
	}
	
	@PostMapping("/getProxyData")
	@ResponseBody
	public ResponseEntity<?> getProxyData(@RequestBody InputProxyDto inputObj) {
		logger.info("Process Start");
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		try {
			ownerDetailsObj = processService.startProxyProcess(inputObj);
			return ResponseEntity.status(HttpStatus.OK).body(ownerDetailsObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{'status':'Failure'}");
		}
	}
	
	@GetMapping("/hello")
	@ResponseBody
	public String hello() {
		logger.info("Process Start");
		return "Hello World";
	}
}
