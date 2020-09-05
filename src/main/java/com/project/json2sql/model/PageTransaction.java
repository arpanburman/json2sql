package com.project.json2sql.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="page_transaction")
public class PageTransaction {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="search_string")
	private String searchString;
	
	@Column(name="page_number")
	private long pageNumber;
	
	@Column(name="page_size")
	private long pageSize;
	
	@Column(name="max_result")
	private long maxResult;
	
	@Column(name="isProcess")
	private String isProcess;
	
	@Column(name="processCode")
	private Integer processCode;
	
	@Column(name="processError", length = 2000)
	private String processError;
	
	@Column(name="usingProxy")
	private boolean usingProxy;
	
	@Column(name="error_log", length = 2000)
	private String errorLog;
	
	@Column(name="cretatedDate")
	private String createdDate;
	
	@Column(name="cretatedBy")
	private String createdBy;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSearchString() {
		return searchString;
	}

	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	public long getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}

	public long getPageSize() {
		return pageSize;
	}

	public void setPageSize(long pageSize) {
		this.pageSize = pageSize;
	}

	public String getIsProcess() {
		return isProcess;
	}

	public void setIsProcess(String isProcess) {
		this.isProcess = isProcess;
	}

	public Integer getProcessCode() {
		return processCode;
	}

	public void setProcessCode(Integer processCode) {
		this.processCode = processCode;
	}

	public String getProcessError() {
		return processError;
	}

	public void setProcessError(String processError) {
		this.processError = processError;
	}

	public boolean isUsingProxy() {
		return usingProxy;
	}

	public void setUsingProxy(boolean usingProxy) {
		this.usingProxy = usingProxy;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public long getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(long maxResult) {
		this.maxResult = maxResult;
	}
	
}
