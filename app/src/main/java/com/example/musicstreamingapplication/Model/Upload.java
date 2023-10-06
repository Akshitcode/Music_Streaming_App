package com.example.musicstreamingapplication.Model;

public class Upload {

    public String name;
    public String url;
    public String songCategory;

    public Upload(String name, String url, String songCategory) {
        this.name = name;
        this.url = url;
        this.songCategory = songCategory;
    }

    public Upload() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSongCategory() {
        return songCategory;
    }

    public void setSongCategory(String songsCategory) {
        this.songCategory = songsCategory;
    }
}
