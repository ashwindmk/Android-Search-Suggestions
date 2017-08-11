package com.example.ashwin.searchsuggestions.models;

/**
 * Created by ashwin on 10/8/17.
 */

public class Suggestion {

    private String title = "", subtitle = "";

    public Suggestion() { }

    public Suggestion(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }
}
