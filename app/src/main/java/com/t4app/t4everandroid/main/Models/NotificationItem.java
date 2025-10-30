package com.t4app.t4everandroid.main.Models;

public class NotificationItem {
    private String id;
    private String title;
    private String message;
    private String date;
    private String actionText;
    private String type;
    private boolean isChecked;
    private boolean isRead;

    public NotificationItem(String id,
                            String title,
                            String message, String date, String actionText, String type,
                            boolean isChecked, boolean isRead) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.actionText = actionText;
        this.type = type;
        this.isChecked = isChecked;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
    public String getActionText() { return actionText; }
    public boolean isChecked() { return isChecked; }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public void setChecked(boolean checked) { isChecked = checked; }
}
