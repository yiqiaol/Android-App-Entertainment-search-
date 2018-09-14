package com.example.a15051.placesearch;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataResult;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.PlacePhotoResult;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PhotosFragment extends Fragment {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Bitmap> detailPhotos;
    private TextView noPhotos;
    private GeoDataClient mGeoDataClient;
    private GoogleApiClient mGoogleApiClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String placeId = ((ThirdActivity)getActivity()).receiveDetails().getPlaceId();
        detailPhotos = new ArrayList<>();
        mGoogleApiClient = ((ThirdActivity) getActivity()).getmGoogleApiClient();
        placePhotosAsync(placeId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photos_fragment,null);
        noPhotos = (TextView)view.findViewById(R.id.no_photos);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.photoRecyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if(detailPhotos.size() == 0){
            noPhotos.setVisibility(View.VISIBLE);
            detailPhotos = new ArrayList<>();
        }else{
            noPhotos.setVisibility(View.GONE);
        }
        adapter = new PhotosAdapter(detailPhotos);
        recyclerView.setAdapter(adapter);
    }

    private ResultCallback<PlacePhotoResult> mDisplayPhotoResultCallback
            = new ResultCallback<PlacePhotoResult>() {
        @Override
        public void onResult(PlacePhotoResult placePhotoResult) {
            if (!placePhotoResult.getStatus().isSuccess()) {
                return;
            }

            detailPhotos.add(placePhotoResult.getBitmap());
            noPhotos.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();

        }
    };


    private void placePhotosAsync(String placeId) {
        Places.GeoDataApi.getPlacePhotos(mGoogleApiClient, placeId)
                .setResultCallback(new ResultCallback<PlacePhotoMetadataResult>() {


                    @Override
                    public void onResult(PlacePhotoMetadataResult photos) {
                        if (!photos.getStatus().isSuccess()) {
                            return;
                        }
                        PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                        if (photoMetadataBuffer.getCount() > 0) {
                            for (int i = 0; i < photoMetadataBuffer.getCount(); i++) {
                                photoMetadataBuffer.get(i)
                                        .getScaledPhoto(mGoogleApiClient, 1600,
                                                1600)
                                        .setResultCallback(mDisplayPhotoResultCallback);
                            }
                        }
                        photoMetadataBuffer.release();
                    }

                });
    }
}


