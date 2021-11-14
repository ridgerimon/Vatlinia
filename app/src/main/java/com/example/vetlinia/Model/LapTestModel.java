package com.example.vetlinia.Model;

public class LapTestModel
{
    String imageUrl;
    String description;
    String doctorComment;
    String key;

    public LapTestModel() {
    }

    public LapTestModel(String imageUrl, String description, String doctorComment, String key) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.doctorComment = doctorComment;
        this.key = key;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDoctorComment() {
        return doctorComment;
    }

    public void setDoctorComment(String doctorComment) {
        this.doctorComment = doctorComment;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
