
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
    "totalPages",
    "currentPage"
})
public class PagesDto {

    @JsonProperty("totalPages")
    private Integer totalPages;
    @JsonProperty("currentPage")
    private Integer currentPage;

    @JsonProperty("totalPages")
    public Integer getTotalPages() {
        return totalPages;
    }

    @JsonProperty("totalPages")
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    @JsonProperty("currentPage")
    public Integer getCurrentPage() {
        return currentPage;
    }

    @JsonProperty("currentPage")
    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

	@Override
	public String toString() {
		return "PagesDto [totalPages=" + totalPages + ", currentPage=" + currentPage + "]";
	}

}
