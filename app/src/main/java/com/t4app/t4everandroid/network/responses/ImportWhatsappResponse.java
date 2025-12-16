package com.t4app.t4everandroid.network.responses;

import com.google.gson.annotations.SerializedName;
import com.t4app.t4everandroid.main.ui.chat.models.Messages;

import java.io.Serializable;
import java.util.List;

public class ImportWhatsappResponse implements Serializable {
    @SerializedName("message")
    public String message;

    @SerializedName("users")
    public List<String> users;

    @SerializedName("importedCount")
    public Integer importedCount;

    @SerializedName("data")
    public List<Messages> data;

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

    public Integer getImportedCount() {
        return importedCount;
    }

    public void setImportedCount(Integer importedCount) {
        this.importedCount = importedCount;
    }

    public List<Messages> getData() {
        return data;
    }

    public void setData(List<Messages> data) {
        this.data = data;
    }
}
