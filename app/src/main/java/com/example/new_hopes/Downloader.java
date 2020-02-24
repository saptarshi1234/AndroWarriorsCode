package com.example.new_hopes;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static android.content.ContentValues.TAG;

public class Downloader {
    public Context mContext;

    public Downloader(Context mContext) {
        this.mContext = mContext;
    }

    public void songDownLoad(){

    }
    public  void playListDownload(){

    }
    public void YoutubeUrl(String songName){
        MyTask task = new MyTask(this,songName);
        task.execute();

    }
    public void returnedUrl(String songName){
        Log.d(TAG, "returnedUrl: "+songName);
    }

}
