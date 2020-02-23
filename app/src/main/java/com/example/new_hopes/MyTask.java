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
        String URL = null;
        String url = "https://www.youtube.com/results?search_query="+songName.replace(" ","+");
        //url = "http://zetcode.com";
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
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                            .maxBodySize(0)
                            .timeout(0)
                            .get();


                } catch (IOException e) {
                    Log.d("hell", e.getMessage().toString());
                }
        Log.d("hell", "read9 " + doc[0].body().html());
        Elements links = doc[0].select("a[id=video-title]");
        //Log.d("hell", "doInBackground: "+link);

        Log.d(TAG, "doInBackground: "+doc[0].html().length());
        //Log.d("hell", "doInBackground: "+links.size());



        return  null;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        //Call your next task (ui thread)
        d.returnedUrl(songName);


    }
}