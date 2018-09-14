package com.example.a15051.placesearch;

//import android.support.v7.app.AlertController;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ResultListViewAdapter extends RecyclerView.Adapter<ResultListViewAdapter.ViewHolder> {
    private List<SearchResult> data; //ResultList replace the todo
    private Context context;
//    private SearchResult resultList;

    public ResultListViewAdapter(List<SearchResult> data,Context context) {
        this.data = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_list_item, parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final SearchResult resultList = data.get(position);
        Log.i("message",position+"");
        holder.placeName.setText(resultList.getName());
        Log.i("changed","strange");
        holder.placeLoc.setText(resultList.getVicinity());
        Picasso.get().load(resultList.getIcon_url()).into(holder.placeIcon);
        /*****initialize the result list by SharedPreference************************/
        SharedPreferences mSharedPreferences = context.getSharedPreferences("fav1",Context.MODE_PRIVATE);
        if(mSharedPreferences.contains(resultList.getId())){
            holder.favButton.setImageDrawable(context.getResources().getDrawable(R.drawable.favorites_fill_red));
        }else{
            holder.favButton.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_empty_black));
        }
        /*****************detail on click*****************************************/
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place_id = resultList.getId();
                Log.i("clicked placeName",resultList.getName());
                getDetail(place_id,resultList);
            }
        });

        holder.favButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences mSharedPreferences = context.getSharedPreferences("fav1",Context.MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                if(mSharedPreferences.contains(resultList.getId())){
                    holder.favButton.setImageDrawable(context.getResources().getDrawable(R.drawable.favorite_empty_black));
                    Toast.makeText(context, resultList.getName()+ "was moved from favorites", Toast.LENGTH_LONG).show();
                    removeFav(resultList);
                }else{
                    holder.favButton.setImageDrawable(context.getResources().getDrawable(R.drawable.favorites_fill_red));
                    Toast.makeText(context, resultList.getName()+ "was added to favorites", Toast.LENGTH_LONG).show();
                    storeFav(resultList);
                }
            }
        });

    }
    private void storeFav(SearchResult resultList){
        String id = resultList.getId();
        String icon = resultList.getIcon_url();
        String name = resultList.getName();
        String loc = resultList.getVicinity();
        String nextToken = resultList.getNextToken();
        String Lat=resultList.getLat()+"";

        String Lng=resultList.getLng()+"";
        String fav_info = icon+"{"+id+"{"+name+"{"+loc+"{"+nextToken+"{"+Lat+"{"+Lng;
        SharedPreferences mSharedPreferences = context.getSharedPreferences("fav1",Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putString(id,fav_info);
        mEditor.apply();
    }
    private void removeFav(SearchResult resultList){
        SharedPreferences mSharedPreferences = context.getSharedPreferences("fav1",Context.MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.remove(resultList.getId());
        mEditor.apply();
    }


    private void actionsOnUiThread(final Details locationsList){
        ((SecondActivity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.i("run","success");
                Intent intent = new Intent(context, ThirdActivity.class);
                Bundle bundle=new Bundle();
                bundle.putSerializable("detailData",(Serializable)locationsList);
                intent.putExtras(bundle);
                context.startActivity(intent);
                Log.i("send","data");
            }
        });
    }


    public void getDetail(String placeId,SearchResult a){
        final SearchResult resultList = a;
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Fetching details");
        progressDialog.show();

        String url ="http://hw9-js.us-west-1.elasticbeanstalk.com/?placeid="+placeId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("details",response);
                        progressDialog.dismiss();
                        try{
                            Log.i("response",response);
                            JSONObject myJsonObject = new JSONObject(response);
                            JSONObject object = myJsonObject.getJSONObject("result");
                            //get yelp data
                            JSONArray address = object.getJSONArray("address_components");
                            String city="";
                            String states="";
                            for(int i = 0; i<address.length();i++){
                                if(address.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_2")){
                                    city = address.getJSONObject(i).getString("short_name");
                                }else if(address.getJSONObject(i).getJSONArray("types").getString(0).equals("administrative_area_level_1")){
                                    states = address.getJSONObject(i).getString("short_name");
                                }
                            }
                            ArrayList<Review> reviews = new ArrayList<>();
                            if(object.has("reviews")){
                                JSONArray jsonArray = object.getJSONArray("reviews");
                                Log.i("reviewlength",String.valueOf(jsonArray.length()));
                                for(int i = 0; i<jsonArray.length(); i++){
                                    JSONObject info = jsonArray.getJSONObject(i);
                                    Review review = new Review(info.getString("profile_photo_url"),info.getString("author_name"),info.getString("rating"),info.getString("time"),info.getString("text"),info.getString("author_url"));
                                    reviews.add(review);
                                }
                            }else{
                                Log.i("reviews","none");
                            }
                            Log.i("review","getAll");
                            String addr = "";
                            String phone = "";
                            String rate = "";
                            String price = "";
                            String web = "";
                            if(object.has("formatted_address")){
                                addr = object.getString("formatted_address");
                            }
                            if(object.has("formatted_phone_number")){
                                phone = object.getString("formatted_phone_number");
                            }
                            if(object.has("rating")){
                                rate = object.getString("rating");
                            }
                            if(object.has("price_level")){
                                price = object.getString("price_level");
                            }
                            if(object.has("website")){
                                web = object.getString("website");
                            }

                            Details placeDetails = new Details(addr,phone,object.getString("name"),
                                    rate,price,object.getString("url"),web,object.getString("place_id"),reviews,city,states,resultList.getIcon_url(),resultList.getLat(),resultList.getLng());
                            Log.i("review","getAll2");
                            actionsOnUiThread(placeDetails); //send info on UI threads

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error","That didn't work!");
                Toast.makeText(context,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);

    }
    @Override
    public int getItemCount() {
        if(data!=null){
            return data.size();
        }else{
            return 0;
        }

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView placeIcon;
        public TextView placeName;
        public TextView placeLoc;
        public LinearLayout linearLayout;
        public ImageButton favButton;
        public ViewHolder(View itemView) {
            super(itemView);
            placeIcon = (ImageView) itemView.findViewById(R.id.main_list_item_img);
            placeName = (TextView) itemView.findViewById(R.id.main_list_item_name);
            placeLoc  = (TextView) itemView.findViewById(R.id.main_list_item_place);
            linearLayout = (LinearLayout)itemView.findViewById(R.id.linear_layout);
            favButton= (ImageButton)itemView.findViewById(R.id.main_list_item_fav);
        }
    }
}
