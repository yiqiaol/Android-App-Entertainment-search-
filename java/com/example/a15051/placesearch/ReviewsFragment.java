package com.example.a15051.placesearch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReviewsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ReviewsAdapter adapter;
    private Spinner reviewType;
    private Spinner reviewOrder;
    private TextView noReviews;

    private List<Review> yelpReviews;
    private List<Review> reviews;
    private List<Review> reviews_default;
    private boolean hasReview = true;

    /*******************get yelp review*********************************/
    public void getYelpView(){
        String name= ((ThirdActivity)getActivity()).receiveDetails().getName();
        name = URLEncoder.encode(name);
        String city = ((ThirdActivity)getActivity()).receiveDetails().getCity();
        city = URLEncoder.encode(city);
        String states = ((ThirdActivity)getActivity()).receiveDetails().getStates();
        String address1 = ((ThirdActivity)getActivity()).receiveDetails().getAddress();
        address1 = URLEncoder.encode(address1);
        String url ="http://hw9-js.us-west-1.elasticbeanstalk.com/?name="+name+"&city="+city+"&state="+states+"&country=US&address1="+address1;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject myJsonObject = new JSONObject(response);
                            JSONArray reviews = myJsonObject.getJSONArray("reviews");
                            yelpReviews = new ArrayList<>();
                            for(int i = 0; i<reviews.length(); i++){
                                JSONObject info = reviews.getJSONObject(i);
                                JSONObject user = info.getJSONObject("user");
                                Review review = new Review(user.getString("image_url"),user.getString("name"),info.getString("rating"),info.getString("time_created"),info.getString("text"),info.getString("url"));
                                yelpReviews.add(review);
                            }
//                            actionsOnUiThread(); //send info on UI threads

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error","That didn't work!");
                Toast.makeText(getActivity(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        queue.add(stringRequest);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.reviews_fragment,null);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.reviewRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        reviewType = (Spinner) getActivity().findViewById(R.id.review_type);
        reviewOrder = (Spinner) getActivity().findViewById(R.id.review_order);
        noReviews = (TextView) getActivity().findViewById(R.id.no_reviews);

        getYelpView();
        setUI();
    }
    /***************************SetUI according to the button*****************************************************/
    public void setUI(){
        reviews = new ArrayList<>();
        reviews = ((ThirdActivity)getActivity()).receiveDetails().getPlaceReviews();
        reviews_default = new ArrayList<>(reviews);
        noReviews.setVisibility(View.VISIBLE);
        /***********************Spinner click and choose from google/yelp*******************************************/

        reviewType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] languages = getActivity().getResources().getStringArray(R.array.review_type);
                if(languages[pos].equals("Google reviews")){
                    reviews = ((ThirdActivity)getActivity()).receiveDetails().getPlaceReviews();
                    if(reviews.size()==0){
                        noReviews.setVisibility(View.VISIBLE);
                        Log.i("google","1");
                    }else{
                        noReviews.setVisibility(View.GONE);
                        Log.i("google","2");
                    }
                    reviews_default = new ArrayList<>(reviews);

                }else if(languages[pos].equals("Yelp Reviews")){
                    Log.i("yelp","3");
                    if(yelpReviews != null){
                        reviews.clear();
                        reviews = yelpReviews;
                        reviews_default = new ArrayList<>(reviews);
                        noReviews.setVisibility(View.GONE);
                    }else{
                        hasReview = false;
                        reviews = new ArrayList<>();
                        noReviews.setVisibility(View.VISIBLE);
                    }
                }
                adapter = new ReviewsAdapter(reviews,getContext());
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        /*********************Spinner click and sort the list*********************************************************/

        reviewOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String[] languages = getActivity().getResources().getStringArray(R.array.review_order);
                if(languages[pos].equals("Default Order")){
                    adapter = new ReviewsAdapter(reviews_default,getContext());
                }else{
                    if(languages[pos].equals("Highest Rating")){
                        Log.i("click","1");
                        Collections.sort(reviews, new Comparator<Review>() {
                            @Override
                            public int compare(Review o1, Review o2) {
                                if(Integer.valueOf(o1.getRevRate()) <= Integer.valueOf(o2.getRevRate())) {
                                    return 1;
                                }
                                else {
                                    return -1;
                                }
                            }
                        });
                    }else if(languages[pos].equals("Lowest Rating")){
                        Log.i("click","2");
                        Collections.sort(reviews, new Comparator<Review>() {
                            @Override
                            public int compare(Review o1, Review o2) {
                                if(Integer.valueOf(o1.getRevRate()) > Integer.valueOf(o2.getRevRate())) {
                                    return 1;
                                }
                                else {
                                    return -1;
                                }
                            }
                        });
                    }else if(languages[pos].equals("Most Recent")) {
                        Log.i("click","3");
                        Collections.sort(reviews, new Comparator<Review>() {
                            @Override
                            public int compare(Review o1, Review o2) {
                                int differ = 0 - o1.getRevTime().compareTo(o2.getRevTime());
                                return differ;
                            }
                        });
                    }else if(languages[pos].equals("Least Recent")) {
                        Log.i("click","4");
                        Collections.sort(reviews, new Comparator<Review>() {
                            @Override
                            public int compare(Review o1, Review o2) {
                                int differ = o1.getRevTime().compareTo(o2.getRevTime());
                                return differ;
                            }
                        });
                    }
                    adapter = new ReviewsAdapter(reviews,getContext());
                }
                if(reviews.size()!=0){
                    Log.i("changed1",reviews.get(0).getRevName());
                }else{
                    Log.i("changed1","error");
                }
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

    }
}
