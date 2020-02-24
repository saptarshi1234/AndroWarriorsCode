package com.example.new_hopes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SongPlay extends AppCompatActivity {

    RecyclerView recyclerView;
    SongPlayRecyclerAdapter adapter;
    List<SongPlayData> list = new ArrayList<>();
    static List<String[]> songsInfo=new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    static TextView songName,songArtist,time_el1,time_tot;
    @SuppressLint("StaticFieldLeak")
    static ImageButton forward,backward,repeat,shuffle;

    FloatingActionButton play;

    private static final String TAG = "SongPlay";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/

        list.add(new SongPlayData(R.drawable.eminem));
        list.add(new SongPlayData(R.drawable.ic_action_voice_search));

        recyclerView
                = findViewById(
                R.id.songPlayRV);
        adapter
                = new SongPlayRecyclerAdapter(
                list, Objects.requireNonNull(getApplication()));
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(
                new CustomLinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        // For perfect Scrolling
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        forward=findViewById(R.id.forward);
        backward=findViewById(R.id.backward);
        shuffle=findViewById(R.id.shuffle);
        repeat=findViewById(R.id.repeat);

        final ProgressBar seekbar=findViewById(R.id.SongPlayProgressBar);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            public void run() {
                while (GlobalFunctions.mPlayer.isPlaying()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            seekbar.setMax(GlobalFunctions.mPlayer.getDuration() / 1000);
                            time_el1.setText(getDurationString(GlobalFunctions.mPlayer
                                    .getCurrentPosition() / 1000));
                            seekbar.setProgress(GlobalFunctions.mPlayer
                                    .getCurrentPosition() / 1000);
                        }
                    });
                }
            }
        };
        final Thread t=new  Thread(runnable);

        songArtist=findViewById(R.id.songPlaySongArtist);
        songName=findViewById(R.id.songPlaySongName);

        time_el1=findViewById(R.id.SongPlayTimeEl);
        time_tot=findViewById(R.id.SongPlayTimeTotal);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d(TAG, "onScrollStateChanged: "+newState);
            }
        });

        play=findViewById(R.id.SongPlayFabPlay);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GlobalFunctions.isPlaying) {
                    t.stop();
                    GlobalFunctions.pausePlaying();
                }
                else{
                    t.start();
                    GlobalFunctions.resumePlaying(songName.getText().toString());}
               /* if(GlobalFunctions.isPlaying)
                    play.setBackgroundDrawable(R.drawable.playing);
                else
                    play.setBackgroundDrawable(R.drawable.paused);*/
            }
        });

        
    }

    static void changeNames(int i){
        songName.setText(songsInfo.get(i)[0]);
        songArtist.setText(songsInfo.get(i)[1]);
        time_el1.setText("0:00");
        time_tot.setText(songsInfo.get(i)[2]);

    }

    String getDurationString(int a){
        String temp=a/60+":";
        if(a%60<10)
            temp+="0"+a%60;
        else
            temp+=a%60;
        return temp;

    }



}
