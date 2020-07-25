package com.project.json2sql.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="summary")
public class Summary {
	
	@Id
	@Column(name="summary_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long summaryId;
	
	@Column(name="total")
	private String total;
	
	@Column(name="onTheMarket")
	private String onTheMarket;
	
	@Column(name="recentlySold")
	private String recentlySold;
	
	@Column(name="other")
	private String other;
	
	@Column(name="isLastPage")
	private String isLastPage;
	
	@Column(name="forRent")
	private String forRent;
	
	@Column(name="totalPages")
	private String totalPages;
	
	@Column(name="currentPage")
	private String currentPage;
	
	@Column(name="process_id")
	private long processId;
	
	@Column(name="created_date")
	private String createdDate;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="updated_date")
	private String lastUpdated;
	
	@Column(name="updated_by")
	private String updatedBy;

	public long getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(long summaryId) {
		this.summaryId = summaryId;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getOnTheMarket() {
		return onTheMarket;
	}

	public void setOnTheMarket(String onTheMarket) {
		this.onTheMarket = onTheMarket;
	}

	public String getOther() {
		return other;
	}

	public void setOther(String other) {
		this.other = other;
	}

	public String getIsLastPage() {
		return isLastPage;
	}

	public void setIsLastPage(String isLastPage) {
		this.isLastPage = isLastPage;
	}

	public String getForRent() {
		return forRent;
	}

	public void setForRent(String forRent) {
		this.forRent = forRent;
	}

	public String getTotalPages() {
		return totalPages;
	}

	public void setTotalPages(String totalPages) {
		this.totalPages = totalPages;
	}

	public String getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(String currentPage) {
		this.currentPage = currentPage;
	}

	public long getProcessId() {
		return processId;
	}

	public void setProcessId(long processId) {
		this.processId = processId;
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

	public String getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(String lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public String getRecentlySold() {
		return recentlySold;
	}

	public void setRecentlySold(String recentlySold) {
		this.recentlySold = recentlySold;
	}

	
}
