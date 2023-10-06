package com.example.musicstreamingapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.jean.jcplayer.model.JcAudio;
import com.example.jean.jcplayer.view.JcPlayerView;
import com.example.musicstreamingapplication.Adapter.SongsAdapter;
import com.example.musicstreamingapplication.Model.GetSongs;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class SongsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ProgressBar progressBar;
    Boolean chekin = false;
    List<GetSongs> mUpload;
    SongsAdapter adapter;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    JcPlayerView jcPlayerView;
    ArrayList<JcAudio> jcAudios = new ArrayList<>();
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBarShowing);
        jcPlayerView = findViewById(R.id.jcPlayer);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUpload = new ArrayList<>();
        recyclerView.setAdapter(adapter);

        adapter = new SongsAdapter(getApplicationContext(),mUpload, new SongsAdapter.RecyclerItemClickListener(){
            @Override
            public void onClickListener(GetSongs songs, int position) {


                jcPlayerView.playAudio(jcAudios.get(position));
                jcPlayerView.setVisibility(View.VISIBLE);
                jcPlayerView.createNotification();

                changeSelectedSong(position);

            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference().child("songs");
        valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUpload.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GetSongs getSongs = snapshot.getValue(GetSongs.class);
                    getSongs.setmKey(snapshot.getKey());
                    getSongs.setArtist(StringUtils.substringBefore(getSongs.getArtist(), "(").trim());
                    currentIndex = 0;
                    String s = getIntent().getExtras().getString("artistName");

                    String[] arrofStr = getSongs.getArtist().split(",");
                    ArrayList<String> names = new ArrayList<>();
                    for(String a : arrofStr) {
                        if(s.equals(a.trim())) {
                            mUpload.add(getSongs);
                            chekin =true;
                            jcAudios.add(JcAudio.createFromURL(getSongs.getSongTitle(),getSongs.getSongLink()));
                            break;
                        }
                        names.add(a.trim());
                    }
                    getSongs.setNames(names);
                }

                if(chekin) {
                    jcPlayerView.initPlaylist(jcAudios,null);
                }else {
                    Toast.makeText(SongsActivity.this, "There is no Song...", Toast.LENGTH_SHORT).show();
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
}