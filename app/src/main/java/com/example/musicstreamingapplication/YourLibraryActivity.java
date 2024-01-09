package com.example.musicstreamingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.example.musicstreamingapplication.Adapter.SongsAdapter;
import com.example.musicstreamingapplication.Model.GetSongs;
import com.example.musicstreamingapplication.Model.LikedDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class YourLibraryActivity extends AppCompatActivity {

    LinearLayout artistTab, songTAb, searchTab, yourLibraryTab, likedSongs, downloadedSongs,uploadSongs;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    JcPlayerView jcPlayerView;
    List<GetSongs> mUpload;
    SongsAdapter adapter;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private int currentIndex;
    LikedDatabase likedDatabase;
    Boolean chekin = false, backPressed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_library);



        artistTab = findViewById(R.id.artist_tab);
        songTAb = findViewById(R.id.songs_tab);
        searchTab = findViewById(R.id.search_tab);
        yourLibraryTab = findViewById(R.id.your_library_tab);
        likedSongs = findViewById(R.id.liked_songs);
        downloadedSongs = findViewById(R.id.downloaded_songs);
        uploadSongs = findViewById(R.id.upload_songs);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBarShowing);
        jcPlayerView = findViewById(R.id.jcPlayer);
        likedDatabase = new LikedDatabase(getApplicationContext());

        artistTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ArtistAlbumActivity.class);
                startActivity(intent);
            }
        });
        songTAb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SongsActivity.class);
                intent.putExtra("artistName", "all");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        searchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

        likedSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                backPressed=true;
                likedSongs.setVisibility(View.GONE);
                downloadedSongs.setVisibility(View.GONE);
                uploadSongs.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                getSupportActionBar().setTitle("Liked Songs");

                showLikedSongs();

            }
        });
        downloadedSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        uploadSongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), UploadSongActivity.class);
                startActivity(intent);
                finish();
            }
        });
        yourLibraryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likedSongs.setVisibility(View.VISIBLE);
                downloadedSongs.setVisibility(View.VISIBLE);
                uploadSongs.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                backPressed=false;
                getSupportActionBar().setTitle("Your Library");
            }
        });
    }

    private void showLikedSongs() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUpload = new ArrayList<>();
        recyclerView.setAdapter(adapter);


        adapter = new SongsAdapter(getApplicationContext(),likedDatabase,mUpload, new SongsAdapter.RecyclerItemClickListener(){
            @Override
            public void onClickListener(GetSongs songs, SongsAdapter.SongViewHolder songViewHolder, int position) {


                jcPlayerView.playAudio(jcAudios.get(position));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();

                changeSelectedSong(position);

            }
            @Override
            public void onLikedClickListener(GetSongs getSongs, SongsAdapter.SongViewHolder holder, int position, LikedDatabase likedDatabase) {
                if(getSongs.isIsliked()){
                    holder.iv_like_button.setBackgroundResource(R.drawable.whiteheart);
                    likedDatabase.deleteSongFromDatabaseByID(getSongs.getmKey());
                    getSongs.setIsliked(false);
                }
                else {
                    holder.iv_like_button.setBackgroundResource(R.drawable.greenheart);
                    likedDatabase.addLikedSongToDatabase(getSongs.getmKey());
                    getSongs.setIsliked(true);
                }
            }
        });
        chekin = false;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUpload.clear();
                jcAudios.clear();
                ArrayList<String> allLikedSongs = new ArrayList<>();

                allLikedSongs = likedDatabase.returnAllLikedSongs();


                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GetSongs getSongs = snapshot.getValue(GetSongs.class);
                    getSongs.setmKey(snapshot.getKey());
                    getSongs.setSongTitle(StringUtils.substringBefore(getSongs.getSongTitle(),"(").trim());
                    getSongs.setArtist(StringUtils.substringBefore(getSongs.getArtist(), "(").trim());
                    currentIndex = 0;
                    if(allLikedSongs.contains(getSongs.getmKey())){
                        mUpload.add(getSongs);
                        chekin =true;
                        jcAudios.add(JcAudio.createFromURL(getSongs.getSongTitle(),getSongs.getSongLink()));
                    }

                }

                if(chekin) {
                    jcPlayerView.initPlaylist(jcAudios,null);
                }else {
                    Toast.makeText(getApplicationContext(), "There is no Song...", Toast.LENGTH_SHORT).show();
                }

                adapter.setSelectedPosition(0);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void changeSelectedSong(int index){
        adapter.notifyItemChanged(adapter.getSelectedPosition());
        currentIndex = index;
        adapter.setSelectedPosition(currentIndex);
        adapter.notifyItemChanged(currentIndex);
    }

    @Override
    public void onBackPressed() {
        if(backPressed){
            likedSongs.setVisibility(View.VISIBLE);
            downloadedSongs.setVisibility(View.VISIBLE);
            uploadSongs.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            backPressed=false;
            getSupportActionBar().setTitle("Your Library");
        }
        else{
            super.onBackPressed();
            finish();
        }
    }
}