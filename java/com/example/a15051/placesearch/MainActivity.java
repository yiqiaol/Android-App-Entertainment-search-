package com.example.a15051.placesearch;

import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private List<Fragment> list;
    private PagerAdapter adapter;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        myTabLayout = (TabLayout) findViewById(R.id.tvtablayout);
        myViewPager = (ViewPager) findViewById(R.id.tvviewpager);
        list = new ArrayList<>();
        list.add(new SearchFragment());
        list.add(new FavoriteFragment());

        adapter = new pageAdapter(getSupportFragmentManager(),list);
        myViewPager.setAdapter(adapter);

        myTabLayout.addTab(myTabLayout.newTab());
        myTabLayout.addTab(myTabLayout.newTab());
        myTabLayout.setupWithViewPager(myViewPager);

        myTabLayout.getTabAt(0).setCustomView(getTabView("SEARCH",R.drawable.search));
        myTabLayout.getTabAt(1).setCustomView(getTabView("FAVORITES",R.drawable.favorites_fill_white));

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }
    public View getTabView(String str, int image_id) {
        //change tab xml to view
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item, null);
        TextView txt_title = (TextView) view.findViewById(R.id.tab_item_text);
        txt_title.setText(str);
        ImageView img_title = (ImageView) view.findViewById(R.id.tab_item_img);
        img_title.setImageResource(image_id);
        return view;
    }

    public GoogleApiClient getmGoogleApiClient(){
        return mGoogleApiClient;
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
