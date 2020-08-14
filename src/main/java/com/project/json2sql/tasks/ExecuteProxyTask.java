package com.project.json2sql.tasks;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.project.json2sql.dto.ConfigurationDto;
import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.Root;
import com.project.json2sql.model.AuditOwnerDetails;
import com.project.json2sql.model.OwnerDetails;
import com.project.json2sql.model.OwnerProcess;
import com.project.json2sql.model.Properties;
import com.project.json2sql.repository.AuditOwnerDetailsRepository;
import com.project.json2sql.repository.OwnerDetailsRepository;
import com.project.json2sql.repository.OwnerProcessRepository;
import com.project.json2sql.util.DateUtil;

public class ExecuteProxyTask implements Runnable {

	private final ConfigurationDto configDtoObj;
	private final Properties propObj;

	public static final Logger logger = LoggerFactory.getLogger(ExecuteProxyTask.class);

	@Autowired
	private OwnerProcessRepository ownerProcessRepository;

	@Autowired
	private AuditOwnerDetailsRepository auditOwnerDetailsRepository;

	@Autowired
	private OwnerDetailsRepository ownerDetailsRepository;

	public ExecuteProxyTask(ConfigurationDto configDtoObj, Properties propObj) {
		super();
		this.configDtoObj = configDtoObj;
		this.propObj = propObj;
	}

	@Override
	public void run() {
		OwnerDetails ownerDetailsObj = new OwnerDetails();
		AuditOwnerDetails auditOwnerDetails = new AuditOwnerDetails();
		String status = "N";
		try {
			if (null != this.configDtoObj) {

				InputProxyDto inputObj = new InputProxyDto();
				OwnerProcess ownerProcess = new OwnerProcess();
				inputObj.setOp(configDtoObj.getOp());
				inputObj.setSid(configDtoObj.getSid());
				inputObj.setUid(configDtoObj.getUid());
				inputObj.setLoc("AU");
				inputObj.setAppCode("rppandroid");
				inputObj.setId(Integer.parseInt(this.propObj.getId()));
				ownerProcess.setId(this.propObj.getId());
				ownerProcess.setCreatedDate(DateUtil.getCurrentDateTime());
				ownerProcess.setCreatedBy("System");
				ownerProcess.setIsProcess("N");

				Root rootObj = callProxy(configDtoObj, inputObj, ownerProcess);

				if (rootObj != null) {
					List<OwnerDetails> ownerDetailsListOldObj = ownerDetailsRepository.fetchById(inputObj.getId() + "");
					if (ownerDetailsListOldObj.size() > 0) {
						// Audit table transfer
						auditOwnerDetails.setFirstName(ownerDetailsListOldObj.get(0).getFirstName());
						auditOwnerDetails.setLastName(ownerDetailsListOldObj.get(0).getLastName());
						auditOwnerDetails.setOwnerAddress(ownerDetailsListOldObj.get(0).getOwnerAddress());
						auditOwnerDetails.setOwnerName(ownerDetailsListOldObj.get(0).getOwnerName());
						auditOwnerDetails.setCompanyName(ownerDetailsListOldObj.get(0).getCompanyName());
						auditOwnerDetails.setId(ownerDetailsListOldObj.get(0).getId());
						auditOwnerDetails.setCreatedBy(ownerDetailsListOldObj.get(0).getCreatedBy());
						auditOwnerDetails.setCreatedDate(ownerDetailsListOldObj.get(0).getCreatedDate());
						auditOwnerDetails.setIsActive("Y");
						auditOwnerDetailsRepository.updateStatus(ownerDetailsListOldObj.get(0).getId().toString(), "N");
						auditOwnerDetailsRepository.save(auditOwnerDetails);

						ownerDetailsObj.setOwnerId(ownerDetailsListOldObj.get(0).getOwnerId());
						ownerDetailsObj.setFirstName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getFirstName());
						ownerDetailsObj
								.setLastName(rootObj.getResponse().getResult().getOwnerDetails().get(0).getLastName());
						ownerDetailsObj.setOwnerAddress(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerAddress());
						ownerDetailsObj.setOwnerName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerName());
						ownerDetailsObj.setCompanyName(rootObj.getResponse().getResult().getCompanyName());
						ownerDetailsObj.setId(inputObj.getId().toString());
						ownerDetailsObj.setCreatedDate(DateUtil.getCurrentDateTime());
						ownerDetailsObj.setCreatedBy("System");
						ownerDetailsObj.setIsActive("Y");
						ownerDetailsRepository.save(ownerDetailsObj);
						status = "Y";

					} else {
						ownerDetailsObj.setFirstName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getFirstName());
						ownerDetailsObj
								.setLastName(rootObj.getResponse().getResult().getOwnerDetails().get(0).getLastName());
						ownerDetailsObj.setOwnerAddress(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerAddress());
						ownerDetailsObj.setOwnerName(
								rootObj.getResponse().getResult().getOwnerDetails().get(0).getOwnerName());
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

		} catch (Exception e) {
			status = "N";
			logger.error("Error in Proxy Service" + e);
		}
		// return status;
	}

	public Root callProxy(ConfigurationDto configDtoObj, InputProxyDto inputObj, OwnerProcess ownerProcess)
			throws Exception {
		Root rootObj = new Root();
		URL u = new URL(configDtoObj.getUrl());
		HttpURLConnection conn = (HttpURLConnection) u.openConnection();
		OwnerProcess ownerProcessObj = ownerProcess;
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

			OutputStream out = conn.getOutputStream();
			// out.write(inputObj.toString().getBytes());
			out.write(inputObj.toString().getBytes("UTF-8"));
			out.close();

			// read the response
			/*
			 * InputStream ip = conn.getInputStream(); BufferedReader br1 = new
			 * BufferedReader(new InputStreamReader(ip));
			 */

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
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
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

}
