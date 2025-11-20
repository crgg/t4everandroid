package com.t4app.t4everandroid.main.Models;

public class CategoryItem {
    private String text;
    private int iconResId;

    public CategoryItem(String text, int iconResId) {
        this.text = text;
        this.iconResId = iconResId;
    }

    public String getText() {
        return text;
    }

    public int getIconResId() {
        return iconResId;
    }
}
