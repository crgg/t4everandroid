package com.t4app.t4everandroid.main.Models;

public class EmailTest {

    private String contactName;
    private String title;
    private String content;
    private String date;
    private boolean isFavorite;

    public EmailTest(String contactName, String title, String content, String date, boolean isFavorite) {
        this.contactName = contactName;
        this.title = title;
        this.content = content;
        this.date = date;
        this.isFavorite = isFavorite;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
