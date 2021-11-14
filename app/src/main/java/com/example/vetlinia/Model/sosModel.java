package com.example.vetlinia.Model;

public class sosModel
{
    String lat;
    String Long;
    String Address;
    String animalKey;
    String patientID;
    String Key;
    String isAccepted;
    String message;

    public sosModel() {
    }

    public sosModel(String lat, String aLong, String address, String animalKey, String patientID, String key, String isAccepted, String message) {
        this.lat = lat;
        this.Long = aLong;
        this.Address = address;
        this.animalKey = animalKey;
        this.patientID = patientID;
        this.Key = key;
        this.isAccepted = isAccepted;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(String isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return Long;
    }

    public void setLong(String aLong) {
        Long = aLong;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getAnimalKey() {
        return animalKey;
    }

    public void setAnimalKey(String animalKey) {
        this.animalKey = animalKey;
    }

    public String getKey() {
        return Key;
    }

    public void setKey(String key) {
        Key = key;
    }
}
