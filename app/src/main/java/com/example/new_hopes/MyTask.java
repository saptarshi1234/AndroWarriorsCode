package com.example.new_hopes;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

class MyTask extends AsyncTask<String, Boolean, Boolean> {
    String songName,link;
    Downloader d;
    public final String TAG ="hell";


    public MyTask(Downloader d,String songName) {
        super();
        this.d = d;
        this.songName = songName;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        String url = "https://www.youtube.com/results?search_query="+songName.replace(" ","+");
        Log.d("hell",url);
        final Document[] doc = {null};
        java.net.URL vidUrl;
        String html="";
        final String[] html2 = {""};
        final String finalUrl = url;

                try {
                    html2[0] = Jsoup.connect(finalUrl).get().html();
                    doc[0] = Jsoup.connect(finalUrl)
                            .header("Accept-Encoding", "gzip, deflate")
                            .get();


                } catch (IOException e) {
                    Log.d("hell", e.getMessage().toString());
                }
        Log.d("hell", "read9 " + doc[0].body().html());
        Elements links = doc[0].select("a[id=video-title]");
        //Log.d("hell", "doInBackground: "+link);

        Log.d(TAG, "doInBackground: "+doc[0].html().length());
        String totalHTML=doc[0].html();
        int counter=0;
        String ans="";
        songName=songName.toLowerCase();

        while (true) {
            int i=totalHTML.indexOf("\"url\":\"/watch?");
            if(i==-1)
                break;
            String imp;
            try{
            imp=totalHTML.substring(i-3000,i);}catch (Exception e){
                imp=totalHTML.substring(i-500,i);
            }
            if(imp.toLowerCase().contains(songName))
            {
                ans=totalHTML.substring(i,i+60);
                ans="https://www.youtube.com"+ans.substring(7,27);
                break;}
            else
                totalHTML=totalHTML.substring(i+1);
            counter++;
        }
        Log.d(TAG, "doInBackground: occ are"+counter);
        Log.d(TAG, "doInBackground: ans is "+ans);
        String song_url=ans;
        MainActivity.downloadActivity.startDownloading(song_url);
        //Log.d("hell", "doInBackground: "+links.size());



        return  null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //Call your next task (ui thread)
        d.returnedUrl(songName);


    }
}