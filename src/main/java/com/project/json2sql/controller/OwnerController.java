package com.project.json2sql.controller;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.json2sql.dto.ConfigurationDto;
import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.OwnerProcess;
import com.project.json2sql.service.ProcessService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController

@RequestMapping("/json2sql")
public class OwnerController {

	public static final Logger logger = LoggerFactory.getLogger(OwnerController.class);

	@Value("${pageLimit}")
	private int pageLimit;

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
	public ResponseEntity<?> setConfigDetails(@RequestBody ConfigurationDto configDtoObj) {
		ConfigProperties cofigObj = new ConfigProperties();
		try {
			cofigObj = processService.setConfigDetails(configDtoObj);
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
			obj = (JSONObject) parser.parse(new InputStreamReader(file.getInputStream(), "UTF-8"));
			if (obj != null) {
				InputProxyDto jsonObj = objectMapper.readValue(obj.toString(), InputProxyDto.class);
				ownerDetailsObj = processService.startProxyProcess(jsonObj);
			}
			if (null != ownerDetailsObj)
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Success\"}");
			else
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Failure\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while File Uplaod");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@ApiOperation(value = "Store Proxy Data from JSON to SQL")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Successfully Stored"),
			@ApiResponse(code = 401, message = "You are not authorized to view the resource"),
			@ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
			@ApiResponse(code = 404, message = "The resource you were trying to reach is not found") })
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

	@GetMapping("/getOwnerByLimit/{offset}")
	@ResponseBody
	public ResponseEntity<?> getOwnerLimit(@PathVariable int offset) {
		List<OwnerDetails> OwnerObjList = new ArrayList<>();
		try {
			OwnerObjList = processService.getAllOwners(10, offset);
			return ResponseEntity.status(HttpStatus.OK).body(OwnerObjList);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@GetMapping("/getOwnerById/{id}")
	@ResponseBody
	public ResponseEntity<?> getOwnerById(@PathVariable String id) {
		List<OwnerDetails> OwnerObjList = new ArrayList<>();
		try {
			OwnerObjList = processService.getOwnerById(id);
			return ResponseEntity.status(HttpStatus.OK).body(OwnerObjList);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@GetMapping("/getOwnerCount")
	@ResponseBody
	public ResponseEntity<?> getOwnerCount() {
		int count = 0;
		try {
			count = processService.getOwnerCount();
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@PostMapping("/getExecuteProxy")
	@ResponseBody
	public ResponseEntity<?> getExecuteProxy() {
		logger.info("Process Start");
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		String status = "N";
		try {
			status = processService.getExecuteProxy();
			return ResponseEntity.status(HttpStatus.OK).body(ownerDetailsObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@PostMapping("/startExecuteProxy")
	@ResponseBody
	public ResponseEntity<?> startExecuteProxy() {
		logger.info("startExecuteProxy Start");
		try {
			processService.multiThreadExecuteProxy();
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception ex) {
			logger.error("Error Occured while startExecuteProxy");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@PostMapping("/stopExecuteProxy")
	@ResponseBody
	public ResponseEntity<?> stopExecuteProxy() {
		logger.info("stopExecuteProxy Start");
		try {
			processService.stopMultiThreadExecuteProxy();
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception ex) {
			logger.error("Error Occured while stopExecuteProxy");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@GetMapping("/addProcessID/{id}")
	@ResponseBody
	public ResponseEntity<?> addProcessID(@PathVariable String id) {
		logger.info("Add Process ID");
		String status = "Error: Property Id not Added";
		try {
			status = processService.addProcessID(id);
			return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"'"+status+"'\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}

	@GetMapping("/getProcessFailedData/{offset}")
	@ResponseBody
	public ResponseEntity<?> getProcessFailedData(@PathVariable int offset) {
		List<OwnerProcess> OwnerProcessObjList = new ArrayList<>();
		try {
			OwnerProcessObjList = processService.getProcessFailedData(10, offset);
			return ResponseEntity.status(HttpStatus.OK).body(OwnerProcessObjList);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/runFailedProcess")
	@ResponseBody
	public ResponseEntity<?> runFailedProcess() {
		logger.info("runFailedProcess Start");
		try {
			processService.multiThreadReRunFailedExecuteProxy();
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception ex) {
			logger.error("Error Occured while startExecuteProxy");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/testProxyConfig")
	@ResponseBody
	public ResponseEntity<?> testProxyConfig(@RequestBody ConfigurationDto configDtoObj) {
		logger.info("testProxyConfig Start");
		String responceCode = null;
		try {
			responceCode = processService.testProxy(configDtoObj);
			return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\""+responceCode+"\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while startProxy");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/removeProcessID/{id}")
	@ResponseBody
	public ResponseEntity<?> removeProcessID(@PathVariable String id) {
		logger.info("Remove Process ID:: "+id);
		String status = "Error: Property Id not Removed";
		try {
			status = processService.removeProcessID(id);
			return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"'"+status+"'\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
}
