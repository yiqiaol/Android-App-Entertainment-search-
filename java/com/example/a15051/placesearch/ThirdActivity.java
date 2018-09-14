package com.example.a15051.placesearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.places.Places;


public class ThirdActivity extends AppCompatActivity implements OnConnectionFailedListener{
//public class ThirdActivity extends AppCompatActivity implements OnConnectionFailedListener{
    private ViewPager DetailViewPager;
    private TabLayout DetailTabLayout;
    private List<Fragment> detailList;
    private pageAdapter adapter;
    private GoogleApiClient mGoogleApiClient;

    private Details details;
    private List<Bitmap> photos;

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.i("check","1");
        //change the title label dynamically according to the intent
        super.onCreate(savedInstanceState);
        setContentView(R.layout.third_activity);
        /* 显示App icon左侧的back键 */
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        /********************get the data from adapter**********************************/
        Intent intent = getIntent();
        details = (Details)intent.getSerializableExtra("detailData");

        /************************Initialize the action bar******************************/
        String detail_name = details.getName();
        setTitle(detail_name);
        getSupportActionBar().setElevation(0);

        /***********************Connect the tablayout************************************/
        //get this two element
        DetailViewPager = (ViewPager) findViewById(R.id.detailviewpager);
        DetailTabLayout = (TabLayout) findViewById(R.id.detaillayout);
        DetailTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        //add four fragment into the list
        detailList = new ArrayList<>();
        detailList.add(new InfoFragment());
        detailList.add(new PhotosFragment());
        detailList.add(new MapFragment());
        detailList.add(new ReviewsFragment());

        DetailTabLayout.addTab(DetailTabLayout.newTab());
        DetailTabLayout.addTab(DetailTabLayout.newTab());
        DetailTabLayout.addTab(DetailTabLayout.newTab());
        DetailTabLayout.addTab(DetailTabLayout.newTab());

        adapter = new pageAdapter(getSupportFragmentManager(),detailList);
        DetailViewPager.setAdapter(adapter);
        DetailTabLayout.setupWithViewPager(DetailViewPager);

        //put the divider
        LinearLayout linearLayout = (LinearLayout) DetailTabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this,R.drawable.divider));

        DetailTabLayout.getTabAt(0).setCustomView(getTabView("INFO",R.drawable.info));
        DetailTabLayout.getTabAt(1).setCustomView(getTabView("PHOTOS",R.drawable.photos));
        DetailTabLayout.getTabAt(2).setCustomView(getTabView("MAP",R.drawable.map));
        DetailTabLayout.getTabAt(3).setCustomView(getTabView("REVIEWS",R.drawable.reviews));
        Log.i("check","3");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.third_menu,menu);

        SharedPreferences mSharedPreferences = getSharedPreferences("fav1", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        if(mSharedPreferences.contains(details.getPlaceId())){
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.favorites_fill_white);
        }else{
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.favorite_empty_white);

        }
        return super.onCreateOptionsMenu(menu);
    }

    private void storeFav(Details resultList){
        String id = resultList.getPlaceId();
        String icon = resultList.getIcon();
        String name = resultList.getName();
        String loc = resultList.getAddress();

        String nextToken = "#";
        String lat = resultList.getLat()+"";
        String lng = resultList.getLng()+"";
        String fav_info = icon+"{"+id+"{"+name+"{"+loc+"{"+nextToken+"{"+lat+"{"+lng;
        SharedPreferences mSharedPreferences = getSharedPreferences("fav1",Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(id,fav_info);
        mEditor.apply();
    }
    private void removeFav(Details resultList){
        SharedPreferences mSharedPreferences = getSharedPreferences("fav1",Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.remove(resultList.getPlaceId());
        mEditor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_share:
                Toast.makeText(this,"share by twitter",Toast.LENGTH_LONG).show();
                String src = "https://twitter.com/intent/tweet?text=Check out "+details.getName()+" located at "+details.getAddress()+" . Website: "+ details.getGooglePage()+" #TravelAndEntertainmentSearch";
                Uri uri = Uri.parse(src);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            case R.id.action_favorite:
                SharedPreferences mSharedPreferences = getSharedPreferences("fav1", Context.MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                if(mSharedPreferences.contains(details.getPlaceId())){
                    item.setIcon(R.drawable.favorite_empty_white);
                    Toast.makeText(this, details.getName()+ "was moved from favorites", Toast.LENGTH_LONG).show();
                    removeFav(details);
                }else{
                    item.setIcon(R.drawable.favorites_fill_white);
                    Toast.makeText(this, details.getName()+ "was added to favorites", Toast.LENGTH_LONG).show();
                    storeFav(details);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //this method offer a public function for other fragment to use
    public Details receiveDetails(){
        return details;
    }
    public GoogleApiClient getmGoogleApiClient(){
        return mGoogleApiClient;
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
}
