package com.t4app.t4everandroid.main.Models;

public class ListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private int type;
    private String category;
    private QuestionTest question;

    public ListItem(int type, String category, QuestionTest question) {
        this.type = type;
        this.category = category;
        this.question = question;
    }

    public int getType() { return type; }
    public String getCategory() { return category; }
    public QuestionTest getQuestion() { return question; }

}
