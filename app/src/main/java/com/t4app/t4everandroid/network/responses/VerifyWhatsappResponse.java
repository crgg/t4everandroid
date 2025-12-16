package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.ui.chat.models.WhatsappMessage;

import java.io.Serializable;
import java.util.List;

public class VerifyWhatsappResponse implements Serializable {
    @SerializedName("message")
    public String message;

    @SerializedName("users")
    public List<String> users;

    @SerializedName("data")
    public List<WhatsappMessage> data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public List<WhatsappMessage> getData() {
        return data;
    }

    public void setData(List<WhatsappMessage> data) {
        this.data = data;
    }
}
