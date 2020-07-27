
package com.project.json2sql.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "id",
    "lat",
    "lon",
    "unitNumber",
    "streetNumber",
    "streetName",
    "streetType",
    "streetDirection",
    "address",
    "suburb",
    "postcode",
    "state",
    "bathrooms",
    "bedrooms",
    "parking",
    "landSize",
    "salePrice",
    "saleDate",
    "onTheMarket",
    "listingDate",
    "listingPrice",
    "listingDescription",
    "listedType",
    "auctionDate",
    "auctionTime",
    "rentalListingDate",
    "rentalListingPrice",
    "rentalListingPeriod",
    "REAId",
    "agentName",
    "recentSales",
    "photo",
    "ucv",
    "ucvDate",
    "realPropertyDescriptor",
    "lgaName",
    "lastSaleType",
    "lotPlan",
    "zoning",
    "isAgentAdvised",
    "landUsePrimary",
    "currentRentalPrice",
    "forRent",
    "forRentDaysOnMarket",
    "forRentAgencyName",
    "occupancyType",
    "volume",
    "folio",
    "titlePrefix",
    "titleSuffix",
    "mapReference",
    "block",
    "section",
    "parcelList",
    "developmentZone",
    "isPriceWithheld"
})
public class PropertyDto {

    @JsonProperty("type")
    private String type;
    @JsonProperty("id")
    private Integer id;
    @JsonProperty("lat")
    private Double lat;
    @JsonProperty("lon")
    private Double lon;
    @JsonProperty("unitNumber")
    private String unitNumber;
    @JsonProperty("streetNumber")
    private String streetNumber;
    @JsonProperty("streetName")
    private String streetName;
    @JsonProperty("streetType")
    private String streetType;
    @JsonProperty("streetDirection")
    private String streetDirection;
    @JsonProperty("address")
    private String address;
    @JsonProperty("suburb")
    private String suburb;
    @JsonProperty("postcode")
    private String postcode;
    @JsonProperty("state")
    private String state;
    @JsonProperty("bathrooms")
    private Integer bathrooms;
    @JsonProperty("bedrooms")
    private Integer bedrooms;
    @JsonProperty("parking")
    private Integer parking;
    @JsonProperty("landSize")
    private Integer landSize;
    @JsonProperty("salePrice")
    private String salePrice;
    @JsonProperty("saleDate")
    private String saleDate;
    @JsonProperty("onTheMarket")
    private Boolean onTheMarket;
    @JsonProperty("listingDate")
    private String listingDate;
    @JsonProperty("listingPrice")
    private String listingPrice;
    @JsonProperty("listingDescription")
    private String listingDescription;
    @JsonProperty("listedType")
    private String listedType;
    @JsonProperty("auctionDate")
    private String auctionDate;
    @JsonProperty("auctionTime")
    private String auctionTime;
    @JsonProperty("rentalListingDate")
    private String rentalListingDate;
    @JsonProperty("rentalListingPrice")
    private String rentalListingPrice;
    @JsonProperty("rentalListingPeriod")
    private String rentalListingPeriod;
    @JsonProperty("REAId")
    private String rEAId;
    @JsonProperty("agentName")
    private String agentName;
    @JsonProperty("recentSales")
    private Boolean recentSales;
    @JsonProperty("photo")
    private String photo;
    @JsonProperty("ucv")
    private Integer ucv;
    @JsonProperty("ucvDate")
    private String ucvDate;
    @JsonProperty("realPropertyDescriptor")
    private String realPropertyDescriptor;
    @JsonProperty("lgaName")
    private String lgaName;
    @JsonProperty("lastSaleType")
    private String lastSaleType;
    @JsonProperty("lotPlan")
    private String lotPlan;
    @JsonProperty("zoning")
    private String zoning;
    @JsonProperty("isAgentAdvised")
    private Boolean isAgentAdvised;
    @JsonProperty("landUsePrimary")
    private String landUsePrimary;
    @JsonProperty("currentRentalPrice")
    private String currentRentalPrice;
    @JsonProperty("forRent")
    private Boolean forRent;
    @JsonProperty("forRentDaysOnMarket")
    private Integer forRentDaysOnMarket;
    @JsonProperty("forRentAgencyName")
    private String forRentAgencyName;
    @JsonProperty("occupancyType")
    private String occupancyType;
    @JsonProperty("volume")
    private String volume;
    @JsonProperty("folio")
    private String folio;
    @JsonProperty("titlePrefix")
    private String titlePrefix;
    @JsonProperty("titleSuffix")
    private String titleSuffix;
    @JsonProperty("mapReference")
    private String mapReference;
    @JsonProperty("block")
    private String block;
    @JsonProperty("section")
    private String section;
    @JsonProperty("parcelList")
    private List<Object> parcelList = null;
    @JsonProperty("developmentZone")
    private String developmentZone;
    @JsonProperty("isPriceWithheld")
    private String isPriceWithheld;

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("lat")
    public Double getLat() {
        return lat;
    }

