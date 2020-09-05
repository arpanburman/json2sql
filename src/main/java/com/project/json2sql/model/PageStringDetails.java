package com.project.json2sql.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="page_details")
public class PageStringDetails {

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
	
	@Column(name="updatedDate")
	private String updatedDate;

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

	public long getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(long maxResult) {
		this.maxResult = maxResult;
	}

	public String getUpdatedDate() {
		return updatedDate;
	}

	public void setUpdatedDate(String updatedDate) {
		this.updatedDate = updatedDate;
	}
	
}
