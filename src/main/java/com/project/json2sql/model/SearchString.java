package com.project.json2sql.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="search_string")
public class SearchString {
	
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(name="search_string")
	private String searchString;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="isProcess")
	private String isProcess;
	
	@Column(name="startPage")
	private long startPage;
	
	@Column(name="endPage")
	private long endPage;
	
	@Column(name="lastReadPage")
	private long lastReadPage;

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

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getIsProcess() {
		return isProcess;
	}

	public void setIsProcess(String isProcess) {
		this.isProcess = isProcess;
	}

	public long getStartPage() {
		return startPage;
	}

	public void setStartPage(long startPage) {
		this.startPage = startPage;
	}

	public long getEndPage() {
		return endPage;
	}

	public void setEndPage(long endPage) {
		this.endPage = endPage;
	}

	public long getLastReadPage() {
		return lastReadPage;
	}

	public void setLastReadPage(long lastReadPage) {
		this.lastReadPage = lastReadPage;
	}

}
