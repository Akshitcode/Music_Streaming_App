package com.example.musicstreamingapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicstreamingapplication.Model.Upload;
import com.example.musicstreamingapplication.R;
import com.example.musicstreamingapplication.SongsActivity;


import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<MyViewHolder>{

    private Context mContext ;
    private List <Upload> uploads;

    public RecyclerViewAdapter(Context mContext, List<Upload> uploads) {
        this.mContext = mContext;
        this.uploads = uploads;
    }



    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.card_view_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Upload upload = uploads.get(position);
        holder.tv_book_text.setText(upload.getName());
        Glide.with(mContext).load(upload.getUrl()).into(holder.imd_book_thumnail);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SongsActivity.class);
                intent.putExtra("songsCategory", upload.getSongsCategory());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

}
