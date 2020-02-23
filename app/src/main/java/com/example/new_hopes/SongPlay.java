package com.example.new_hopes;

import android.os.Bundle;
import android.view.View;

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


        
    }

}
