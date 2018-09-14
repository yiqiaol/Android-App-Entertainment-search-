package com.example.a15051.placesearch;



import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<SearchResult> Results;
    private List<SearchResult> currentResult;
    private TextView noResults;
    private Dialog progressDialog;
    private String next_token;
    private int page;
    private Button bt_previous;
    private Button bt_next;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);
        /******** show back icon*******************/
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //button_click function
        bt_previous = (Button) findViewById(R.id.previous);
        bt_next = (Button) findViewById(R.id.next);
        //no results textview
        noResults = (TextView) findViewById(R.id.no_results);
        /****************Get list result and show**********************/
        Intent intent = getIntent();
        currentResult = new ArrayList<>();
        page = 0;
        Results = new ArrayList<>(((ArrayList)intent.getSerializableExtra("data")));
        //initialize the page
        Log.i("count1",String.valueOf(Results.size()));
        if(Results.size()==0){
            noResults.setVisibility(View.VISIBLE);
        }else{
            noResults.setVisibility(View.GONE);
            updateData();
        }
//        updateData();
        //pre/next button on-click function
        bt_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page--;
                updateData();
                Log.i("This is",String.valueOf(page));
            }
        });
        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                updateData();
                Log.i("This is",String.valueOf(page));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentResult.size() == 0){
            noResults.setVisibility(View.VISIBLE);
        }else{
            noResults.setVisibility(View.GONE);
        }
        adapter = new ResultListViewAdapter(currentResult,SecondActivity.this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void actionsOnUiThread(final List<SearchResult> results){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                currentResult.clear();
                int release = Math.min(Results.size()-20*page,20);
                for(int i = page*20; i< page*20+release; i++){
                    currentResult.add(Results.get(i));
                }
                if(currentResult.get(0).getNextToken().equals("")){
                    bt_next.setEnabled(false);
                    bt_previous.setEnabled(true);
                }
                adapter = new ResultListViewAdapter(currentResult,SecondActivity.this);
                adapter.notifyDataSetChanged();
                recyclerView.setAdapter(adapter);
            }
        });
    }
    public void getNextPage(final String next_token){
        final ProgressDialog progressDialog = new ProgressDialog(SecondActivity.this);
        progressDialog.setMessage("Fetching next page");
        progressDialog.show();
        String url ="http://hw9-js.us-west-1.elasticbeanstalk.com/?pagetoken="+next_token;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try{
                            //update the data and next_token
                            JSONObject myJsonObject = new JSONObject(response);
                            String next_page = "";
                            if(myJsonObject.has("next_page_token")) {
                                next_page = myJsonObject.get("next_page_token").toString();
                            }
                            JSONArray jsonArray = myJsonObject.getJSONArray("results");
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject each = jsonArray.getJSONObject(i);
                                double Lat = each.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                double Lng = each.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                SearchResult searchResult = new SearchResult(each.get("icon").toString(),each.get("place_id").toString(),each.get("name").toString(),each.get("vicinity").toString(),next_page,Lat,Lng);
                                Results.add(searchResult);
                            }
                            actionsOnUiThread(Results);
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error","That didn't work!");
                progressDialog.dismiss();
                Toast.makeText(SecondActivity.this,error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    /**************update the show date********************************/
    //case1: if there has exist the data, we just take the results
    //case2: if no and has next token, we send http request
    //case: if no and  has no token.we do nothing
    private void updateData(){
        if(Results.size() > page*20){
            int release = Math.min(Results.size()-20*page,20);
            currentResult = new ArrayList<>();
            for(int i = page*20; i< page*20+release; i++){
                currentResult.add(Results.get(i));
            }
            //check the pre/next button
            if(page == 0){
                bt_previous.setEnabled(false);
            }else{
                bt_previous.setEnabled(true);
            }
            if((currentResult.get(0).getNextToken().length()!=0)){
                bt_next.setEnabled(true);
            }else{
                bt_next.setEnabled(false);
            }
            adapter = new ResultListViewAdapter(currentResult,this);
            adapter.notifyDataSetChanged();
            recyclerView.setAdapter(adapter);
        }else {
            //we need to send http request

            if (currentResult.get(0).getNextToken().length()!=0) {
                next_token = Results.get(Results.size() - 1).getNextToken();
                getNextPage(next_token);
                bt_next.setEnabled(true);
                bt_previous.setEnabled(true);
            }
        }

    }
}
