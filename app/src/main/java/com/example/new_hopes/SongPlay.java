package com.example.new_hopes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.example.new_hopes.GlobalFunctions.getSongPath;

public class SongPlay extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    MediaPlayer mPlayer;
    static TextToSpeech textToSpeech;
    RecyclerView recyclerView;
    SongPlayRecyclerAdapter adapter;
    List<SongPlayData> list = new ArrayList<>();
    static List<String[]> songsInfo=new ArrayList<>();

    @SuppressLint("StaticFieldLeak")
    static TextView songName,songArtist,time_el1,time_tot;
    @SuppressLint("StaticFieldLeak")
    static ImageButton forward,backward,repeat,shuffle;

    static FloatingActionButton play;
    GestureDetector gestureDetector;

    private static final String TAG = "SongPlay";

    void generateSongData(List<String[]> abc){
        ArrayList<String> song_names=getPlayLists();
        String[] array=song_names.toArray(new String[0]);

        for (int i=0;i<array.length;i++)
        {
            String temp[]={array[0],array[0],getSongLoc(array[0])};
            abc.add(temp);
        }
    }


    ArrayList<String> getPlayLists()
    {
        ArrayList<String> names=new ArrayList<String>();
        File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath() +File.separator +"new_hopes21"+File.separator);
        ArrayList<PlayList> xyz=(ArrayList<PlayList>) CollectSongs.restore_state(file);
        for (PlayList playList : xyz){
            for(Song song: playList.songs){
                names.add(song.name);
            }
        }

        return names;
    }

    String getSongLoc(String name)
    {
        final File savedPlaylistFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + "new_hopes21" + File.separator+"songs1"+File.separator+name);
        return savedPlaylistFile.getAbsolutePath();




    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_play);
        /*Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
*/

        gestureDetector = new GestureDetector(SongPlay.this, SongPlay.this);

        list.add(new SongPlayData(R.drawable.eminem));
        list.add(new SongPlayData(R.drawable.ic_action_voice_search));
        generateSongData(songsInfo);

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
                while (mPlayer.isPlaying()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            seekbar.setMax(mPlayer.getDuration() / 1000);
                            time_el1.setText(getDurationString(mPlayer
                                    .getCurrentPosition() / 1000));
                            seekbar.setProgress(mPlayer
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

        textToSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

        play=findViewById(R.id.SongPlayFabPlay);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(GlobalFunctions.isPlaying) {
//                    t.stop();
                    GlobalFunctions.pausePlaying();
                }
                else{
                 //   t.start();
                    try {
                        startPlaying(getSongLoc(songName.getText().toString())+File.separator+".mp3");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

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

    static void speak(String text){
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    void startPlaying(String song_name) throws IOException {
        try {
            String path = getSongLoc(songName.getText().toString()) + ".mp3";
            Log.d(TAG, "startPlaying: hdsfjhbfj" + path);
            MediaPlayer mp = new MediaPlayer();
            mp.setDataSource(path);

            // mp=MediaPlayer.create(this, Uri.parse(path));
            mp.prepareAsync();

            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }catch (Exception e){}

        /*Toast.makeText(this, "path is"+path, Toast.LENGTH_SHORT).show();
        try {
            mPlayer.setDataSource(path);
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mPlayer.prepareAsync();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: I was called");
        Toast.makeText(this, "double tap working", Toast.LENGTH_SHORT).show();
        play.performClick();
        speak("hi");
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    public void onPause(){
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }
}
/*
public class SongPlay extends AppCompatActivity {
    private static final String TAG = "SongPlay";
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_song_play);

        @SuppressLint("WrongViewCast")
        Button button = findViewById(R.id.forward);
        button.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
                Toast.makeText(getApplicationContext(), "Swiped top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                Toast.makeText(getApplicationContext(), "Swiped right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                Toast.makeText(getApplicationContext(), "Swiped left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                Toast.makeText(getApplicationContext(), "Swiped bottom", Toast.LENGTH_SHORT).show();
            }

        });
    }

    class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener(Context ctx) {
            gestureDetector = new GestureDetector(ctx, new GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 300;
            private static final int SWIPE_VELOCITY_THRESHOLD = 300;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                Log.i("TAG", "onSingleTapConfirmed:");
                Toast.makeText(getApplicationContext(), "Single Tap Detected", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                Log.i("TAG", "onLongPress:");
                Toast.makeText(getApplicationContext(), "Long Press Detected", Toast.LENGTH_SHORT).show();
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(getApplicationContext(), "Double Tap Detected", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}*/
