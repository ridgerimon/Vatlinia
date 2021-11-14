package com.example.vetlinia.Model;

public class DoctorModel {
    private String fullname, email, mobilenumber, gender, hospitalLocation, specialization, imageurl;

    public DoctorModel() {
    }

    public DoctorModel(String fullname, String email, String mobilenumber, String gender, String hospitalLocation, String specialization, String imageurl) {
        this.fullname = fullname;
        this.email = email;
        this.mobilenumber = mobilenumber;
        this.gender = gender;
        this.hospitalLocation = hospitalLocation;
        this.specialization = specialization;
        this.imageurl = imageurl;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHospitalLocation() {
        return hospitalLocation;
    }

    public void setHospitalLocation(String hospitalLocation) {
        this.hospitalLocation = hospitalLocation;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
