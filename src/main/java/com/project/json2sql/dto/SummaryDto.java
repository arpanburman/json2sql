
package com.project.json2sql.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "total",
    "onTheMarket",
    "recentlySold",
    "other",
    "isLastPage",
    "forRent"
})
public class SummaryDto {

    @JsonProperty("total")
    private Integer total;
    @JsonProperty("onTheMarket")
    private Integer onTheMarket;
    @JsonProperty("recentlySold")
    private Integer recentlySold;
    @JsonProperty("other")
    private Integer other;
    @JsonProperty("isLastPage")
    private Boolean isLastPage;
    @JsonProperty("forRent")
    private Integer forRent;

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }

    @JsonProperty("onTheMarket")
    public Integer getOnTheMarket() {
        return onTheMarket;
    }

    @JsonProperty("onTheMarket")
    public void setOnTheMarket(Integer onTheMarket) {
        this.onTheMarket = onTheMarket;
    }

    @JsonProperty("recentlySold")
    public Integer getRecentlySold() {
        return recentlySold;
    }

    @JsonProperty("recentlySold")
    public void setRecentlySold(Integer recentlySold) {
        this.recentlySold = recentlySold;
    }

    @JsonProperty("other")
    public Integer getOther() {
        return other;
    }

    @JsonProperty("other")
    public void setOther(Integer other) {
        this.other = other;
    }

    @JsonProperty("isLastPage")
    public Boolean getIsLastPage() {
        return isLastPage;
    }

    @JsonProperty("isLastPage")
    public void setIsLastPage(Boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    @JsonProperty("forRent")
    public Integer getForRent() {
        return forRent;
    }

    @JsonProperty("forRent")
    public void setForRent(Integer forRent) {
        this.forRent = forRent;
    }

	@Override
	public String toString() {
		return "SummaryDto [total=" + total + ", onTheMarket=" + onTheMarket + ", recentlySold=" + recentlySold
				+ ", other=" + other + ", isLastPage=" + isLastPage + ", forRent=" + forRent + "]";
	}


}
