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

import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.service.ProcessService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController

@RequestMapping("/json2sql")
@Api(value="json2sql", description="Operations pertaining to JSON in SQL")
public class MainController {
	
	public static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	ProcessService processService;


	@ApiOperation(value = "Store Properties from JSON to SQL")
	@ApiResponses(value = {
	        @ApiResponse(code = 200, message = "Successfully Stored"),
	        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
	        @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
	        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
	}
	)
	@PostMapping("/getJsonStoreSql")
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
