package com.project.json2sql.dto;

import java.util.List;

public class Result {

	 private String firstName;

	    private String lastName;

	    private String ownerName;

	    private List<OwnerDetails> ownerDetails;

	    private String companyName;

	    public void setFirstName(String firstName){
	        this.firstName = firstName;
	    }
	    public String getFirstName(){
	        return this.firstName;
	    }
	    public void setLastName(String lastName){
	        this.lastName = lastName;
	    }
	    public String getLastName(){
	        return this.lastName;
	    }
	    public void setOwnerName(String ownerName){
	        this.ownerName = ownerName;
	    }
	    public String getOwnerName(){
	        return this.ownerName;
	    }
	    public void setOwnerDetails(List<OwnerDetails> ownerDetails){
	        this.ownerDetails = ownerDetails;
	    }
	    public List<OwnerDetails> getOwnerDetails(){
	        return this.ownerDetails;
	    }
	    public void setCompanyName(String companyName){
	        this.companyName = companyName;
	    }
	    public String getCompanyName(){
	        return this.companyName;
	    }
}
