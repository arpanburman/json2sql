package com.project.json2sql.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.model.ProcessScheduleJson;
import com.project.json2sql.repository.SchedulerRepository;
import com.project.json2sql.service.ProcessService;
import com.project.json2sql.service.SchedulerService;
import com.project.json2sql.util.DateUtil;
import com.project.json2sql.util.ServiceUtil;

@Service
public class SchedulerServiceImpl implements SchedulerService{
	
	@Value("${jsonPath}")
    private String jsonPath;
	
	@Value("${processJsonPath}")
    private String processJsonPath;
	
	@Autowired
	ProcessService processService;
	
	@Autowired
	SchedulerRepository schedulerRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

	@Override
	public Map<Object, Object> processJsonUploadJob() {
		Map<Object, Object> processedJob =new HashMap<Object, Object>();
		try {
			File folder = new File(jsonPath);
			File[] listOfFiles = folder.listFiles();

			for (File file : listOfFiles) {
			    if (file.isFile()) {
			        System.out.println(file.getPath());
			        this.fileProcess(file.getPath());
			    }
			}
			
		} catch (Exception e) {
			logger.error("Job Can't Process");
		}
		
		return processedJob;
	}

	private void fileProcess(String path) {
		ProcessScheduleJson processJsonObj = new ProcessScheduleJson();
		ObjectMapper objectMapper = new ObjectMapper();
		String isProcess = "N";
		String processPath = null;
		try {
			byte[] fileContent = Files.readAllBytes(Paths.get(path));
			String jsonData = new String(fileContent);
			if (!StringUtils.isEmpty(jsonData)) {
				MainJson jsonObj = objectMapper.readValue(jsonData.toString(), MainJson.class);
				isProcess = processService.startProcess(jsonObj);
				logger.info("Json Processed: "+isProcess);
				if(isProcess.equalsIgnoreCase("Y")) {
					processPath=moveAfterProcess(path);
				}
			}
		} catch (Exception e) {
			isProcess = "N";
			processJsonObj.setErrorLog("JSON Not valid: "+e.getMessage());
			logger.error("Can't Process"+e);
		}
		processJsonObj.setCreatedDate(DateUtil.getCurrentDateTime());
		processJsonObj.setFilePath(path);
		processJsonObj.setIsProcess(isProcess);
		processJsonObj.setProcessedDate(DateUtil.getCurrentDateTime());
		processJsonObj.setProcessedPath(processPath);
		schedulerRepository.save(processJsonObj);
	}

	private String moveAfterProcess(String path) {
		File file = new File(path); 
		String uuid =  UUID.randomUUID().toString();
        String processPath = processJsonPath+uuid+".json";
        // renaming the file and moving it to a new location 
        if(file.renameTo 
           (new File(processPath))) 
        { 
            file.delete(); 
            logger.info("File moved successfully"); 
            return processPath; 
        } 
        else
        { 
        	logger.info("Failed to move the file"); 
        	return processPath=null; 
        }
    } 
 
}
