package com.example.vetlinia.Model;

public class PatientModel {
    String fullName;
    String patientUserId;
    String email;
    String birthdate;
    String closeMobileNumber;
    String mobileNumber;
    String address;
    String imageUrl;

    public PatientModel() {
    }

    public PatientModel(String fullName, String patientUserId, String email, String birthdate, String closeMobileNumber, String mobileNumber, String address, String imageUrl) {
        this.fullName = fullName;
        this.patientUserId = patientUserId;
        this.email = email;
        this.birthdate = birthdate;
        this.closeMobileNumber = closeMobileNumber;
        this.mobileNumber = mobileNumber;
        this.address = address;
        this.imageUrl = imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPatientUserId() {
        return patientUserId;
    }

    public void setPatientUserId(String patientUserId) {
        this.patientUserId = patientUserId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCloseMobileNumber() {
        return closeMobileNumber;
    }

    public void setCloseMobileNumber(String closeMobileNumber) {
        this.closeMobileNumber = closeMobileNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
