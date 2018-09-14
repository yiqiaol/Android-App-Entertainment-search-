package com.example.a15051.placesearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;



import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    private List<Review> reviews;
    private Context context;
    public ReviewsAdapter(List<Review> reviews,Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @Override
    public ReviewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //给每个item赋值
        final Review review = reviews.get(position);

        holder.reviewName.setText(review.getRevName());
        holder.reviewName.setTextColor(Color.parseColor("#01a183"));
        holder.reviewRating.setRating(Float.valueOf(review.getRevRate()));
        String date="";
        if(review.getRevTime().indexOf("-")==-1){
            Long timestamp = Long.parseLong(review.getRevTime())*1000;
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestamp));

        }else{
            date = review.getRevTime();
        }
        holder.reviewTime.setText(date);
        holder.reviewContent.setText(review.getRevContent());
        Picasso.get().load(review.getRevImg()).into(holder.reviewPhoto);
        holder.reviewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(review.getAuthorUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(reviews != null) {
            return reviews.size();
        }else{
            return 0;
        }
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView reviewPhoto;
        private TextView reviewName;
        private RatingBar reviewRating;
        private TextView reviewTime;
        private TextView reviewContent;
        private LinearLayout reviewLayout;
        private ViewHolder(View itemView) {
            super(itemView);
            reviewPhoto = (ImageView) itemView.findViewById(R.id.review_photo);
            reviewName = (TextView) itemView.findViewById(R.id.review_name);
            reviewRating = (RatingBar) itemView.findViewById(R.id.review_rating);
            reviewTime = (TextView) itemView.findViewById(R.id.review_time);
            reviewContent = (TextView) itemView.findViewById(R.id.review_content);
            reviewLayout = (LinearLayout)itemView.findViewById(R.id.review_layout);
        }
    }
}