    @JsonProperty("lat")
    public void setLat(Double lat) {
        this.lat = lat;
    }

    @JsonProperty("lon")
    public Double getLon() {
        return lon;
    }

    @JsonProperty("lon")
    public void setLon(Double lon) {
        this.lon = lon;
    }

    @JsonProperty("unitNumber")
    public String getUnitNumber() {
        return unitNumber;
    }

    @JsonProperty("unitNumber")
    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    @JsonProperty("streetNumber")
    public String getStreetNumber() {
        return streetNumber;
    }

    @JsonProperty("streetNumber")
    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    @JsonProperty("streetName")
    public String getStreetName() {
        return streetName;
    }

    @JsonProperty("streetName")
    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @JsonProperty("streetType")
    public String getStreetType() {
        return streetType;
    }

    @JsonProperty("streetType")
    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    @JsonProperty("streetDirection")
    public String getStreetDirection() {
        return streetDirection;
    }

    @JsonProperty("streetDirection")
    public void setStreetDirection(String streetDirection) {
        this.streetDirection = streetDirection;
    }

    @JsonProperty("address")
    public String getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(String address) {
        this.address = address;
    }

    @JsonProperty("suburb")
    public String getSuburb() {
        return suburb;
    }

    @JsonProperty("suburb")
    public void setSuburb(String suburb) {
        this.suburb = suburb;
    }

    @JsonProperty("postcode")
    public String getPostcode() {
        return postcode;
    }

    @JsonProperty("postcode")
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("bathrooms")
    public Integer getBathrooms() {
        return bathrooms;
    }

    @JsonProperty("bathrooms")
    public void setBathrooms(Integer bathrooms) {
        this.bathrooms = bathrooms;
    }

    @JsonProperty("bedrooms")
    public Integer getBedrooms() {
        return bedrooms;
    }

    @JsonProperty("bedrooms")
    public void setBedrooms(Integer bedrooms) {
        this.bedrooms = bedrooms;
    }

    @JsonProperty("parking")
    public Integer getParking() {
        return parking;
    }

    @JsonProperty("parking")
    public void setParking(Integer parking) {
        this.parking = parking;
    }

    @JsonProperty("landSize")
    public Integer getLandSize() {
        return landSize;
    }

    @JsonProperty("landSize")
    public void setLandSize(Integer landSize) {
        this.landSize = landSize;
    }

    @JsonProperty("salePrice")
    public String getSalePrice() {
        return salePrice;
    }

    @JsonProperty("salePrice")
    public void setSalePrice(String salePrice) {
        this.salePrice = salePrice;
    }

    @JsonProperty("saleDate")
    public String getSaleDate() {
        return saleDate;
    }

    @JsonProperty("saleDate")
    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    @JsonProperty("onTheMarket")
    public Boolean getOnTheMarket() {
        return onTheMarket;
    }

    @JsonProperty("onTheMarket")
    public void setOnTheMarket(Boolean onTheMarket) {
        this.onTheMarket = onTheMarket;
    }

    @JsonProperty("listingDate")
    public String getListingDate() {
        return listingDate;
    }

    @JsonProperty("listingDate")
    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    @JsonProperty("listingPrice")
    public String getListingPrice() {
        return listingPrice;
    }

    @JsonProperty("listingPrice")
    public void setListingPrice(String listingPrice) {
        this.listingPrice = listingPrice;
    }

    @JsonProperty("listingDescription")
    public String getListingDescription() {
        return listingDescription;
    }

    @JsonProperty("listingDescription")
    public void setListingDescription(String listingDescription) {
        this.listingDescription = listingDescription;
    }

    @JsonProperty("listedType")
    public String getListedType() {
        return listedType;
    }

    @JsonProperty("listedType")
    public void setListedType(String listedType) {
        this.listedType = listedType;
    }

    @JsonProperty("auctionDate")
    public String getAuctionDate() {
        return auctionDate;
    }

    @JsonProperty("auctionDate")
    public void setAuctionDate(String auctionDate) {
        this.auctionDate = auctionDate;
    }

    @JsonProperty("auctionTime")
    public String getAuctionTime() {
        return auctionTime;
    }

    @JsonProperty("auctionTime")
    public void setAuctionTime(String auctionTime) {
        this.auctionTime = auctionTime;
    }

    @JsonProperty("rentalListingDate")
    public String getRentalListingDate() {
        return rentalListingDate;
    }

    @JsonProperty("rentalListingDate")
    public void setRentalListingDate(String rentalListingDate) {
        this.rentalListingDate = rentalListingDate;
    }

