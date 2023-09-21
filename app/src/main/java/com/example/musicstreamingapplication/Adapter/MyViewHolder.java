package com.example.musicstreamingapplication.Adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreamingapplication.R;

public class MyViewHolder extends RecyclerView.ViewHolder {

    TextView tv_book_text;
    ImageView imd_book_thumnail;
    CardView cardView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);

        tv_book_text = itemView.findViewById(R.id.book_title_id);
        imd_book_thumnail = itemView.findViewById(R.id.book_img_id);
        cardView = itemView.findViewById(R.id.card_view_id);

    }
}
