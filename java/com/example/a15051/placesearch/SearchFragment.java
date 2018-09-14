package com.example.a15051.placesearch;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.seatgeek.placesautocomplete.DetailsCallback;
import com.seatgeek.placesautocomplete.OnPlaceSelectedListener;
import com.seatgeek.placesautocomplete.model.Place;
import com.seatgeek.placesautocomplete.model.PlaceDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



public class SearchFragment extends Fragment implements OnMapReadyCallback {
    private Button buttonSearch;
    private Button button_clear;
    private EditText keywords;
    private EditText distance;
    private AutoCompleteTextView location;
    private autoCompleteAdapter PlaceAutoCompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));

    private RadioGroup fromGroup;
    private RadioButton current;
    private RadioButton other;
    private boolean fromCurrentLoc;
    private Spinner category;
    private TextView error_keyword;
    private TextView error_from;
    private List<SearchResult> searchResults;
    private String place_keywords;
    private String place_location;
    private String address;

    private FusedLocationProviderClient mFusedLocationClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private LocationRequest mLocationRequest ;
    private Location currentLocation;

    private Boolean mLocationPermissionsGranted = false;
    private String currentPointInfo;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private static final String TAG = "MapActivity";
    String[] Category = {"default","airport","amusement_park","aquarium","art_gallery","bakery","bar","beauty_salon","bowling_alley","bus_station","cafe","campground","car_rental","casino","lodging","movie_theater","museum","night_club","park","parking","restaurant","shopping_mall","stadium","subway_station","taxi_stand","train_station","transit_station","travel_agency","zoo"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getLocationPermission();
        View view = inflater.inflate(R.layout.search_fragment, null);
        keywords = (EditText) view.findViewById(R.id.keywords);
        distance = (EditText) view.findViewById(R.id.distance);
        location = (AutoCompleteTextView) view.findViewById(R.id.location);
        category = (Spinner) view.findViewById(R.id.category);
        fromGroup = (RadioGroup) view.findViewById(R.id.radioGroupId);
        current = (RadioButton) view.findViewById(R.id.radioButton);
        other = (RadioButton) view.findViewById(R.id.radioButton2);
        error_keyword = (TextView) view.findViewById(R.id.error_keyword);
        error_from = (TextView) view.findViewById(R.id.error_from);
        error_keyword.setVisibility(View.GONE);
        error_from.setVisibility(View.GONE);
        /*********************Initialization************************************/
        current.setChecked(true);
        other.setChecked(false);
        fromCurrentLoc = true;
        location.setEnabled(false);
        RadioGroupListener listener = new RadioGroupListener();
        fromGroup.setOnCheckedChangeListener(listener);

        /******************set adapter for autocomplete text view**************/
        //AutoComplete
        GoogleApiClient mGoogleApiClient = ((MainActivity) getActivity()).getmGoogleApiClient();
        PlaceAutoCompleteAdapter = new autoCompleteAdapter(getActivity(), mGoogleApiClient,
                LAT_LNG_BOUNDS, null);
        location.setAdapter(PlaceAutoCompleteAdapter);
        return view;

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            // TODO Auto-generated method stub
            switch (checkedId) {
                case R.id.radioButton:
                    fromCurrentLoc = true;
                    current.setChecked(true);
                    location.setEnabled(false);
                    error_from.setVisibility(View.GONE);
                    break;
                case R.id.radioButton2:
                    fromCurrentLoc = false;
                    other.setChecked(true);
                    location.setEnabled(true);
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = (Button) getActivity().findViewById(R.id.buttonSearch);
        Button button_clear = (Button) getActivity().findViewById(R.id.buttonClear);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                place_keywords = keywords.getText().toString();
                Log.i("place_keywords","a"+place_keywords+"b");
                if(location==null){
                    place_location="";
                }else{
                    place_location = location.getText().toString();
                }

                if (TextUtils.isEmpty(keywords.getText().toString().trim())|| (fromCurrentLoc == false && TextUtils.isEmpty(location.getText()))) {
                    if(TextUtils.isEmpty(keywords.getText().toString().trim())) {
                        error_keyword.setVisibility(View.VISIBLE);
                    }
                    if(fromCurrentLoc == false &&  TextUtils.isEmpty(location.getText().toString().trim())) {
                        error_from.setVisibility(View.VISIBLE);
                    }
                    Toast.makeText(getActivity(), "Please fix all fields with errors", Toast.LENGTH_LONG).show();
                }else {
                    error_keyword.setVisibility(View.GONE);
                    error_keyword.setVisibility(View.GONE);
                    SearchForResult();
                }
            }

        });
        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Initialization", Toast.LENGTH_LONG).show();
                current.setChecked(true);
                other.setChecked(false);
                fromCurrentLoc = true;
                location.setEnabled(false);
                keywords.setText("");
                distance.setText("");
                category.setSelection(0);
                location.setText("");
                error_keyword.setVisibility(View.GONE);
                error_from.setVisibility(View.GONE);
            }
        });

    }
    private void actionsOnUiThread(final List<SearchResult> locationsList){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getActivity(), SecondActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("data",(Serializable)locationsList);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
    }
    public void SearchForResult(){
        //1.get location
//        address = place_location;
        Log.i("address",place_location);
        //2.get radius
        double length = 0;
        if(distance.getText().toString().equals("")){
            length = 10*1600;
        }else{
            length = Double.valueOf(distance.getText().toString())*1600;
        }
        String place_distance = String.valueOf(length);
        //3.get category
        String place_category = category.getSelectedItem().toString();
        long intex = category.getSelectedItemId();

        String actual_category = Category[(int)intex];

        /*******************Set the progress bar************************************/
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching results");
        progressDialog.show();
        /*******************HttpRequest********************************************/
        String url = "";
        searchResults = new ArrayList<>();
        if(fromCurrentLoc == false){
            url ="http://hw9-js.us-west-1.elasticbeanstalk.com/?Location="+place_location+"&keyword="+place_keywords+"&type="+actual_category+"&radius="+place_distance;
        }else{
            url ="http://hw9-js.us-west-1.elasticbeanstalk.com/?location="+currentPointInfo+"&keyword="+place_keywords+"&type="+actual_category+"&radius="+place_distance;
        }
        Log.i("url",url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try{
                            JSONObject myJsonObject = new JSONObject(response);
                            String next_page = "";
                            if(myJsonObject.has("next_page_token")){
                                next_page = myJsonObject.get("next_page_token").toString();
                            }
                            JSONArray jsonArray = myJsonObject.getJSONArray("results");
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject each = jsonArray.getJSONObject(i);
                                double Lat = each.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                double Lng = each.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                SearchResult searchResult = new SearchResult(each.get("icon").toString(),each.get("place_id").toString(),each.get("name").toString(),each.get("vicinity").toString(),next_page,Lat,Lng);
                                searchResults.add(searchResult);
                            }
                            actionsOnUiThread(searchResults); //send info on UI threads
                        }catch (Exception e){

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }



    private LocationCallback mLocationCallback = new LocationCallback(){
        @Override
        public void onLocationResult(LocationResult locationResult) {
            currentLocation=locationResult.getLastLocation();
            currentPointInfo = currentLocation.getLatitude()+","+currentLocation.getLongitude();
            Log.i("position",currentPointInfo);

        }
    };


    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current locationOriginal");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,mLocationCallback, Looper.myLooper());

    }
    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting locationOriginal permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(getActivity(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(getActivity(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                getDeviceLocation();
            }else{
                ActivityCompat.requestPermissions(getActivity(),
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

}
