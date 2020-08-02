package com.project.json2sql.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"op",
"sid",
"uid",
"loc",
"appCode",
"id"
})
public class InputProxyDto {

@JsonProperty("op")
private String op;
@JsonProperty("sid")
private String sid;
@JsonProperty("uid")
private String uid;
@JsonProperty("loc")
private String loc;
@JsonProperty("appCode")
private String appCode;
@JsonProperty("id")
private Integer id;

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

@JsonProperty("id")
public Integer getId() {
return id;
}

@JsonProperty("id")
public void setId(Integer id) {
this.id = id;
}

}