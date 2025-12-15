package com.t4app.t4everandroid.main.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.ui.chat.models.Messages;
import com.t4app.t4everandroid.room.converters.LegacyProfileConverter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Entity(tableName = "profiles")
@TypeConverters({LegacyProfileConverter.class})
public class LegacyProfile implements Serializable {
    @SerializedName("id")
    private String id;

    @PrimaryKey
    @SerializedName("user_id")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("state")
    private String state;

    @SerializedName("age")
    private int age;

    @SerializedName("avatar_path")
    private String avatarPath;

    @SerializedName("death_date")
    private String deathDate;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("family_relationship")
    private String familyRelationship;

    @SerializedName("alias")
    private String alias;

    @SerializedName("country")
    private String country;

    @SerializedName("language")
    private String language;

    @SerializedName("base_personality")
    private List<String> basePersonality;

    @SerializedName("date_creation")
    private String dateCreation;

    @SerializedName("open_session")
    private Session openSession;

    @SerializedName("last_session")
    private Session lastSession;

    @SerializedName("session_history")
    private List<Session> sessionHistory;

    @SerializedName("session_history_pagination")
    private SessionPagination sessionHistoryPagination;

    @SerializedName("big_five_answers")
    private List<Question> bigFiveAnswers;

    private transient Messages lastMessage;

    public Messages getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(Messages lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(String deathDate) {
        this.deathDate = deathDate;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getFamilyRelationship() {
        return familyRelationship;
    }

    public void setFamilyRelationship(String familyRelationship) {
        this.familyRelationship = familyRelationship;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getBasePersonality() {
        return basePersonality;
    }

    public void setBasePersonality(List<String> basePersonality) {
        this.basePersonality = basePersonality;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Session getOpenSession() {
        return openSession;
    }

    public void setOpenSession(Session openSession) {
        this.openSession = openSession;
    }

    public Session getLastSession() {
        return lastSession;
    }

    public void setLastSession(Session lastSession) {
        this.lastSession = lastSession;
    }

    public List<Session> getSessionHistory() {
        return sessionHistory;
    }

    public void setSessionHistory(List<Session> sessionHistory) {
        this.sessionHistory = sessionHistory;
    }

    public SessionPagination getSessionHistoryPagination() {
        return sessionHistoryPagination;
    }

    public void setSessionHistoryPagination(SessionPagination sessionHistoryPagination) {
        this.sessionHistoryPagination = sessionHistoryPagination;
    }

    public List<Question> getBigFiveAnswers() {
        return bigFiveAnswers;
    }

    public void setBigFiveAnswers(List<Question> bigFiveAnswers) {
        this.bigFiveAnswers = bigFiveAnswers;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof LegacyProfile)) return false;
        LegacyProfile other = (LegacyProfile) obj;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
