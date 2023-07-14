package com.example.musicstreamingapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;

import java.util.ArrayList;
import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    List<Song> songs;
    ExoPlayer player;
    ConstraintLayout playerView;

    public SongAdapter(Context context, List<Song> songs,ExoPlayer player,ConstraintLayout playerView) {
        this.context = context;
        this.songs = songs;
        this.player = player;
        this.playerView = playerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.song_row_item, parent, false);

        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Song song = songs.get(position);
        SongViewHolder viewHolder = (SongViewHolder) holder;

        viewHolder.titleHolder.setText(song.getTitle());
        viewHolder.duration.setText(String.valueOf(song.getDuration()));
        viewHolder.sizeHolder.setText(String.valueOf(song.getSize()));
        Uri artWorkUri  = song.getArtWorkUri();

        if (artWorkUri != null) {
            viewHolder.artWorkHolder.setImageURI(artWorkUri);

            if (viewHolder.artWorkHolder.getDrawable() == null) {
                viewHolder.artWorkHolder.setImageResource(R.drawable.default_network);
            }
        }

        //play song on item clcik
        viewHolder.itemView.setOnClickListener(v -> {
            if(!player.isPlaying()) {
                player.setMediaItems(getMediaItems(), position,0);
            }else {
                player.pause();
                player.seekTo(position,0);
            }

            //prepare and play
            player.prepare();
            player.play();

            playerView.setVisibility(View.VISIBLE);

        });
    }

    private List<MediaItem> getMediaItems() {
        //define a list of media items
        List<MediaItem> mediaItems = new ArrayList<>();
        for(Song song : songs) {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(song.getUri())
                    .setMediaMetadata(getMetaData(song))
                    .build();

            //add the media item to mediaitems list
            mediaItems.add(mediaItem);
        }
        return mediaItems;
    }

    private MediaMetadata getMetaData(Song song) {
        return new MediaMetadata.Builder()
                .setTitle(song.getTitle())
                .setArtworkUri(song.getArtWorkUri())
                .build();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {

        ImageView artWorkHolder;
        TextView titleHolder;
        TextView duration, sizeHolder;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);

            artWorkHolder = itemView.findViewById(R.id.artworkView);
            titleHolder = itemView.findViewById(R.id.titleView);
            duration = itemView.findViewById(R.id.durationView);
            sizeHolder = itemView.findViewById(R.id.sizeView);
        }
    }
    @Override
    public int getItemCount() {
        return songs.size();
    }


    //filter songs/ search results
    public void filterSongs(List<Song> filteredList) {
        songs = filteredList;
        notifyDataSetChanged();
    }
}
