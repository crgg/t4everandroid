package com.t4app.t4everandroid.main.Models;

public class CategoryItem {
    private String text;
    private int iconResId;
    private int colorId;

    public CategoryItem(String text, int iconResId, int colorId) {
        this.text = text;
        this.iconResId = iconResId;
        this.colorId = colorId;
    }

    public String getText() {
        return text;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getColorId() {
        return colorId;
    }
}
