package com.example.a15051.placesearch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FavoriteFragment extends Fragment{
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    List<SearchResult> favResults;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_fragment,null);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public void onStart() {
        favResults = getFavObj();
        /********************check if no favs*****************************************/
        TextView no_favorite = getActivity().findViewById(R.id.no_favorite);
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("fav1", Context.MODE_PRIVATE);
        Map<String,?> map = mSharedPreferences.getAll();
        if(map.entrySet() == null || map.entrySet().size() == 0){
            no_favorite.setVisibility(View.VISIBLE);
        }else{
            no_favorite.setVisibility(View.GONE);
        }
        /***********update the adapter with the fav_list********************************/
        adapter = new FavoriteAdapter(favResults,getContext(),recyclerView,no_favorite);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
        super.onStart();
    }

    private List<SearchResult> getFavObj(){
        SharedPreferences mSharedPreferences = getActivity().getSharedPreferences("fav1", Context.MODE_PRIVATE);
        Map<String,?> map = mSharedPreferences.getAll();
        SearchResult favResult;
        List<SearchResult> favs = new ArrayList<>();
        for(Map.Entry<String,?> entry: map.entrySet()){
            String info = ""+ entry.getValue();
            Log.i("msg",info);
            String[] array = info.split("\\{");
            Log.i("favs",array.length+"");
            favResult = new SearchResult(array[0],array[1],array[2],array[3],array[4],Double.valueOf(array[5]),Double.valueOf(array[6]));
            favs.add(favResult);
        }
        return new ArrayList<SearchResult>(favs);
    }
}
