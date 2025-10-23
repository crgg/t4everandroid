package com.t4app.t4everandroid.main.Models;

import android.net.Uri;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Media implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("assistant_id")
    private String assistantId;

    @SerializedName("type")
    private String type;

    @SerializedName("transcription")
    private String transcription;

    @SerializedName("metadata")
    private String metaData;

    @SerializedName("date_upload")
    private String dateUpload;

    private transient Uri uri;
    private transient String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(String assistantId) {
        this.assistantId = assistantId;
    }

    public String getTranscription() {
        return transcription;
    }

    public void setTranscription(String transcription) {
        this.transcription = transcription;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public String getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(String dateUpload) {
        this.dateUpload = dateUpload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
