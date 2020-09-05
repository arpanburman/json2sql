package com.project.json2sql.helper;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.Root;
import com.project.json2sql.model.AuditOwnerDetails;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.OwnerProcess;
import com.project.json2sql.repository.AuditOwnerDetailsRepository;
import com.project.json2sql.repository.OwnerDetailsRepository;
import com.project.json2sql.repository.OwnerProcessRepository;
import com.project.json2sql.repository.PropertiesRepository;
import com.project.json2sql.util.DateUtil;

@Component
public class ProcessServiceHelper {

	@Autowired
	private AuditOwnerDetailsRepository auditOwnerDetailsRepository;

	@Autowired
	private OwnerDetailsRepository ownerDetailsRepository;

	@Autowired
	private OwnerProcessRepository ownerProcessRepository;
	
	@Autowired
	PropertiesRepository propertiesRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(ProcessServiceHelper.class);

	public String doExecuteProxy(com.project.json2sql.model.Properties propObj, ConfigProperties configDtoObj, String status) throws Exception {
		if ("cronJob".equals(status)) {
			Thread.sleep(Long.parseLong(configDtoObj.getFrequency()));
		}
		logger.info("Execution Start for ID:",propObj.getId());
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		AuditOwnerDetails auditOwnerDetails = new AuditOwnerDetails();
		InputProxyDto inputObj = new InputProxyDto();
		OwnerProcess ownerProcess = new OwnerProcess();
		int count = ownerProcessRepository.checkProcessedId(propObj.getId(), "Y");
		if(count==0) {
			logger.info("Processing for :: "+propObj.getId());
			inputObj.setOp(configDtoObj.getOp());
			inputObj.setSid(configDtoObj.getSid());
			inputObj.setUid(configDtoObj.getUid());
			inputObj.setLoc(configDtoObj.getLoc());
			inputObj.setAppCode(configDtoObj.getAppcode());
			inputObj.setId(Integer.parseInt(propObj.getId()));
			
			ownerProcess.setId(propObj.getId());
			ownerProcess.setCreatedDate(DateUtil.getCurrentDateTime());
			ownerProcess.setCreatedBy("System");
			ownerProcess.setIsProcess("N");
	
			Root rootObj = callProxy(configDtoObj, inputObj, ownerProcess);
			List<com.project.json2sql.dto.OwnerDetails> ownerDetailsListObj = rootObj.getResponse().getResult().getOwnerDetails();
			if (rootObj != null) {
				List<OwnerDetails> ownerDetailsListOldObj = ownerDetailsRepository.fetchById(inputObj.getId() + "");
				if (ownerDetailsListOldObj.size() > 0) {
					for(OwnerDetails ownerObj : ownerDetailsListOldObj) {
						// Audit table transfer
						auditOwnerDetails = new AuditOwnerDetails();
						auditOwnerDetails.setFirstName(ownerObj.getFirstName());
						auditOwnerDetails.setLastName(ownerObj.getLastName());
						auditOwnerDetails.setOwnerAddress(ownerObj.getOwnerAddress());
						auditOwnerDetails.setOwnerName(ownerObj.getOwnerName());
						auditOwnerDetails.setCompanyName(ownerObj.getCompanyName());
						auditOwnerDetails.setId(ownerObj.getId());
						auditOwnerDetails.setCreatedBy(ownerObj.getCreatedBy());
						auditOwnerDetails.setCreatedDate(ownerObj.getCreatedDate());
						auditOwnerDetails.setIsActive("Y");
						auditOwnerDetailsRepository.updateStatus(ownerObj.getId().toString(), "N");
						auditOwnerDetailsRepository.save(auditOwnerDetails);
					}
				ownerDetailsRepository.deleteOldOwnerDetails(inputObj.getId()+"");
					for(com.project.json2sql.dto.OwnerDetails ownerObj : ownerDetailsListObj) {
						ownerDetailsObj = new OwnerDetails();
						ownerDetailsObj = new OwnerDetails();
						ownerDetailsObj.setFirstName(ownerObj.getFirstName());
						ownerDetailsObj.setLastName(ownerObj.getLastName());
						ownerDetailsObj
								.setOwnerAddress(ownerObj.getOwnerAddress());
						ownerDetailsObj.setOwnerName(ownerObj.getOwnerName());
						ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
						ownerDetailsObj.setId(inputObj.getId().toString());
						ownerDetailsObj.setCreatedDate(DateUtil.getCurrentDateTime());
						ownerDetailsObj.setCreatedBy("System");
						ownerDetailsObj.setIsActive("Y");
						ownerDetailsRepository.save(ownerDetailsObj);
						status = "Y";
					}
				} else {
					if(ownerDetailsListObj.size()>0) {
						for(com.project.json2sql.dto.OwnerDetails ownerObj : ownerDetailsListObj) {
							ownerDetailsObj = new OwnerDetails();
							ownerDetailsObj.setFirstName(ownerObj.getFirstName());
							ownerDetailsObj.setLastName(ownerObj.getLastName());
							ownerDetailsObj
									.setOwnerAddress(ownerObj.getOwnerAddress());
							ownerDetailsObj.setOwnerName(ownerObj.getOwnerName());
							ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
							ownerDetailsObj.setId(inputObj.getId().toString());
							ownerDetailsObj.setCreatedDate(DateUtil.getCurrentDateTime());
							ownerDetailsObj.setCreatedBy("System");
							ownerDetailsObj.setIsActive("Y");
							ownerDetailsRepository.save(ownerDetailsObj);
							status = "Y";
						}
					}
				}
			}
		}else {
			logger.info("Already processed for id :: "+propObj.getId());
			propertiesRepository.updateProcess(propObj.getId()+"", "Y");
			status = "Y";
		}
		return status;
	}

