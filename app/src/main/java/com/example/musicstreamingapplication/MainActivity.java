package com.example.musicstreamingapplication;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chibde.visualizer.BarVisualizer;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.jgabrielfreitas.core.BlurImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private List<Song> allSongs = new ArrayList<>();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private static final String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
    private ExoPlayer player;

    private ActivityResultLauncher<String> recordAudioPermissionLauncher; // to access in the song adapter
    private static final String recordAudioPermission = Manifest.permission.RECORD_AUDIO;
    private ConstraintLayout playerView;
    private TextView playerCloseBtn;
    //controls
    private TextView songNameView, skipPreviousBtn, skipNextBtn, playPauseBtn, repeatModeBtn, playListBtn;
    private TextView homeSongNameView, homeSkipPreviousBtn, homePlayPauseBtn, homeSkipNextBtn;
    //wrappers
    ConstraintLayout homeControlWrapper, headWrapper, artWorkWrapper, seekBarWrapper, controlWrapper, audioVisualizerWrapper;
    //artWork
    private CircleImageView artWorkView;
    //seek bar
    private SeekBar seekBar;
    private TextView progressView, durationView;
    //audio visualizer
    private BarVisualizer audioVisualizer;
    //blur image view
    private BlurImageView blurImageView;
    //status bar and navigation color
    private int defaultStatusColor;
    //repeat mode
    int repeatMode = 1; // repeat all =1,repeat one =2, shuffle all =3
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //save the status color
        defaultStatusColor = getWindow().getStatusBarColor();
        //set the navigation bar color
        getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(defaultStatusColor,199)); //0 to 255


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        recyclerView = findViewById(R.id.recyclerView);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{WRITE_EXTERNAL_STORAGE}, 100);
            }
        }

        if (checkPermission()) {
            fetchSongs();
        } else {
            requestPermission(); // Request Permission
        }

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {

                    } else {
                        Toast.makeText(MainActivity.this, "You Denied the permission", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(MainActivity.this, "You Denied the permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //record audio permission

        recordAudioPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted-> {
            if(granted && player.isPlaying()) {
                activateAudiovisualizer();
            }else {
                userResponsesOnRecordPermission();
            }
        });
        recordAudioPermissionLauncher.launch(permission);
        //views
        player = new ExoPlayer.Builder(this).build();

        playerView = findViewById(R.id.playerView);
        playerCloseBtn = findViewById(R.id.playerCloseBtn);
        songNameView = findViewById(R.id.songNameView);
        skipPreviousBtn = findViewById(R.id.skipPreviousBtn);
        skipNextBtn = findViewById(R.id.skipNextBtn);
        playPauseBtn = findViewById(R.id.playPauseBtn);
        repeatModeBtn = findViewById(R.id.repeatModeBtn);
        playListBtn =findViewById(R.id.playListBtn);

        homeSongNameView = findViewById(R.id.homeSongNameView);
        homeSkipPreviousBtn = findViewById(R.id.homeSkipPreviousButton);
        homeSkipNextBtn = findViewById(R.id.homeSkipNextButton);
        homePlayPauseBtn = findViewById(R.id.homePlayerPauseButton);

        //wrappers
        homeControlWrapper = findViewById(R.id.homeControlWrapper);
        headWrapper = findViewById(R.id.headWrapper);
        artWorkWrapper = findViewById(R.id.artworkWrapper);
        seekBarWrapper = findViewById(R.id.seekBarWrapper);
        controlWrapper = findViewById(R.id.controlWrapper);
        audioVisualizerWrapper = findViewById(R.id.audioVisualizerWrapper);

        //artwork
        artWorkView = findViewById(R.id.artworkView);
        //seek bar
        seekBar = findViewById(R.id.seekBar);
        progressView = findViewById(R.id.progressView);
        durationView = findViewById(R.id.durationView);
        //audio visualizer
        audioVisualizer = findViewById(R.id.visualizer);
        //blur image view
        blurImageView = findViewById(R.id.blurImageView);

        //player controls method
        playercontrols();
    }

    private void playercontrols() {
        //song name marquee
        songNameView.setSelected(true);
        homeSongNameView.setSelected(true);

        //exit the player view
        playerCloseBtn.setOnClickListener(v -> {
            exitPlayerView();
        });
        playListBtn.setOnClickListener(v-> exitPlayerView());
        //open player view on home control wrapper click
        homeControlWrapper.setOnClickListener(v-> showPlayerView());

        //player listener
        player.addListener(new Player.Listener() {
            @Override
            public void onMediaItemTransition(@Nullable MediaItem mediaItem, int reason) {
                Player.Listener.super.onMediaItemTransition(mediaItem, reason);
                //show the playing song title
                assert mediaItem != null;
                songNameView.setText(mediaItem.mediaMetadata.title);
                homeSongNameView.setText(mediaItem.mediaMetadata.title);

                progressView.setText(getReadableTime((int) player.getCurrentPosition()));
                seekBar.setProgress((int)player.getCurrentPosition());
                seekBar.setMax((int) player.getDuration());
                durationView.setText(getReadableTime((int) player.getDuration()));
                playPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_outline_24, 0 , 0, 0);
                homePlayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_24,0, 0, 0);

                //show the current art work
                showCurrentArtWork();
                //update the progress position of a current playing song
                updatePlayerPositionProgress();
                //load the animation artWork
                artWorkView.setAnimation(loadRotation());

                //set audio visualizer
                activateAudiovisualizer();
                //update playerViewColors
                updatePlayerColors();

                if(!player.isPlaying()) {
                    player.play();
                }
            }
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if(playbackState == ExoPlayer.STATE_READY) {
                    //set values to player views
                    songNameView.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);
                    homeSongNameView.setText(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.title);
                    progressView.setText(getReadableTime((int) player.getCurrentPosition()));
                    durationView.setText(getReadableTime((int) player.getDuration()));
                    seekBar.setMax((int) player.getDuration());
                    seekBar.setProgress((int) player.getCurrentPosition());

                    playPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_outline_24, 0 , 0, 0);
                    homePlayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause_24,0, 0, 0);

                    //show the current art work
                    showCurrentArtWork();
                    //update the progress position of a current playing song
                    updatePlayerPositionProgress();
                    //load the animation artWork
                    artWorkView.setAnimation(loadRotation());

                    //set audio visualizer
                    activateAudiovisualizer();
                    //update playerViewColors
                    updatePlayerColors();
                }else {
                    playPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_circle, 0 , 0, 0);
                    homePlayPauseBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_play_arrow_24,0, 0, 0);

                }
            }
        });
    }

    private Animation loadRotation() {
        RotateAnimation rotateAnimation = new RotateAnimation(0,360, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setDuration(10000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        return rotateAnimation;
    }

    private void updatePlayerPositionProgress() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(player.isPlaying()) {
                    progressView.setText(getReadableTime((int) player.getCurrentPosition()));
                    seekBar.setProgress((int) player.getCurrentPosition());
                }

                //repeat calling the method
                updatePlayerPositionProgress();
            }
        },1000);

    }

    private void showCurrentArtWork() {
        artWorkView.setImageURI(Objects.requireNonNull(player.getCurrentMediaItem()).mediaMetadata.artworkUri);

        if(artWorkView.getDrawable() == null) {
            artWorkView.setImageResource(R.drawable.default_network);
        }
    }

    private String getReadableTime(int duration) {
        String time;
        int hrs = duration/(1000*60*60);
        int min = (duration%(1000*60*60))/(1000*60);
        int sec = (((duration%(1000*60*60))%(1000*60*60))%(1000*60))/1000;

        if(hrs<1){
            time = min+":"+sec;
        }else {
            time = hrs+":"+min+":"+sec;
        }
        return time;
    }

    private void showPlayerView() {
        playerView.setVisibility(View.VISIBLE);
        updatePlayerColors();
    }

    private void updatePlayerColors() {
    }

    private void exitPlayerView() {
        playerView.setVisibility(View.GONE);
        getWindow().setStatusBarColor(defaultStatusColor);
        getWindow().setNavigationBarColor(ColorUtils.setAlphaComponent(defaultStatusColor, 199)); // 0 to 255
    }

    private void userResponsesOnRecordPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if(shouldShowRequestPermissionRationale(recordAudioPermission)) {
                //show an educational ui explaining why we need this permission
                //use alert dialog
                new AlertDialog.Builder(this)
                        .setTitle("Requesting to show Audio Visualizer")
                        .setMessage("Allow this app to display audio visualizer when music is playing00")
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                recordAudioPermissionLauncher.launch(recordAudioPermission);
                            }
                        })
                        .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Permission denied for audio visualizer!!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Permission denied for audio visualizer!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //audio visualizer
    private void activateAudiovisualizer() {


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //release the player
        if(player.isPlaying()) {
            player.stop();
        }else {
            player.release();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int readCheck = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int writeCheck = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            return readCheck == PackageManager.PERMISSION_GRANTED && writeCheck == PackageManager.PERMISSION_GRANTED;
        }
    }

    private String[] permissions = {READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission")
                    .setMessage("Please give the Storage permission")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                intent.addCategory("android.intent.category.DEFAULT");
                                intent.setData(Uri.parse(String.format("package:%s", new Object[]{getApplicationContext().getPackageName()})));
                                activityResultLauncher.launch(intent);
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                                activityResultLauncher.launch(intent);
                            }
                        }
                    })
                    .setCancelable(false)
                    .show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this, permissions, 30);
        }
    }


    private void fetchSongs() {

        List<Song> songs = new ArrayList<>();
        Uri mediaStoreUri;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mediaStoreUri = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        }else  {
            mediaStoreUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        }

        //projection
        String[] projection = new String[]{
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE,
                MediaStore.Audio.Media.ALBUM_ID
        };

        //order
        String sortOrder = MediaStore.Audio.Media.DATE_ADDED;

        //get the songs
        try (Cursor cursor = getContentResolver().query(mediaStoreUri,projection,null ,null,sortOrder)){
            //cache cursor indices
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE);
            int albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID);

            //clear the prvious loaded before adding loading again
            while (cursor.moveToNext()) {
                //get int values of a colum for a given audio file
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                long albumId = cursor.getLong(albumColumn);

                //song uri
                Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);

                //album artwork uri
                Uri albumArtworkUri = ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),albumId);

                //remove .mp3 extension from the songs name
                name = name.substring(0,name.lastIndexOf("."));

                //song item
                Song song = new Song(name, uri, albumArtworkUri, size, duration);

                songs.add(song);
            }

            //display songs
            showSongs(songs);
        }

    }

    private void showSongs(List<Song> songs) {

        if (songs.size() == 0) {
            Toast.makeText(this, "No songs to display", Toast.LENGTH_SHORT).show();
            return;
        }

        //save songs
        allSongs.clear();
        allSongs.addAll(songs);

        //update the tool bar title
        String title = "Musify"+ " - " + songs.size();
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);

        //layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //songs adapter
        songAdapter = new SongAdapter(this, songs, player, playerView);
        //set the adapter to recyclerview
       // recyclerView.setAdapter(songAdapter);

        //recycler animators (optional)

        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(songAdapter);
        scaleInAnimationAdapter.setDuration(500);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator());

        scaleInAnimationAdapter.setFirstOnly(false);
        recyclerView.setAdapter(scaleInAnimationAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(checkPermission() == true) {
            fetchSongs();
        }
    }


    //setting the menu search button

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.search_btn,menu);

        //search button item
        MenuItem menuItem = menu.findItem(R.id.searchBtn);
        SearchView searchView = (SearchView) menuItem.getActionView();

        //search song Method
        searchSong(searchView);
        return super.onCreatePanelMenu(featureId, menu);
    }

    private void searchSong(SearchView searchView) {

        //search view listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                //filter songs
                filterSongs(newText.toLowerCase());
                return true;
            }
        });
    }

    private void filterSongs(String toLowerCase) {
        List<Song> filteredList = new ArrayList<>();

        if(allSongs.size() >0) {
            for (Song song: allSongs) {
                if(song.getTitle().toLowerCase().contains(toLowerCase)) {
                    filteredList.add(song);
                }
            }
            if (songAdapter != null) {
                songAdapter.filterSongs(filteredList);
            }
        }
    }
}