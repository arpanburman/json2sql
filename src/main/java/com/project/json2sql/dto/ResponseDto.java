
package com.project.json2sql.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "status",
    "result"
})
public class ResponseDto {

    @JsonProperty("status")
    private String status;
    @JsonProperty("result")
    private ResultDto result;

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

	public ResultDto getResult() {
		return result;
	}

	public void setResult(ResultDto result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "ResponseDto [status=" + status + ", result=" + result + "]";
	}


}
