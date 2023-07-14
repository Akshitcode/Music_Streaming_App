package com.example.musicstreamingapplication;

import android.net.Uri;

public class Song {

    String title;
    Uri uri;
    Uri artWorkUri;
    int size;
    int duration;

    public Song(String title, Uri uri, Uri artWorkUri, int size, int duration) {
        this.title = title;
        this.uri = uri;
        this.artWorkUri = artWorkUri;
        this.size = size;
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public Uri getUri() {
        return uri;
    }

    public Uri getArtWorkUri() {
        return artWorkUri;
    }

    public int getSize() {
        return size;
    }

    public int getDuration() {
        return duration;
    }
}
