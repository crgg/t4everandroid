package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProfileRequest {
    @SerializedName("alias")
    private String alias;

    @SerializedName("age")
    private int age;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("death_date")
    private String deathDate;

    @SerializedName("family_relationship")
    private String familyRelationship;

    @SerializedName("base_personality")
    private List<String> basePersonality;

    @SerializedName("is_active")
    private boolean isActive;

    @SerializedName("language")
    private String language;

    @SerializedName("country")
    private String country;

    @SerializedName("name")
    private String name;

    public ProfileRequest(String alias, int age, String birthDate, String deathDate, String familyRelationship, List<String> basePersonality, boolean isActive, String language, String country, String name) {
        this.alias = alias;
        this.age = age;
        this.birthDate = birthDate;
        this.deathDate = deathDate;
        this.familyRelationship = familyRelationship;
        this.basePersonality = basePersonality;
        this.isActive = isActive;
        this.language = language;
        this.country = country;
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getFamilyRelationship() {
        return familyRelationship;
    }

    public void setFamilyRelationship(String familyRelationship) {
        this.familyRelationship = familyRelationship;
    }

    public List<String> getBasePersonality() {
        return basePersonality;
    }

    public void setBasePersonality(List<String> basePersonality) {
        this.basePersonality = basePersonality;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
