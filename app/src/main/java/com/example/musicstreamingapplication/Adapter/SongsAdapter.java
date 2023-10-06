package com.example.musicstreamingapplication.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicstreamingapplication.Model.GetSongs;
import com.example.musicstreamingapplication.Model.Utility;
import com.example.musicstreamingapplication.R;

import java.util.List;

public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.SongViewHolder> {

    private int selectedPosition;
    Context context;
    List<GetSongs> arrayListSongs;

    public SongsAdapter(Context context, List<GetSongs> arrayListSongs, RecyclerItemClickListener listener) {
        this.context = context;
        this.arrayListSongs = arrayListSongs;
        this.listener = listener;
    }

    public interface RecyclerItemClickListener{
        void onClickListener(GetSongs songs ,int position);
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

        if(getSongs != null) {
            if(selectedPosition == position) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.black));
                holder.iv_play_active.setVisibility(View.VISIBLE);
            }
            else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                holder.iv_play_active.setVisibility(View.INVISIBLE);
            }
        }
        holder.tv_title.setText(getSongs.getSongTitle());
        holder.tv_artist.setText(getSongs.getArtist());
        String duration = Utility.convertDuration(Long.parseLong(getSongs.getSongDuration()));
        holder.tv_duration.setText(duration);

        holder.bind(getSongs,listener,holder);

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

        public TextView tv_title, tv_artist, tv_duration;
        ImageView iv_play_active;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_artist = itemView.findViewById(R.id.tv_artist);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            iv_play_active = itemView.findViewById(R.id.iv_play_active);
        }

        public void bind(GetSongs getSongs, final RecyclerItemClickListener listener,SongViewHolder songViewHolder) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    songViewHolder.iv_play_active.setImageResource(R.drawable.ic_pause_outline_24);
                    listener.onClickListener(getSongs,getAdapterPosition());
                }
            });
        }
    }

}
