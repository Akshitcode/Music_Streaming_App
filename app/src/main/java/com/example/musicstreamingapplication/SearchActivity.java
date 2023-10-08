package com.example.musicstreamingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
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

public class SearchActivity extends AppCompatActivity {

    LinearLayout artistTab, songTAb, searchTab, yourLibraryTab;

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Boolean chekin = false;
    List<GetSongs> mUpload;
    SongsAdapter adapter;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    JcPlayerView jcPlayerView;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    LikedDatabase likedDatabase;
    private int currentIndex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);




        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBarShowing);
        jcPlayerView = findViewById(R.id.jcPlayer);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUpload = new ArrayList<>();
        recyclerView.setAdapter(adapter);
        likedDatabase = new LikedDatabase(getApplicationContext());

        adapter = new SongsAdapter(getApplicationContext(),likedDatabase,mUpload, new SongsAdapter.RecyclerItemClickListener(){
            @Override
            public void onClickListener(GetSongs songs, SongsAdapter.SongViewHolder songViewHolder, int position) {


                jcPlayerView.playAudio(jcAudios.get(position));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();

                changeSelectedSong(position);

            }
            @Override
            public void onLikedClickListener(GetSongs getSongs, SongsAdapter.SongViewHolder holder, int position, LikedDatabase likedDatabase1) {
                if(likedDatabase1.returnAllLikedSongs().contains(getSongs.getmKey())){
                    holder.iv_like_button.setBackgroundResource(R.drawable.whiteheart);
                    getSongs.setIsliked(false);
                    likedDatabase1.deleteSongFromDatabaseByID(getSongs.getmKey());

                }
                else {
                    holder.iv_like_button.setBackgroundResource(R.drawable.greenheart);
                    likedDatabase.addLikedSongToDatabase(getSongs.getmKey());
                    getSongs.setIsliked(true);
                }

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUpload.clear();
                jcAudios.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GetSongs getSongs = snapshot.getValue(GetSongs.class);
                    getSongs.setmKey(snapshot.getKey());
                    getSongs.setSongTitle(StringUtils.substringBefore(getSongs.getSongTitle(),"(").trim());
                    getSongs.setArtist(StringUtils.substringBefore(getSongs.getArtist(), "(").trim());
                    mUpload.add(getSongs);
                    chekin =true;
                    jcAudios.add(JcAudio.createFromURL(getSongs.getSongTitle(),getSongs.getSongLink()));

                }

                if(chekin) {
                    jcPlayerView.initPlaylist(jcAudios,null);
                }else {
                    Toast.makeText(SearchActivity.this, "There is no Song...", Toast.LENGTH_SHORT).show();
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



        artistTab = findViewById(R.id.artist_tab);
        songTAb = findViewById(R.id.songs_tab);
        searchTab = findViewById(R.id.search_tab);
        yourLibraryTab = findViewById(R.id.your_library_tab);

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

        yourLibraryTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), YourLibraryActivity.class);
                startActivity(intent);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_btn,menu);

        MenuItem menuItem = menu.findItem(R.id.searchBtn);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Type here to search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                processSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                processSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void processSearch(String newText) {
        chekin = false;
        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUpload.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GetSongs getSongs = snapshot.getValue(GetSongs.class);
                    getSongs.setmKey(snapshot.getKey());
                    getSongs.setSongTitle(StringUtils.substringBefore(getSongs.getSongTitle(),"(").trim());
                    getSongs.setArtist(StringUtils.substringBefore(getSongs.getArtist(), "(").trim());
                    currentIndex = 0;
                    if(StringUtils.containsIgnoreCase(getSongs.getArtist(),newText) || StringUtils.containsIgnoreCase(getSongs.getSongTitle(),newText)) {
                        mUpload.add(getSongs);
                        chekin =true;
                        jcAudios.add(JcAudio.createFromURL(getSongs.getSongTitle(),getSongs.getSongLink()));
                    }

                }

                if(chekin) {
                    jcPlayerView.initPlaylist(jcAudios,null);
                }else {
                    Toast.makeText(SearchActivity.this, "There is no Song...", Toast.LENGTH_SHORT).show();
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
}