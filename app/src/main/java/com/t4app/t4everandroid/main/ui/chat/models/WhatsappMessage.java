package com.t4app.t4everandroid.main.ui.chat.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WhatsappMessage implements Serializable {

    @SerializedName("text")
    public String text;

    @SerializedName("username")
    public String username;

    @SerializedName("datetime")
    public String dateTime;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
