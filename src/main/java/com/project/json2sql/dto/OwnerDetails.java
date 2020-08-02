package com.project.json2sql.dto;

public class OwnerDetails
{
    private String firstName;

    private String lastName;

    private String ownerName;

    private String ownerAddress;

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
    public void setOwnerAddress(String ownerAddress){
        this.ownerAddress = ownerAddress;
    }
    public String getOwnerAddress(){
        return this.ownerAddress;
    }
}