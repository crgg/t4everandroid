package com.t4app.t4everandroid.main.ui.chat.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class InlineData implements Serializable {
    @SerializedName("mimeType")
    public String mimeType;

    @SerializedName("fileUrl")
    public String fileUrl;

    @SerializedName("fileKey")
    public String fileKey;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getFileKey() {
        return fileKey;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }
}
