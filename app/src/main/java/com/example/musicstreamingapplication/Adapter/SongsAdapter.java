package com.example.musicstreamingapplication.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicstreamingapplication.Model.GetSongs;
import com.example.musicstreamingapplication.Model.LikedDatabase;
import com.example.musicstreamingapplication.Model.Utility;
import com.example.musicstreamingapplication.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    private int selectedPosition;
    Context context;
    List<GetSongs> arrayListSongs;
    LikedDatabase likedDatabase;
    ArrayList<String> allLikedSongs = new ArrayList<>();

    boolean check = true;

    public SongsAdapter(Context context,LikedDatabase likedDatabase, List<GetSongs> arrayListSongs, RecyclerItemClickListener listener) {
        this.context = context;
        this.arrayListSongs = arrayListSongs;
        this.listener = listener;
        this.likedDatabase = likedDatabase;
    }

    public interface RecyclerItemClickListener{
        void onClickListener(GetSongs songs, SongViewHolder songViewHolder ,int position );
        void onLikedClickListener(GetSongs songs, SongViewHolder songViewHolder ,int position,LikedDatabase likedDatabase);
    }
    private RecyclerItemClickListener listener;
    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(context).inflate(R.layout.songs_row,parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        GetSongs getSongs = arrayListSongs.get(position);
        ArrayList<String> likedSongs = new ArrayList<>();

        if(likedDatabase.returnAllLikedSongs().size()>0){
            likedSongs = likedDatabase.returnAllLikedSongs();
        }



        if(likedSongs.contains(getSongs.getmKey())){
            holder.iv_like_button.setBackgroundResource(R.drawable.greenheart);
        }
        else {
            holder.iv_like_button.setBackgroundResource(R.drawable.whiteheart);
        }


        holder.iv_title.setText(getSongs.getSongTitle());
        holder.iv_artist.setText(getSongs.getArtist());
        String duration = Utility.convertDuration(Long.parseLong(getSongs.getSongDuration()));
        holder.iv_duration.setText(duration);

        if(getSongs.getAlbum_art().trim() !=""){
            Log.e("hello", getSongs.getAlbum_art()+"h");
            //Picasso.get().load(getSongs.getAlbum_art().trim()).into(holder.album_art);

            RequestOptions requestOptions = new RequestOptions()
                    .transform(new RoundedCorners(20));

            Glide.with(context)
                    .load(getSongs.getAlbum_art().trim())
                    .apply(requestOptions)
                    .into(holder.album_art);
        }

        holder.iv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.onLikedClickListener(getSongs,holder, holder.getAdapterPosition(),likedDatabase);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickListener(getSongs,holder, holder.getAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayListSongs.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }


    public class SongViewHolder extends RecyclerView.ViewHolder {

        public TextView iv_title, iv_artist, iv_duration;
        public ImageView iv_like_button, album_art;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_title = itemView.findViewById(R.id.iv_title);
            iv_artist = itemView.findViewById(R.id.iv_artist);
            iv_duration = itemView.findViewById(R.id.tv_duration);
            iv_like_button = itemView.findViewById(R.id.iv_like_button);
            album_art = itemView.findViewById(R.id.iv_artwork);
        }

    }

}
