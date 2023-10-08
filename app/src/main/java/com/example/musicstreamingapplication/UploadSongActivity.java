package com.example.musicstreamingapplication;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;


public class UploadSongActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView textViewImage;
    ProgressBar progressBar;
    Uri audioUri;
    StorageReference mStorageRef;
    Bitmap bitmap;
    StorageTask mUlpoadTask;
    DatabaseReference referenceSongs;
    String songCategory;
    MediaMetadataRetriever metadataRetriever;
    byte [] art;
    String title1,artist1,album_art1 = "" , durations1;
    TextView title, artist, album, durations, dataa;
    ImageView album_art;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_song);

        textViewImage=findViewById(R.id.textViewsongsFilesSelected);
        progressBar =findViewById(R.id.progressBar);
        title = findViewById(R.id.title);
        artist = findViewById(R.id.artist);
        durations = findViewById(R.id.duration);
        album = findViewById(R.id.album);
        dataa = findViewById(R.id.dataa);
        album_art = findViewById(R.id.imageview);

        metadataRetriever = new MediaMetadataRetriever();
        referenceSongs = FirebaseDatabase.getInstance().getReference().child("songs");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("songs");

        Spinner spinner = findViewById(R.id.spinner);

        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("Love Songs");
        categories.add("Sad Songs");
        categories.add("Party Songs");
        categories.add("Birthday Songs");
        categories.add("God Songs");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        songCategory = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        songCategory = "";
    }

    public void openAudioFiles(View v) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.setType("audio/*");
        startActivityForResult(Intent.createChooser(intent,"Select Audio"),101);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==101  && resultCode == RESULT_OK && data.getData() !=null) {
            audioUri = data.getData();
            String filename = getFileName(audioUri);
            textViewImage.setText(filename);
            metadataRetriever.setDataSource(this,audioUri);

            art = metadataRetriever.getEmbeddedPicture();
            bitmap = BitmapFactory.decodeByteArray(art,0, art.length);
            album_art.setImageBitmap(bitmap);
            album.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM));
            artist.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST));
            dataa.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE));
            durations.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            title.setText(metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE));

            artist1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            title1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            durations1 = metadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);


        }
    }

    @SuppressLint("Range")
    private  String getFileName(Uri uri) {
        String result = null;
        if(uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null,null,null,null);

            try{
                if(cursor!=null && cursor.moveToFirst()){
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
            finally {
                cursor.close();
            }
        }
        if(result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut!=-1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void uploadFileToFirebase(View v) {
        if(textViewImage.equals("No File Selected")) {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
        } else {
            if(mUlpoadTask != null && mUlpoadTask.isInProgress()) {
                Toast.makeText(this, "Songs uploads in allready in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadFiles();
            }
        }
    }

    private void uploadFiles() {

        if(audioUri !=null) {
            Toast.makeText(this, "Uploading please Wait!", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            final StorageReference storageReference = mStorageRef.child(System.currentTimeMillis() +"."+ getFileExtension(audioUri));
            mUlpoadTask = storageReference.putFile(audioUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {


                        com.example.musicstreamingapplication.Model.UploadSong uploadSong = new com.example.musicstreamingapplication.Model.UploadSong(songCategory,title1,artist1,audioUri.toString(),durations1,uri.toString());
                        String uploadId = referenceSongs.push().getKey();
                        referenceSongs.child(uploadId).setValue(uploadSong);

                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                    double progress = (100.0* snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);

                }
            });
        }else {
            Toast.makeText(this, "No file Selected to Upload!", Toast.LENGTH_SHORT).show();
        }

    }
    private String getFileExtension(Uri audioUri) {

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(audioUri));
    }

    public void openAlbumUploadsActivity(View v) {
        Intent intent = new Intent(UploadSongActivity.this, UploadAlbumActivity.class);
        startActivity(intent);
    }

}