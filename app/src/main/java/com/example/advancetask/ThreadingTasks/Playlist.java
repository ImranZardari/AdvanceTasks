package com.example.advancetask.ThreadingTasks;

import android.os.Parcel;
import android.os.Parcelable;

public class Playlist implements Parcelable {

    public static final Parcelable.Creator<Playlist> CREATOR = new Parcelable.Creator<Playlist>() {
        @Override
        public Playlist createFromParcel(Parcel source) {
            return new Playlist(source);
        }

        @Override
        public Playlist[] newArray(int size) {
            return new Playlist[size];
        }
    };
    private String songName;
    private String songUrl;

    public Playlist(String songName, String songUrl) {
        this.songName = songName;
        this.songUrl = songUrl;
    }

    protected Playlist(Parcel in) {
        this.songName = in.readString();
        this.songUrl = in.readString();
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.songName);
        dest.writeString(this.songUrl);
    }

    public void readFromParcel(Parcel source) {
        this.songName = source.readString();
        this.songUrl = source.readString();
    }
}
