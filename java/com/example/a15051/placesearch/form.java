package com.example.a15051.placesearch;

import java.io.Serializable;

public class form implements Serializable{
    private String keyword;
    private String category;
    private String distance;
    private String location;

    public form(String keyword,String category,String distance, String location){
       this.keyword = keyword;
       this.category = category;
       this.distance = distance;
       this.location = location;
    }
    public String getKeyword(){
        return keyword;
    }
    public String getCategory(){
        return category;
    }
    public String getDistance(){
        return distance;
    }
    public String getLocation(){
        return location;
    }
}
