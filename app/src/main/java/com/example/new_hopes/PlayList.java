package com.example.new_hopes;

import android.graphics.Bitmap;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayList implements Serializable {
    PlayListNames pName;
    ArrayList<Song> songs;
    File imageLocation;

    public PlayList(PlayListNames pName, ArrayList<Song> songs) {
        this.pName = pName;
        this.songs = songs;
    }
}
