package com.example.new_hopes;

import java.util.ArrayList;

public class PlayList {
    PlayListNames pName;
    ArrayList<Song> song;

    public PlayList(PlayListNames pName, ArrayList<Song> song) {
        this.pName = pName;
        this.song = song;
    }
}
