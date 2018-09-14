package com.example.a15051.placesearch;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Details implements Serializable {
    private String address; //formatted_address
    private String phoneNumber;//formatted_phone_number
    private String name;//name
    private String rating;//rating
    private String price;//price_level
    private String googlePage;//url
    private String website; //website
    private String placeId;
    private ArrayList<Review> reviews;
    private String city;
    private String states;
    private String icon;
    private double Lat;
    private double Lng;


    public Details(String address, String phoneNumber, String name, String rating, String price, String googlePage,
                   String website, String placeId, ArrayList<Review> reviews, String city, String states,String icon,double Lat,double Lng) {
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.rating = rating;
        this.price = price;
        this.googlePage = googlePage;
        this.website = website;
        this.placeId = placeId;
        this.reviews = reviews;
        this.city = city;
        this.states = states;
        this.icon = icon;
        this.Lat = Lat;
        this.Lng = Lng;

    }

    public String getAddress(){
        return address;
    }
    public String getPhoneNumber(){
        return phoneNumber;
    }
    public String getName(){
        return name;
    }
    public String getGooglePage(){
        return googlePage;
    }
    public String getWebsite(){
        return website;
    }
    public String getRating(){
        return rating;
    }
    public String getPrice(){
        return price;
    }
    public String getPlaceId(){
        return placeId;
    }
    public ArrayList getPlaceReviews(){
        return reviews;
    }
    public String getCity(){
        return city;
    }
    public String getStates(){
        return states;
    }
    public String getIcon(){
        return icon;
    }
    public double getLat(){
        return Lat;
    }
    public double getLng(){
        return Lng;
    }



}