	public Root callProxy(ConfigProperties configDtoObj, InputProxyDto inputObj, OwnerProcess ownerProcess)
			throws Exception {
		Root rootObj = new Root();
		OwnerProcess ownerProcessObj = ownerProcess;
		logger.info("Proxy Execution Start for ID:",inputObj.getId());
		ownerProcessRepository.deleteOldProcess(inputObj.getId()+"");
		
		Properties systemSettings = System.getProperties();
		systemSettings.setProperty("proxySet", "true");
		systemSettings.setProperty("http.proxyHost", configDtoObj.getIp());
		systemSettings.setProperty("http.proxyPort", configDtoObj.getPort());
		
		URL u = new URL(configDtoObj.getUrl());
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		
		try {
			conn.setDoOutput(true);
			conn.setInstanceFollowRedirects(false);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("charset", "utf-8");
			conn.setRequestProperty("Authorization", configDtoObj.getAuthorization());
			conn.setRequestProperty("Accept", "application/json");
			conn.setRequestProperty("Accept-Encoding", "gzip");
			conn.setRequestProperty("Cache-Control", "no-cache");
			// conn.setRequestProperty( "User-Agent", "");
			conn.setUseCaches(false);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);

			// conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			
			ObjectMapper mapper = new ObjectMapper();
			String jsonInString = mapper.writeValueAsString(inputObj);

			OutputStream out = conn.getOutputStream();
			// out.write(inputObj.toString().getBytes());
			out.write(jsonInString.toString().getBytes());
			//out.close();


			InputStream in = new BufferedInputStream(conn.getInputStream());
			String result = IOUtils.toString(in, "UTF-8");
			/*
			 * JSONParser parser = new JSONParser(); JSONObject json = (JSONObject) parser.
			 * parse(result);
			 */

			Gson g = new Gson();
			rootObj = g.fromJson(result, Root.class);

			// JSONObject jsonObject = new JSONObject(result);

			System.out.println(conn.getResponseCode() + " : " + conn.getResponseMessage());
			System.out.println(conn.getResponseCode() == HttpURLConnection.HTTP_OK);
			ownerProcessObj.setProcessCode(conn.getResponseCode());
			ownerProcessObj.setProcessError(conn.getResponseMessage());

			if (conn.getResponseCode() != 200) {
				ownerProcessObj.setIsProcess("N");
			}else {
				ownerProcessObj.setIsProcess("Y");
				
				propertiesRepository.updateProcess(inputObj.getId()+"", "Y");
			}

			System.out.println("Using proxy:" + conn.usingProxy());
			ownerProcessObj.setUsingProxy(conn.usingProxy());

			ownerProcessRepository.save(ownerProcessObj);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(false);
		} finally {
			conn.disconnect();
		}

		return rootObj;
	}
	
	public void saveFailedData(OwnerProcess ownerProcessObj) {
		try {
			logger.info("Called save data");
			ownerProcessRepository.deleteOldProcess(ownerProcessObj.getId());
			ownerProcessRepository.save(ownerProcessObj);
		} catch (Exception e) {
			logger.error("OwnerProcess Save error");
		}
	}

}
