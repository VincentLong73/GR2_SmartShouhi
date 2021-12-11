package com.dl.smartshouhi.adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dl.smartshouhi.R;

import java.io.IOException;
import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder1> {

    private Context context;
    private List<Uri> listUriPhoto;
    public PhotoAdapter(Context context) {
        this.context = context;
    }

    public void setData(List<Uri> list){
        this.listUriPhoto = list;
        notifyDataSetChanged();
    }

    public List<Uri> getListUriPhoto() {
        return listUriPhoto;
    }

    @NonNull
    @Override
    public PhotoViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo, parent, false);
        return new PhotoViewHolder1(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder1 holder, int position) {
        Uri uri = listUriPhoto.get(position);

        if(uri != null){
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
                if(bitmap != null){
                    holder.imgPhoto.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return;
    }

    @Override
    public int getItemCount() {
        if(listUriPhoto != null){
            return listUriPhoto.size();
        }
        return 0;
    }

    public static class PhotoViewHolder1 extends RecyclerView.ViewHolder{

        private final ImageView imgPhoto;
        public PhotoViewHolder1(@NonNull View itemView) {
            super(itemView);
            imgPhoto = itemView.findViewById(R.id.img_photo);
        }
    }
}

