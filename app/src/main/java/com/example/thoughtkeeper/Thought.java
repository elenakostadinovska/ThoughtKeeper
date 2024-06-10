package com.example.thoughtkeeper;


import com.google.firebase.Timestamp;

public class Thought {
    //variables
    String title;
    String content;
    Timestamp timestamp;
    String mood;

    //def constructor
    public Thought() {
    }

    //allow other classes to access and modify the properties
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}





