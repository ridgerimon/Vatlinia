package com.example.vetlinia.Model;

public class AddAnimalModel {

    String name;
    String age;
    String bloodGroup;
    String dogPic1;
    String dogPic2;
    String sub;
    String species;
    String type;
    String notes;
    String gender;
    String myAnimalID;
    String userUID;

    public AddAnimalModel() {
    }

    public AddAnimalModel(String name, String age, String bloodGroup, String dogPic1, String dogPic2, String sub, String species, String type, String notes, String gender, String myAnimalID, String userUID) {
        this.name = name;
        this.age = age;
        this.bloodGroup = bloodGroup;
        this.dogPic1 = dogPic1;
        this.dogPic2 = dogPic2;
        this.sub = sub;
        this.species = species;
        this.type = type;
        this.notes = notes;
        this.gender = gender;
        this.myAnimalID = myAnimalID;
        this.userUID = userUID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getDogPic1() {
        return dogPic1;
    }

    public void setDogPic1(String dogPic1) {
        this.dogPic1 = dogPic1;
    }

    public String getDogPic2() {
        return dogPic2;
    }

    public void setDogPic2(String dogPic2) {
        this.dogPic2 = dogPic2;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMyAnimalID() {
        return myAnimalID;
    }

    public void setMyAnimalID(String myAnimalID) {
        this.myAnimalID = myAnimalID;
    }

    public String getUserUID() {
        return userUID;
    }

    public void setUserUID(String userUID) {
        this.userUID = userUID;
    }
}
