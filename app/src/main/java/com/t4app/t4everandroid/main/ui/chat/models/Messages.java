package com.t4app.t4everandroid.main.ui.chat.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Messages implements Serializable {
    @SerializedName("_id")
    public String id;

    @SerializedName("parts")
    public List<Part> parts;

    @SerializedName("role")
    public String role;

    @SerializedName("userId")
    public Integer userId;

    @SerializedName("legacyProfileId")
    public String legacyProfileId;

    @SerializedName("isRead")
    public boolean isRead;

    @SerializedName("createdAt")
    public String createdAt;

    @SerializedName("updatedAt")
    public String updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getLegacyProfileId() {
        return legacyProfileId;
    }

    public void setLegacyProfileId(String legacyProfileId) {
        this.legacyProfileId = legacyProfileId;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
