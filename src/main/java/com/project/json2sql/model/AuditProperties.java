package com.project.json2sql.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="audit_properties")
public class AuditProperties {
	
	@Id
	@Column(name="audit_properties_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long auditPropertiesId;
	
	@Column(name="properties_id")
	private long propertiesId;
	
	@Column(name="type")
	private String type;
	
	@Column(name="id")
	private String id;
	
	@Column(name="lat")
	private String lat;
	
	@Column(name="lon")
	private String lon;
	
	@Column(name="unitNumber")
	private String unitNumber;
	
	@Column(name="streetNumber")
	private String streetNumber;
	
	@Column(name="streetName")
	private String streetName;
	
	@Column(name="streetType")
	private String streetType;
	
	@Column(name="streetDirection")
	private String streetDirection;
	
	@Column(name="address")
	private String address;
	
	@Column(name="suburb")
	private String suburb;
	
	@Column(name="postcode")
	private String postcode;
	
	@Column(name="state")
	private String state;
	
	@Column(name="bathrooms")
	private String bathrooms;
	
	@Column(name="bedrooms")
	private String bedrooms;
	
	@Column(name="parking")
	private String parking;
	
	@Column(name="landSize")
	private String landSize;
	
	@Column(name="salePrice")
	private String salePrice;
	
	@Column(name="saleDate")
	private String saleDate;
	
	@Column(name="onTheMarket")
	private String onTheMarket;
	
	@Column(name="listingDate")
	private String listingDate;
	
	@Column(name="listingPrice")
	private String listingPrice;
	
	@Column(name="listingDescription")
	private String listingDescription;
	
	@Column(name="listedType")
	private String listedType;
	
	@Column(name="auctionDate")
	private String auctionDate;
	
	@Column(name="auctionTime")
	private String auctionTime;
	
	@Column(name="rentalListingDate")
	private String rentalListingDate;
	
	@Column(name="rentalListingPrice")
	private String rentalListingPrice;
	
	@Column(name="rentalListingPeriod")
	private String rentalListingPeriod;
	
	@Column(name="REAId")
	private String REAId;
	
	@Column(name="agentName")
	private String agentName;
	
	@Column(name="recentSales")
	private String recentSales;
	
	@Column(name="photo")
	private String photo;
	
	@Column(name="ucv")
	private String ucv;
	
	@Column(name="ucvDate")
	private String ucvDate;
	
	@Column(name="realPropertyDescriptor", length = 2000)
	private String realPropertyDescriptor;
	
	@Column(name="lgaName")
	private String lgaName;
	
	@Column(name="lastSaleType")
	private String lastSaleType;
	
	@Column(name="lotPlan")
	private String lotPlan;
	
	@Column(name="zoning")
	private String zoning;
	
	@Column(name="isAgentAdvised")
	private String isAgentAdvised;
	
	@Column(name="landUsePrimary")
	private String landUsePrimary;
	
	@Column(name="currentRentalPrice")
	private String currentRentalPrice;
	
	@Column(name="forRent")
	private String forRent;
	
	@Column(name="forRentDaysOnMarket")
	private String forRentDaysOnMarket;
	
	@Column(name="forRentAgencyName")
	private String forRentAgencyName;
	
	@Column(name="occupancyType")
	private String occupancyType;
	
	@Column(name="volume")
	private String volume;
	
	@Column(name="folio")
	private String folio;
	
	@Column(name="titlePrefix")
	private String titlePrefix;
	
	@Column(name="titleSuffix")
	private String titleSuffix;
	
	@Column(name="mapReference")
	private String mapReference;
	
	@Column(name="block")
	private String block;
	
	@Column(name="section")
	private String section;
	
	@Column(name="parcelList")
	private String parcelList;
	
	@Column(name="developmentZone")
	private String developmentZone;
	
	@Column(name="isPriceWithheld")
	private String isPriceWithheld;
	
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

	

	public long getAuditPropertiesId() {
		return auditPropertiesId;
	}

	public void setAuditPropertiesId(long auditPropertiesId) {
		this.auditPropertiesId = auditPropertiesId;
	}


	public long getPropertiesId() {
		return propertiesId;
	}

	public void setPropertiesId(long propertiesId) {
		this.propertiesId = propertiesId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLon() {
		return lon;
	}

	public void setLon(String lon) {
		this.lon = lon;
	}

	public String getUnitNumber() {
		return unitNumber;
	}

	public void setUnitNumber(String unitNumber) {
		this.unitNumber = unitNumber;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getStreetType() {
		return streetType;
	}

	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}

	public String getStreetDirection() {
		return streetDirection;
	}

	public void setStreetDirection(String streetDirection) {
		this.streetDirection = streetDirection;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getSuburb() {
		return suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getBathrooms() {
		return bathrooms;
	}

	public void setBathrooms(String bathrooms) {
		this.bathrooms = bathrooms;
	}

	public String getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(String bedrooms) {
		this.bedrooms = bedrooms;
	}

	public String getParking() {
		return parking;
	}

	public void setParking(String parking) {
		this.parking = parking;
	}

	public String getLandSize() {
		return landSize;
	}

	public void setLandSize(String landSize) {
		this.landSize = landSize;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getSaleDate() {
		return saleDate;
	}

	public void setSaleDate(String saleDate) {
		this.saleDate = saleDate;
	}

	public String getOnTheMarket() {
		return onTheMarket;
	}

	public void setOnTheMarket(String onTheMarket) {
		this.onTheMarket = onTheMarket;
	}

	public String getListingDate() {
		return listingDate;
	}

	public void setListingDate(String listingDate) {
		this.listingDate = listingDate;
	}

	public String getListingPrice() {
		return listingPrice;
	}

	public void setListingPrice(String listingPrice) {
		this.listingPrice = listingPrice;
	}

	public String getListingDescription() {
		return listingDescription;
	}

	public void setListingDescription(String listingDescription) {
		this.listingDescription = listingDescription;
	}

	public String getListedType() {
		return listedType;
	}

	public void setListedType(String listedType) {
		this.listedType = listedType;
	}

	public String getAuctionDate() {
		return auctionDate;
	}

	public void setAuctionDate(String auctionDate) {
		this.auctionDate = auctionDate;
	}

	public String getAuctionTime() {
		return auctionTime;
	}

	public void setAuctionTime(String auctionTime) {
		this.auctionTime = auctionTime;
	}

	public String getRentalListingDate() {
		return rentalListingDate;
	}

	public void setRentalListingDate(String rentalListingDate) {
		this.rentalListingDate = rentalListingDate;
	}

	public String getRentalListingPrice() {
		return rentalListingPrice;
	}

	public void setRentalListingPrice(String rentalListingPrice) {
		this.rentalListingPrice = rentalListingPrice;
	}

	public String getRentalListingPeriod() {
		return rentalListingPeriod;
	}

	public void setRentalListingPeriod(String rentalListingPeriod) {
		this.rentalListingPeriod = rentalListingPeriod;
	}

	public String getREAId() {
		return REAId;
	}

	public void setREAId(String rEAId) {
		REAId = rEAId;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getRecentSales() {
		return recentSales;
	}

	public void setRecentSales(String recentSales) {
		this.recentSales = recentSales;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getUcv() {
		return ucv;
	}

	public void setUcv(String ucv) {
		this.ucv = ucv;
	}

	public String getUcvDate() {
		return ucvDate;
	}

	public void setUcvDate(String ucvDate) {
		this.ucvDate = ucvDate;
	}

	public String getRealPropertyDescriptor() {
		return realPropertyDescriptor;
	}

	public void setRealPropertyDescriptor(String realPropertyDescriptor) {
		this.realPropertyDescriptor = realPropertyDescriptor;
	}

	public String getLgaName() {
		return lgaName;
	}

	public void setLgaName(String lgaName) {
		this.lgaName = lgaName;
	}

	public String getLastSaleType() {
		return lastSaleType;
	}

	public void setLastSaleType(String lastSaleType) {
		this.lastSaleType = lastSaleType;
	}

	public String getLotPlan() {
		return lotPlan;
	}

	public void setLotPlan(String lotPlan) {
		this.lotPlan = lotPlan;
	}

	public String getZoning() {
		return zoning;
	}

	public void setZoning(String zoning) {
		this.zoning = zoning;
	}

	public String getIsAgentAdvised() {
		return isAgentAdvised;
	}

	public void setIsAgentAdvised(String isAgentAdvised) {
		this.isAgentAdvised = isAgentAdvised;
	}

	public String getLandUsePrimary() {
		return landUsePrimary;
	}

	public void setLandUsePrimary(String landUsePrimary) {
		this.landUsePrimary = landUsePrimary;
	}

	public String getCurrentRentalPrice() {
		return currentRentalPrice;
	}

	public void setCurrentRentalPrice(String currentRentalPrice) {
		this.currentRentalPrice = currentRentalPrice;
	}

	public String getForRent() {
		return forRent;
	}

	public void setForRent(String forRent) {
		this.forRent = forRent;
	}

	public String getForRentDaysOnMarket() {
		return forRentDaysOnMarket;
	}

	public void setForRentDaysOnMarket(String forRentDaysOnMarket) {
		this.forRentDaysOnMarket = forRentDaysOnMarket;
	}

	public String getForRentAgencyName() {
		return forRentAgencyName;
	}

	public void setForRentAgencyName(String forRentAgencyName) {
		this.forRentAgencyName = forRentAgencyName;
	}

	public String getOccupancyType() {
		return occupancyType;
	}

	public void setOccupancyType(String occupancyType) {
		this.occupancyType = occupancyType;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getFolio() {
		return folio;
	}

	public void setFolio(String folio) {
		this.folio = folio;
	}

	public String getTitlePrefix() {
		return titlePrefix;
	}

	public void setTitlePrefix(String titlePrefix) {
		this.titlePrefix = titlePrefix;
	}

	public String getTitleSuffix() {
		return titleSuffix;
	}

	public void setTitleSuffix(String titleSuffix) {
		this.titleSuffix = titleSuffix;
	}

	public String getMapReference() {
		return mapReference;
	}

	public void setMapReference(String mapReference) {
		this.mapReference = mapReference;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getParcelList() {
		return parcelList;
	}

	public void setParcelList(String parcelList) {
		this.parcelList = parcelList;
	}

	public String getDevelopmentZone() {
		return developmentZone;
	}

	public void setDevelopmentZone(String developmentZone) {
		this.developmentZone = developmentZone;
	}

	public String getIsPriceWithheld() {
		return isPriceWithheld;
	}

	public void setIsPriceWithheld(String isPriceWithheld) {
		this.isPriceWithheld = isPriceWithheld;
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

	
}
