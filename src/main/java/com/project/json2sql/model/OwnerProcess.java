package com.project.json2sql.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="owner_process")
public class OwnerProcess {

	@Id
	@Column(name="ownerProcessId")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long ownerProcessId;
	
	@Column(name="id")
	private String id;
	
	@Column(name="cretatedDate")
	private String createdDate;
	
	@Column(name="cretatedBy")
	private String createdBy;
	
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

	public long getOwnerProcessId() {
		return ownerProcessId;
	}

	public void setOwnerProcessId(long ownerProcessId) {
		this.ownerProcessId = ownerProcessId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getIsProcess() {
		return isProcess;
	}

	public void setIsProcess(String isProcess) {
		this.isProcess = isProcess;
	}

	public String getErrorLog() {
		return errorLog;
	}

	public void setErrorLog(String errorLog) {
		this.errorLog = errorLog;
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

	
}
