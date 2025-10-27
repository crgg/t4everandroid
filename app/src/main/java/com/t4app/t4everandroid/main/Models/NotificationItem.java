package com.t4app.t4everandroid.main.Models;

public class NotificationItem {
    private String id;
    private String title;
    private String message;
    private String date;
    private String actionText;
    private boolean isChecked;

    public NotificationItem(String id, String title, String message, String date, String actionText, boolean isChecked) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.date = date;
        this.actionText = actionText;
        this.isChecked = isChecked;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
    public String getActionText() { return actionText; }
    public boolean isChecked() { return isChecked; }

    public void setChecked(boolean checked) { isChecked = checked; }
}
