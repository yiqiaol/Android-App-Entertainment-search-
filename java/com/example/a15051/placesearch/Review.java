package com.example.a15051.placesearch;

import java.io.Serializable;

public class Review implements Serializable {
    private String reviewImg;
    private String reviewName;
    private String reviewRate;
    private String reviewTime;
    private String reviewContent;
    private String authorUrl;

    public Review(String reviewImg, String reviewName, String reviewRate, String reviewTime, String reviewContent,String authorUrl) {
        this.reviewImg = reviewImg;
        this.reviewName = reviewName;
        this.reviewRate = reviewRate;
        this.reviewTime = reviewTime;
        this.reviewContent = reviewContent;
        this.authorUrl = authorUrl;
    }
    public String getRevImg(){
        return reviewImg;
    }
    public String getRevName(){
        return reviewName;
    }
    public String getRevRate(){
        return reviewRate;
    }
    public String getRevTime(){
        return reviewTime;
    }
    public String getRevContent(){
        return reviewContent;
    }
    public String getAuthorUrl(){
        return authorUrl;
    }
}
