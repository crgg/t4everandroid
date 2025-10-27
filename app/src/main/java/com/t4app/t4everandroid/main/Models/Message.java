package com.t4app.t4everandroid.main.Models;

public class Message {
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_PDF = 2;
    public static final int TYPE_AUDIO = 3;

    private String id;
    private int type;
    private String content; // texto o URL
    private boolean sentByUser;
    private String time;
    private boolean isRead;

    public Message(String id, int type, String content, boolean sentByUser, String time, boolean isRead) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.sentByUser = sentByUser;
        this.time = time;
        this.isRead = isRead;
    }

    public String getId() { return id; }
    public int getType() { return type; }
    public String getContent() { return content; }
    public boolean isSentByUser() { return sentByUser; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
}
