package com.example.a15051.placesearch;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,OnMapReadyCallback{
    private AutoCompleteTextView mapFrom;
    private autoCompleteAdapter PlaceAutoCompleteAdapter;
    private GoogleApiClient mGoogleApiClient;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Spinner travelMode;
    private String mode;
    private String serverKey ="AIzaSyDjCwxl4ravtviNK5jIi9naKbIDVkwEYbw";
    private LatLng origin = new LatLng(0,0);
    private LatLng destination;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-40, -168), new LatLng(71, 136));

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, null);
        //implement a map
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.whole_map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.whole_map, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

        //AutoComplete
        mapFrom = (AutoCompleteTextView) view.findViewById(R.id.map_from);
        mGoogleApiClient = ((ThirdActivity) getActivity()).getmGoogleApiClient();
        PlaceAutoCompleteAdapter = new autoCompleteAdapter(getActivity(), mGoogleApiClient,
                LAT_LNG_BOUNDS, null);

        //initialize the button
        double lat = ((ThirdActivity)getActivity()).receiveDetails().getLat();
        double lng = ((ThirdActivity)getActivity()).receiveDetails().getLng();
        destination = new LatLng(lat,lng);
        travelMode = (Spinner) view.findViewById(R.id.travel_mode);
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapFrom.setAdapter(PlaceAutoCompleteAdapter);
        mapFrom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("click","1");
                mMap.clear();
                geoLocate();
                requestDirection();
            }
        });

        /******************************************************************/
        travelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] languages = getActivity().getResources().getStringArray(R.array.travel_mode);
                if(languages[pos].equals("Driving")){
                    mode = TransportMode.DRIVING;
                    Log.i("start","1");
                    if(mapFrom!=null ){
                        requestDirection();
                    }

                }else if(languages[pos].equals("Bicycling")){
                    mode = TransportMode.BICYCLING;
                    if(mapFrom!=null ){
                        requestDirection();
//                        mMap.clear();
                    }
                }else if(languages[pos].equals("Transit")){
                    mode = TransportMode.TRANSIT;
                    if(mapFrom!=null ){
                        requestDirection();
//                        mMap.clear();
                    }

                }else if(languages[pos].equals("Walking")){
                    mode = TransportMode.WALKING;
                    if(mapFrom!=null ){
                        requestDirection();
//                        mMap.clear();
                    }

                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(destination));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination,12));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }



    public void requestDirection() {
        Log.i("start","4");
        if(mMap != null) {
            mMap.clear();
        }
        GoogleDirection.withServerKey(serverKey)
                .from(origin)
                .to(destination)
                .transportMode(mode)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {

                        String status = direction.getStatus();
                        Log.i("start",status);
                        if (direction.isOK()) {
                            Route route = direction.getRouteList().get(0);
                            mMap.addMarker(new MarkerOptions().position(origin));
                            mMap.addMarker(new MarkerOptions().position(destination));
                            Log.i("start","2");
                            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
                            mMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                            setCameraWithCoordinationBounds(route);
                        }else{
                            Log.i("start","5");
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {

                    }
                });

    }


    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 12));
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void geoLocate(){
        String searchString = mapFrom.getText().toString();
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
            Log.i("get origin","1");
        }catch (IOException e){
            Log.e("error", "geoLocate: IOException: " + e.getMessage() );
        }
        if(list.size() > 0){
            Address address = list.get(0);
            origin = new LatLng(address.getLatitude(), address.getLongitude());
            Log.i("get origin","2");
        }
    }
}