    @JsonProperty("rentalListingPrice")
    public String getRentalListingPrice() {
        return rentalListingPrice;
    }

    @JsonProperty("rentalListingPrice")
    public void setRentalListingPrice(String rentalListingPrice) {
        this.rentalListingPrice = rentalListingPrice;
    }

    @JsonProperty("rentalListingPeriod")
    public String getRentalListingPeriod() {
        return rentalListingPeriod;
    }

    @JsonProperty("rentalListingPeriod")
    public void setRentalListingPeriod(String rentalListingPeriod) {
        this.rentalListingPeriod = rentalListingPeriod;
    }

    @JsonProperty("REAId")
    public String getREAId() {
        return rEAId;
    }

    @JsonProperty("REAId")
    public void setREAId(String rEAId) {
        this.rEAId = rEAId;
    }

    @JsonProperty("agentName")
    public String getAgentName() {
        return agentName;
    }

    @JsonProperty("agentName")
    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    @JsonProperty("recentSales")
    public Boolean getRecentSales() {
        return recentSales;
    }

    @JsonProperty("recentSales")
    public void setRecentSales(Boolean recentSales) {
        this.recentSales = recentSales;
    }

    @JsonProperty("photo")
    public String getPhoto() {
        return photo;
    }

    @JsonProperty("photo")
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @JsonProperty("ucv")
    public Integer getUcv() {
        return ucv;
    }

    @JsonProperty("ucv")
    public void setUcv(Integer ucv) {
        this.ucv = ucv;
    }

    @JsonProperty("ucvDate")
    public String getUcvDate() {
        return ucvDate;
    }

    @JsonProperty("ucvDate")
    public void setUcvDate(String ucvDate) {
        this.ucvDate = ucvDate;
    }

    @JsonProperty("realPropertyDescriptor")
    public String getRealPropertyDescriptor() {
        return realPropertyDescriptor;
    }

    @JsonProperty("realPropertyDescriptor")
    public void setRealPropertyDescriptor(String realPropertyDescriptor) {
        this.realPropertyDescriptor = realPropertyDescriptor;
    }

    @JsonProperty("lgaName")
    public String getLgaName() {
        return lgaName;
    }

    @JsonProperty("lgaName")
    public void setLgaName(String lgaName) {
        this.lgaName = lgaName;
    }

    @JsonProperty("lastSaleType")
    public String getLastSaleType() {
        return lastSaleType;
    }

    @JsonProperty("lastSaleType")
    public void setLastSaleType(String lastSaleType) {
        this.lastSaleType = lastSaleType;
    }

    @JsonProperty("lotPlan")
    public String getLotPlan() {
        return lotPlan;
    }

    @JsonProperty("lotPlan")
    public void setLotPlan(String lotPlan) {
        this.lotPlan = lotPlan;
    }

    @JsonProperty("zoning")
    public String getZoning() {
        return zoning;
    }

    @JsonProperty("zoning")
    public void setZoning(String zoning) {
        this.zoning = zoning;
    }

    @JsonProperty("isAgentAdvised")
    public Boolean getIsAgentAdvised() {
        return isAgentAdvised;
    }

    @JsonProperty("isAgentAdvised")
    public void setIsAgentAdvised(Boolean isAgentAdvised) {
        this.isAgentAdvised = isAgentAdvised;
    }

    @JsonProperty("landUsePrimary")
    public String getLandUsePrimary() {
        return landUsePrimary;
    }

    @JsonProperty("landUsePrimary")
    public void setLandUsePrimary(String landUsePrimary) {
        this.landUsePrimary = landUsePrimary;
    }

    @JsonProperty("currentRentalPrice")
    public String getCurrentRentalPrice() {
        return currentRentalPrice;
    }

    @JsonProperty("currentRentalPrice")
    public void setCurrentRentalPrice(String currentRentalPrice) {
        this.currentRentalPrice = currentRentalPrice;
    }

    @JsonProperty("forRent")
    public Boolean getForRent() {
        return forRent;
    }

    @JsonProperty("forRent")
    public void setForRent(Boolean forRent) {
        this.forRent = forRent;
    }

    @JsonProperty("forRentDaysOnMarket")
    public Integer getForRentDaysOnMarket() {
        return forRentDaysOnMarket;
    }

    @JsonProperty("forRentDaysOnMarket")
    public void setForRentDaysOnMarket(Integer forRentDaysOnMarket) {
        this.forRentDaysOnMarket = forRentDaysOnMarket;
    }

    @JsonProperty("forRentAgencyName")
    public String getForRentAgencyName() {
        return forRentAgencyName;
    }

    @JsonProperty("forRentAgencyName")
    public void setForRentAgencyName(String forRentAgencyName) {
        this.forRentAgencyName = forRentAgencyName;
    }

