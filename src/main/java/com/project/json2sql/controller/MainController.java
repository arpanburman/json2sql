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
import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.ProcessScheduleJson;
import com.project.json2sql.model.Properties;
import com.project.json2sql.service.ProcessService;
import com.project.json2sql.service.SchedulerService;
import com.project.json2sql.util.DateUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController

@RequestMapping("/json2sql")
@Api(value="json2sql", description="Operations pertaining to JSON in SQL")
public class MainController {
	
	@Value("${pageLimit}")
    private int pageLimit;
	
	public static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	SchedulerService schedulerService;


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
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Success\"}");
			else
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Failure\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/getProperties")
	@ResponseBody
	public ResponseEntity<?> fetchProperties() {
		List<Properties> propertiesObj = new ArrayList<Properties>();
		try {
			propertiesObj = processService.fetchProperties();
			return ResponseEntity.status(HttpStatus.OK).body(propertiesObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/getPropertiesPager/{offset}")
	@ResponseBody
	public ResponseEntity<?> getPropertiesPager(@PathVariable String offset) {
		List<Properties> propertiesObj = new ArrayList<Properties>();
		try {
			propertiesObj = processService.getPropertiesPager(offset);
			return ResponseEntity.status(HttpStatus.OK).body(propertiesObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/getPropertiesById/{id}")
	@ResponseBody
	public ResponseEntity<?> getPropertiesById(@PathVariable String id) {
		List<Properties> propertiesObj = new ArrayList<Properties>();
		try {
			propertiesObj = processService.getPropertiesById(id);
			return ResponseEntity.status(HttpStatus.OK).body(propertiesObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/getPropertiesCount")
	@ResponseBody
	public ResponseEntity<?> getPropertiesCount() {
		int count = 0;
		try {
			count = processService.getPropertiesCount();
			return ResponseEntity.status(HttpStatus.OK).body(count);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/uploadFile")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
		JSONParser parser = new JSONParser();
		ObjectMapper objectMapper = new ObjectMapper();
        JSONObject obj;
        String isProcess = "N";
		try {
			obj = (JSONObject )parser.parse(new InputStreamReader(file.getInputStream(), "UTF-8"));
			if(obj != null) {
				MainJson jsonObj = objectMapper.readValue(obj.toString(), MainJson.class); 
				isProcess = processService.startProcess(jsonObj);
			}
			if(isProcess == "Y")
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Success\"}");
			else
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"Failure\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while File Uplaod");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	
	
	@GetMapping("/getPropertiesByLimit/{offset}")
	@ResponseBody
	public ResponseEntity<?> getPropertiesByLimit(@PathVariable int offset) {
		List<Properties> propertiesObj = new ArrayList<Properties>();
		try {
			propertiesObj = processService.getAllProperties(pageLimit, offset);
			return ResponseEntity.status(HttpStatus.OK).body(propertiesObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/scheduleJson")
	@ResponseBody
	public void processJsonUploadJob() {
		 System.out.println("Java cron job expression:: " + DateUtil.getCurrentDateTime());
	     schedulerService.processJsonUploadJob();
	}
	
	@GetMapping("/getScheduleFileByLimit/{offset}")
	@ResponseBody
	public ResponseEntity<?> getScheduleFileByLimit(@PathVariable int offset) {
		List<ProcessScheduleJson> fileObjList = new ArrayList<ProcessScheduleJson>();
		try {
			fileObjList = processService.getAllFileList(10, offset);
			return ResponseEntity.status(HttpStatus.OK).body(fileObjList);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
}
