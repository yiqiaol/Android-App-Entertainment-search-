package com.example.a15051.placesearch;

import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {
    private List<Bitmap> photos;

    public PhotosAdapter(List<Bitmap> photos) {
        this.photos = photos;
    }

    @Override
    public PhotosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_item, parent,false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(PhotosAdapter.ViewHolder holder, int position) {
        final Bitmap photo_url = photos.get(position);
        holder.photo.setImageBitmap(photo_url);
    }

    @Override
    public int getItemCount() {
        if(photos != null){
            return photos.size();
        }else{
            return 0;
        }

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView photo;
        public ViewHolder(View itemView) {
            super(itemView);
            photo = (ImageView) itemView.findViewById(R.id.img_item);
        }

    }
}
