package com.example.musicstreamingapplication.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreamingapplication.Model.GetSongs;
import com.example.musicstreamingapplication.Model.LikedDatabase;
import com.example.musicstreamingapplication.Model.Utility;
import com.example.musicstreamingapplication.R;

import java.util.ArrayList;
import java.util.List;

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

//        if(getSongs != null) {
//            if(selectedPosition == position) {
//                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.black));
//                holder.iv_play_active.setVisibility(View.VISIBLE);
//            }
//            else {
//                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
//                holder.iv_play_active.setVisibility(View.INVISIBLE);
//            }
//        }

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

        holder.iv_like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(getSongs.isIsliked()){
//                    holder.iv_like_button.setBackgroundResource(R.drawable.whiteheart);
//                    likedDatabase.deleteSongFromDatabaseByID(getSongs.getmKey());
//                    getSongs.setIsliked(false);
//                }
//                else {
//                    holder.iv_like_button.setBackgroundResource(R.drawable.greenheart);
//                    likedDatabase.addLikedSongToDatabase(getSongs.getmKey());
//                    getSongs.setIsliked(true);
//                }
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
        public ImageView iv_like_button;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            iv_title = itemView.findViewById(R.id.iv_title);
            iv_artist = itemView.findViewById(R.id.iv_artist);
            iv_duration = itemView.findViewById(R.id.tv_duration);
            iv_like_button = itemView.findViewById(R.id.iv_like_button);
        }

    }

}