    @JsonProperty("occupancyType")
    public String getOccupancyType() {
        return occupancyType;
    }

    @JsonProperty("occupancyType")
    public void setOccupancyType(String occupancyType) {
        this.occupancyType = occupancyType;
    }

    @JsonProperty("volume")
    public String getVolume() {
        return volume;
    }

    @JsonProperty("volume")
    public void setVolume(String volume) {
        this.volume = volume;
    }

    @JsonProperty("folio")
    public String getFolio() {
        return folio;
    }

    @JsonProperty("folio")
    public void setFolio(String folio) {
        this.folio = folio;
    }

    @JsonProperty("titlePrefix")
    public String getTitlePrefix() {
        return titlePrefix;
    }

    @JsonProperty("titlePrefix")
    public void setTitlePrefix(String titlePrefix) {
        this.titlePrefix = titlePrefix;
    }

    @JsonProperty("titleSuffix")
    public String getTitleSuffix() {
        return titleSuffix;
    }

    @JsonProperty("titleSuffix")
    public void setTitleSuffix(String titleSuffix) {
        this.titleSuffix = titleSuffix;
    }

    @JsonProperty("mapReference")
    public String getMapReference() {
        return mapReference;
    }

    @JsonProperty("mapReference")
    public void setMapReference(String mapReference) {
        this.mapReference = mapReference;
    }

    @JsonProperty("block")
    public String getBlock() {
        return block;
    }

    @JsonProperty("block")
    public void setBlock(String block) {
        this.block = block;
    }

    @JsonProperty("section")
    public String getSection() {
        return section;
    }

    @JsonProperty("section")
    public void setSection(String section) {
        this.section = section;
    }

    @JsonProperty("parcelList")
    public List<Object> getParcelList() {
        return parcelList;
    }

    @JsonProperty("parcelList")
    public void setParcelList(List<Object> parcelList) {
        this.parcelList = parcelList;
    }

    @JsonProperty("developmentZone")
    public String getDevelopmentZone() {
        return developmentZone;
    }

    @JsonProperty("developmentZone")
    public void setDevelopmentZone(String developmentZone) {
        this.developmentZone = developmentZone;
    }

    @JsonProperty("isPriceWithheld")
    public String getIsPriceWithheld() {
        return isPriceWithheld;
    }

    @JsonProperty("isPriceWithheld")
    public void setIsPriceWithheld(String isPriceWithheld) {
        this.isPriceWithheld = isPriceWithheld;
    }

	@Override
	public String toString() {
		return "PropertyDto [type=" + type + ", id=" + id + ", lat=" + lat + ", lon=" + lon + ", unitNumber="
				+ unitNumber + ", streetNumber=" + streetNumber + ", streetName=" + streetName + ", streetType="
				+ streetType + ", streetDirection=" + streetDirection + ", address=" + address + ", suburb=" + suburb
				+ ", postcode=" + postcode + ", state=" + state + ", bathrooms=" + bathrooms + ", bedrooms=" + bedrooms
				+ ", parking=" + parking + ", landSize=" + landSize + ", salePrice=" + salePrice + ", saleDate="
				+ saleDate + ", onTheMarket=" + onTheMarket + ", listingDate=" + listingDate + ", listingPrice="
				+ listingPrice + ", listingDescription=" + listingDescription + ", listedType=" + listedType
				+ ", auctionDate=" + auctionDate + ", auctionTime=" + auctionTime + ", rentalListingDate="
				+ rentalListingDate + ", rentalListingPrice=" + rentalListingPrice + ", rentalListingPeriod="
				+ rentalListingPeriod + ", rEAId=" + rEAId + ", agentName=" + agentName + ", recentSales=" + recentSales
				+ ", photo=" + photo + ", ucv=" + ucv + ", ucvDate=" + ucvDate + ", realPropertyDescriptor="
				+ realPropertyDescriptor + ", lgaName=" + lgaName + ", lastSaleType=" + lastSaleType + ", lotPlan="
				+ lotPlan + ", zoning=" + zoning + ", isAgentAdvised=" + isAgentAdvised + ", landUsePrimary="
				+ landUsePrimary + ", currentRentalPrice=" + currentRentalPrice + ", forRent=" + forRent
				+ ", forRentDaysOnMarket=" + forRentDaysOnMarket + ", forRentAgencyName=" + forRentAgencyName
				+ ", occupancyType=" + occupancyType + ", volume=" + volume + ", folio=" + folio + ", titlePrefix="
				+ titlePrefix + ", titleSuffix=" + titleSuffix + ", mapReference=" + mapReference + ", block=" + block
				+ ", section=" + section + ", parcelList=" + parcelList + ", developmentZone=" + developmentZone
				+ ", isPriceWithheld=" + isPriceWithheld + "]";
	}


}
