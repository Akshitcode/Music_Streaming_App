package com.example.musicstreamingapplication.Model;

import java.util.ArrayList;

public class GetSongs {
    String songCategory, songTitle, artist, album_art, songDuration, songLink, mKey;
    ArrayList<String> names = new ArrayList<>();
    public GetSongs(String album_art, String songLink, String songTitle, String artist, String songDuration, String songCategory) {
        if(songTitle.trim().equals("")){
            songTitle = "No Title";
        }

        this.songTitle = songTitle;
        this.artist = artist;
        this.album_art = album_art;
        this.songDuration = songDuration;
        this.songLink = songLink;
        this.songCategory = songCategory;
    }

    public GetSongs() {
    }

    public String getSongsCategory() {
        return songCategory;
    }

    public void setSongsCategory(String songsCategory) {
        this.songCategory = songsCategory;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum_art() {
        return album_art;
    }

    public void setAlbum_art(String album_art) {
        this.album_art = album_art;
    }

    public String getSongDuration() {
        return songDuration;
    }

    public void setSongDuration(String songDuration) {
        this.songDuration = songDuration;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public String getmKey() {
        return mKey;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }
}
