
package com.project.json2sql.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "summary",
    "pages",
    "buildings",
    "properties"
})
public class ResultDto {

    @JsonProperty("summary")
    private SummaryDto summary;
    @JsonProperty("pages")
    private PagesDto pages;
    @JsonProperty("buildings")
    private List<Object> buildings = null;
    @JsonProperty("properties")
    private List<PropertyDto> properties = null;
	public SummaryDto getSummary() {
		return summary;
	}
	public void setSummary(SummaryDto summary) {
		this.summary = summary;
	}
	public PagesDto getPages() {
		return pages;
	}
	public void setPages(PagesDto pages) {
		this.pages = pages;
	}
	public List<Object> getBuildings() {
		return buildings;
	}
	public void setBuildings(List<Object> buildings) {
		this.buildings = buildings;
	}
	public List<PropertyDto> getProperties() {
		return properties;
	}
	public void setProperties(List<PropertyDto> properties) {
		this.properties = properties;
	}

}
