package com.project.json2sql.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.project.json2sql.dto.ConfigProxyRequestDto;
import com.project.json2sql.dto.MainJson;
import com.project.json2sql.dto.PagesDto;
import com.project.json2sql.dto.PropertyDto;
import com.project.json2sql.dto.RequestSearchStringDto;
import com.project.json2sql.dto.SummaryDto;
import com.project.json2sql.model.AuditProperties;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.Properties;
import com.project.json2sql.model.Summary;
import com.project.json2sql.repository.AuditPropertiesRepository;
import com.project.json2sql.repository.PageTransactionRepository;
import com.project.json2sql.repository.PropertiesRepository;
import com.project.json2sql.repository.SearchStringRepository;
import com.project.json2sql.repository.SummaryRepository;

public class CheckProxyPageSize {
	
	@Autowired
	SummaryRepository summaryRepository;

	@Autowired
	PropertiesRepository propertiesRepository;
	
	@Autowired
	AuditPropertiesRepository auditPropertiesRepository;
	
	@Autowired
	SearchStringRepository searchStringRepository;
	
	@Autowired
	PageTransactionRepository pageTransactionRepository;
	
	public static final Logger logger = LoggerFactory.getLogger(CheckProxyPageSize.class);
	
	

}
