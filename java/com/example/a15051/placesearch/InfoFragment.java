package com.example.a15051.placesearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.seatgeek.placesautocomplete.model.Place;

public class InfoFragment extends Fragment {
    private TextView address;
    private TextView phone;
    private TextView price;
    private RatingBar rate;
    private TextView googlePage;
    private TextView website;
    private Details PlaceDetails;
    private LinearLayout addressLayout;
    private LinearLayout phoneLayout;
    private LinearLayout priceLayout;
    private LinearLayout rateLayout;
    private LinearLayout googlePageLayout;
    private LinearLayout websiteLayout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment,null);
        address = (TextView) view.findViewById(R.id.address);
        phone = (TextView) view.findViewById(R.id.phone_number);
        price = (TextView) view.findViewById(R.id.price_level);
        rate = (RatingBar) view.findViewById(R.id.rating);
        googlePage = (TextView) view.findViewById(R.id.google_page);
        website = (TextView) view.findViewById(R.id.websites);
        addressLayout = (LinearLayout)view.findViewById(R.id.address_layout);
        phoneLayout=(LinearLayout)view.findViewById(R.id.phone_layout);
        priceLayout=(LinearLayout)view.findViewById(R.id.price_layout);
        rateLayout=(LinearLayout)view.findViewById(R.id.rate_layout);
        googlePageLayout=(LinearLayout)view.findViewById(R.id.googlePage_layout);
        websiteLayout=(LinearLayout)view.findViewById(R.id.website_layout);
        PlaceDetails = ((ThirdActivity)getContext()).receiveDetails();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //address
        if(PlaceDetails.getAddress().equals("")){
            addressLayout.setVisibility(View.GONE);
        }else{
            address.setText(PlaceDetails.getAddress());
        }
        //phone
        if(PlaceDetails.getPhoneNumber().equals("")){
            phoneLayout.setVisibility(View.GONE);
        }else{
            phone.setText(PlaceDetails.getPhoneNumber());
        }

        //Price
        if(!PlaceDetails.getPrice().equals("")){
            String priceLevel = "";
            for(int i = 0; i< Integer.valueOf(PlaceDetails.getPrice());i++){
                priceLevel += "$";
            }
            price.setText(priceLevel);
        }else{
            priceLayout.setVisibility(View.GONE);
        }

        //rate
        if(PlaceDetails.getRating().equals("")){
            rateLayout.setVisibility(View.GONE);
        }else{
            rate.setRating(Float.valueOf(PlaceDetails.getRating()));
        }
        //google page
        if(PlaceDetails.getGooglePage().equals("")){
            googlePageLayout.setVisibility(View.GONE);
        }else{
            googlePage.setText(PlaceDetails.getGooglePage());
        }
        //website
        if(PlaceDetails.getWebsite().equals("")){
            websiteLayout.setVisibility(View.GONE);
        }else {
            website.setText(PlaceDetails.getWebsite());
        }
    }
}
