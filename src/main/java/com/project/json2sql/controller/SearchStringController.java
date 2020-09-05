package com.project.json2sql.controller;

import java.util.ArrayList;
import java.util.List;

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

import com.project.json2sql.dto.ConfigProxyRequestDto;
import com.project.json2sql.dto.ConfigurationDto;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.PageSkipTransaction;
import com.project.json2sql.model.PageTransaction;
import com.project.json2sql.model.SearchString;
import com.project.json2sql.model.SearchStringFile;
import com.project.json2sql.service.SearchStringService;

import io.swagger.annotations.Api;

@RestController

@RequestMapping("/searchString")
@Api(value="searchString", description="Operations pertaining to JSON in SQL")
public class SearchStringController {

	@Value("${pageLimit}")
    private int pageLimit;
	
	public static final Logger logger = LoggerFactory.getLogger(SearchStringController.class);
	
	@Autowired
	SearchStringService searchStringService;
	
	@PostMapping("/uploadFile")
	public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String status = "N";
		try {
			status = searchStringService.uploadFile(file);
			if(status == "Y")
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"File Uploaded\"}");
			else
				return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\"File Not Uploaded\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while File Uplaod");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	

	@GetMapping("/getUploadedStringFileByLimit/{offset}")
	@ResponseBody
	public ResponseEntity<?> getUploadedStringFileByLimit(@PathVariable int offset) {
		List<SearchStringFile> searchStringFileListObj = new ArrayList<SearchStringFile>();
		try {
			searchStringFileListObj = searchStringService.getSearchString(10, offset);
			return ResponseEntity.status(HttpStatus.OK).body(searchStringFileListObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/getSearchStringByLimit/{offset}")
	@ResponseBody
	public ResponseEntity<?> getSearchStringByLimit(@PathVariable int offset) {
		List<SearchString> searchStringListObj = new ArrayList<SearchString>();
		try {
			searchStringListObj = searchStringService.getSearchStringList(10, offset);
			return ResponseEntity.status(HttpStatus.OK).body(searchStringListObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/getSearchStringById/{id}")
	@ResponseBody
	public ResponseEntity<?> getSearchStringById(@PathVariable String id) {
		List<SearchString> stringObj = new ArrayList<>();
		try {
			stringObj = searchStringService.getSearchStringById(id);
			return ResponseEntity.status(HttpStatus.OK).body(stringObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/countSearchString")
	@ResponseBody
	public ResponseEntity<?> countSearchString() {
		int count = 0;
		try {
			count = searchStringService.countSearchString();
			return ResponseEntity.status(HttpStatus.OK).body(count);
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
			cofigObj = searchStringService.setConfigDetails(configDtoObj);
			return ResponseEntity.status(HttpStatus.OK).body(cofigObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/configDetails")
	@ResponseBody
	public ResponseEntity<?> configDetails() {
		ConfigProperties cofigObj = new ConfigProperties();
		try {
			cofigObj = searchStringService.fetchConfigProp();
			return ResponseEntity.status(HttpStatus.OK).body(cofigObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/testProxyConfig")
	@ResponseBody
	public ResponseEntity<?> testProxyConfig(@RequestBody ConfigurationDto configDtoObj) {
		logger.info("testProxyConfig Start");
		String responceCode = null;
		try {
			responceCode = searchStringService.testProxy(configDtoObj);
			return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\""+responceCode+"\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while startProxy");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/startExecuteProxyString")
	@ResponseBody
	public ResponseEntity<?> startExecuteProxyString(@RequestBody ConfigProxyRequestDto configProxyRequestDto) {
		logger.info("startExecuteProxy String Start");
		try {
			searchStringService.multiThreadExecuteProxy(configProxyRequestDto);
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception ex) {
			logger.error("Error Occured while startExecuteProxy String");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/stopExecuteProxy")
	@ResponseBody
	public ResponseEntity<?> stopExecuteProxy() {
		logger.info("stopExecuteProxy Start");
		try {
			searchStringService.stopMultiThreadExecuteProxy();
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception ex) {
			logger.error("Error Occured while stopExecuteProxy");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	@GetMapping("/skipSearchString")
	@ResponseBody
	public ResponseEntity<?> skipSearchString() {
		List<PageSkipTransaction> PageSkipTransactionListObj = new ArrayList<>();
		try {
			PageSkipTransactionListObj = searchStringService.skipSearchString();
			return ResponseEntity.status(HttpStatus.OK).body(PageSkipTransactionListObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/addSkipSearchString")
	@ResponseBody
	public ResponseEntity<?> addSkipSearchString(@RequestBody PageSkipTransaction PageSkipTransaction) {
		List<PageSkipTransaction> PageSkipTransactionListObj = new ArrayList<>();
		try {
			PageSkipTransactionListObj = searchStringService.addSkipSearchString(PageSkipTransaction);
			return ResponseEntity.status(HttpStatus.OK).body(PageSkipTransactionListObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/deleteSkipSearchString")
	@ResponseBody
	public ResponseEntity<?> deleteSkipSearchString(@RequestBody PageSkipTransaction PageSkipTransaction) {
		List<PageSkipTransaction> PageSkipTransactionListObj = new ArrayList<>();
		try {
			PageSkipTransactionListObj = searchStringService.deleteSkipSearchString(PageSkipTransaction);
			return ResponseEntity.status(HttpStatus.OK).body(PageSkipTransactionListObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/processTransactionDetails/{offset}")
	@ResponseBody
	public ResponseEntity<?> processTransactionDetails(@PathVariable int offset) {
		List<PageTransaction> pageTransactionListObj = new ArrayList<>();
		try {
			pageTransactionListObj = searchStringService.processTransactionDetails(10, offset);
			return ResponseEntity.status(HttpStatus.OK).body(pageTransactionListObj);
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@GetMapping("/countTransactionDetails")
	@ResponseBody
	public ResponseEntity<?> countTransactionDetails() {
		int count = 0;
		try {
			count = searchStringService.countTransactionDetails();
			return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\""+count+"\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while Fetch");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/startReRunProxyString")
	@ResponseBody
	public ResponseEntity<?> startReRunProxyString(@RequestBody ConfigProxyRequestDto configProxyRequestDto) {
		logger.info("startExecuteProxy String Start");
		String status = null;
		try {
			status = searchStringService.startReRunProxyString(configProxyRequestDto);
			return ResponseEntity.status(HttpStatus.OK).body("{\"status\":\""+status+"\"}");
		} catch (Exception ex) {
			logger.error("Error Occured while startExecuteProxy String");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
	
	@PostMapping("/runFailedProcess")
	@ResponseBody
	public ResponseEntity<?> runFailedProcess() {
		logger.info("Failed Process run Start");
		try {
			searchStringService.multiThreadFailedRunProxy();
			return ResponseEntity.status(HttpStatus.OK).build();
		} catch (Exception ex) {
			logger.error("Error Occured while startExecuteProxy String");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"status\":\"Failure\"}");
		}
	}
}
