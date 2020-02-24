package com.example.new_hopes;

import android.media.MediaPlayer;

import java.io.IOException;

public class GlobalFunctions {

    static boolean isPlaying=false;
    static MediaPlayer mPlayer = new MediaPlayer();
    static String songName;


    static void stopPlaying()
    {
        isPlaying=false;
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    static String getSongPath(String name){
        return "";
    }

    static void startPlaying(String song_name){
        String path=getSongPath(song_name);
        songName=song_name;
        try {
            mPlayer.setDataSource(path);
            mPlayer.prepare();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();




        isPlaying=true;
    }
    static void pausePlaying(){
        mPlayer.pause();
    }

    static void resumePlaying(String song_name){
        try {
            mPlayer.start();
        }catch (Exception e){
            startPlaying(song_name);
        }
    }

}
