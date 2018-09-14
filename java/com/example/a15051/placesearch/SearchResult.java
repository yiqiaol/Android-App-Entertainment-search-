package com.example.a15051.placesearch;

import java.io.Serializable;

public class SearchResult implements Serializable{
    private String icon_url;
    private String id;
    private String name;
    private String vicinity;
    private String nextToken;
    private double Lat;
    private double Lng;



    public SearchResult(String icon_url,String id, String name,String vicinity,String nextToken,double Lat,double Lng ) {
        this.icon_url = icon_url;
        this.id = id;
        this.name = name;
        this.vicinity = vicinity;
        this.nextToken = nextToken;
        this.Lat = Lat;
        this.Lng = Lng;
    }
    public String getIcon_url()
    {
        return icon_url;
    }
    public String getId()
    {
        return id;
    }
    public String getName()
    {
        return name;
    }
    public String getVicinity()
    {
        return vicinity;
    }
    public String getNextToken(){
        return nextToken;
    }
    public double getLat(){
        return Lat;
    }
    public double getLng(){
        return Lng;
    }
}
