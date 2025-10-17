package com.t4app.t4everandroid.main.Models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class Scale {
    @SerializedName("min")
    private int min;

    @SerializedName("max")
    private int max;

    @SerializedName("labels")
    private Map<String, String> labels;

    @SerializedName("hint")
    private String hint;

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public Map<String, String> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, String> labels) {
        this.labels = labels;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
