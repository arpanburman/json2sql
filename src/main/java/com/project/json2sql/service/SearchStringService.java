package com.project.json2sql.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.project.json2sql.dto.ConfigProxyRequestDto;
import com.project.json2sql.dto.ConfigurationDto;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.PageSkipTransaction;
import com.project.json2sql.model.PageTransaction;
import com.project.json2sql.model.SearchString;
import com.project.json2sql.model.SearchStringFile;

public interface SearchStringService {

	String uploadFile(MultipartFile file);

	List<SearchStringFile> getSearchString(int pageLimit, int offset);

	void processSearchStringJob();

	List<SearchString> getSearchStringList(int pageLimit, int offset);

	List<SearchString> getSearchStringById(String id);

	int countSearchString();

	ConfigProperties setConfigDetails(ConfigurationDto configDtoObj);

	ConfigProperties fetchConfigProp();

	String testProxy(ConfigurationDto configDtoObj) throws Exception;

	void multiThreadExecuteProxy(ConfigProxyRequestDto configProxyRequestDto);

	void stopMultiThreadExecuteProxy() throws InterruptedException;

	List<PageSkipTransaction> skipSearchString();

	List<PageSkipTransaction> addSkipSearchString(PageSkipTransaction pageSkipTransaction);

	List<PageSkipTransaction> deleteSkipSearchString(PageSkipTransaction pageSkipTransaction);

	void pageProcessSearchStringJob();

	List<PageTransaction> processTransactionDetails(int limit, int offset);

	String startReRunProxyString(ConfigProxyRequestDto configProxyRequestDto);

	void multiThreadFailedRunProxy();

	int countTransactionDetails();

}
