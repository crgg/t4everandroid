package com.t4app.t4everandroid.Login.models;

public class OnboardingItem {
    private final int imageRes;
    private final String title;
    private final String description;

    public OnboardingItem(int imageRes, String title, String description) {
        this.imageRes = imageRes;
        this.title = title;
        this.description = description;
    }

    public int getImageRes() { return imageRes; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
}

