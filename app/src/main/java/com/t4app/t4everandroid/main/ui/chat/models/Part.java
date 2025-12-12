package com.t4app.t4everandroid.main.ui.chat.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Part implements Serializable {

    @SerializedName("text")
    public String text;

    @SerializedName("inlineData")
    public InlineData inlineData;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InlineData getInlineData() {
        return inlineData;
    }

    public void setInlineData(InlineData inlineData) {
        this.inlineData = inlineData;
    }
}
