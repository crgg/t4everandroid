package com.t4app.t4everandroid.main.Models;

public class EmailTest {

    private String contactName;
    private String email;
    private String title;
    private String content;
    private String date;
    private String type;
    private boolean isFavorite;
    private boolean isSent;
    private boolean isDeleted;
    private boolean isSpam;
    private boolean isRead;

    public EmailTest(String contactName, String email,
                     String title, String content,
                     String date, String type,
                     boolean isFavorite, boolean isSent, boolean isDeleted, boolean isSpam, boolean isRead) {
        this.contactName = contactName;
        this.email = email;
        this.title = title;
        this.content = content;
        this.date = date;
        this.type = type;
        this.isFavorite = isFavorite;
        this.isSent = isSent;
        this.isDeleted = isDeleted;
        this.isSpam = isSpam;
        this.isRead = isRead;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
