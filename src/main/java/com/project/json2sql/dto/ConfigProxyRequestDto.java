package com.project.json2sql.dto;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
	"authorization",
	"op",
	"sid",
	"uid",
	"url",
	"ip",
	"port",
	"frequency",
	"loc",
	"appCode",
	"starttime",
	"endtime",
	"searchString",
	"pageSize",
	"maxResult",
	"pageNumber"
})
public class ConfigProxyRequestDto {

	@JsonProperty("authorization")
	private String authorization;
	@JsonProperty("op")
	private String op;
	@JsonProperty("sid")
	private String sid;
	@JsonProperty("uid")
	private String uid;
	@JsonProperty("url")
	private String url;
	@JsonProperty("ip")
	private String ip;
	@JsonProperty("port")
	private String port;
	@JsonProperty("frequency")
	private String frequency;
	@JsonProperty("loc")
	private String loc;
	@JsonProperty("appCode")
	private String appCode;
	@JsonProperty("starttime")
	private String starttime;
	@JsonProperty("endtime")
	private String endtime;
	@JsonProperty("searchString")
	private String searchString;
	@JsonProperty("pageSize")
	private String pageSize;
	@JsonProperty("maxResult")
	private String maxResult;
	@JsonProperty("pageNumber")
	private String pageNumber;
	
	private String key;

	@JsonProperty("authorization")
	public String getAuthorization() {
		return authorization;
	}

	@JsonProperty("authorization")
	public void setAuthorization(String authorization) {
		this.authorization = authorization;
	}

	@JsonProperty("op")
	public String getOp() {
		return op;
	}

	@JsonProperty("op")
	public void setOp(String op) {
		this.op = op;
	}

	@JsonProperty("sid")
	public String getSid() {
		return sid;
	}

	@JsonProperty("sid")
	public void setSid(String sid) {
		this.sid = sid;
	}

	@JsonProperty("uid")
	public String getUid() {
		return uid;
	}

	@JsonProperty("uid")
	public void setUid(String uid) {
		this.uid = uid;
	}

	@JsonProperty("url")
	public String getUrl() {
		return url;
	}

	@JsonProperty("url")
	public void setUrl(String url) {
		this.url = url;
	}

	@JsonProperty("ip")
	public String getIp() {
		return ip;
	}

	@JsonProperty("ip")
	public void setIp(String ip) {
		this.ip = ip;
	}

	@JsonProperty("port")
	public String getPort() {
		return port;
	}

	@JsonProperty("port")
	public void setPort(String port) {
		this.port = port;
	}

	@JsonProperty("frequency")
	public String getFrequency() {
		return frequency;
	}

	@JsonProperty("frequency")
	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	@JsonProperty("loc")
	public String getLoc() {
		return loc;
	}

	@JsonProperty("loc")
	public void setLoc(String loc) {
		this.loc = loc;
	}

	@JsonProperty("appCode")
	public String getAppCode() {
		return appCode;
	}

	@JsonProperty("appCode")
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	@JsonProperty("starttime")
	public String getStarttime() {
		return starttime;
	}

	@JsonProperty("starttime")
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	@JsonProperty("endtime")
	public String getEndtime() {
		return endtime;
	}

	@JsonProperty("endtime")
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	@JsonProperty("searchString")
	public String getSearchString() {
		return searchString;
	}

	@JsonProperty("searchString")
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}

	@JsonProperty("pageSize")
	public String getPageSize() {
		return pageSize;
	}

	@JsonProperty("pageSize")
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@JsonProperty("maxResult")
	public String getMaxResult() {
		return maxResult;
	}

	@JsonProperty("maxResult")
	public void setMaxResult(String maxResult) {
		this.maxResult = maxResult;
	}

	@JsonProperty("pageNumber")
	public String getPageNumber() {
		return pageNumber;
	}

	@JsonProperty("pageNumber")
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}


}