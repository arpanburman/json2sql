package com.project.json2sql.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MainJson {

	@JsonProperty("response")
	private ResponseDto response;

	public ResponseDto getResponse() {
		return response;
	}

	public void setResponse(ResponseDto response) {
		this.response = response;
	}

	

}
