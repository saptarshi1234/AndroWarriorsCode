package com.example.new_hopes;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SongPlayRecyclerAdapter
        extends RecyclerView.Adapter<SongPlayRecyclerViewHolder> {

    List<SongPlayData> list
            = Collections.emptyList();

    Context context;

    public SongPlayRecyclerAdapter(List<SongPlayData> list,
                                     Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public SongPlayRecyclerViewHolder
    onCreateViewHolder(ViewGroup parent,
                       int viewType)
    {

        Context context
                = parent.getContext();
        LayoutInflater inflater
                = LayoutInflater.from(context);

        // Inflate the layout

        View photoView
                = inflater
                .inflate(R.layout.song_play_photo,
                        parent, false);

        SongPlayRecyclerViewHolder viewHolder
                = new SongPlayRecyclerViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SongPlayRecyclerViewHolder viewHolder, final int position) {
        viewHolder.songPhoto.setImageResource(list.get(position).songPhoto);
        SongPlay.changeNames(position);

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(
            RecyclerView recyclerView)
    {
        super.onAttachedToRecyclerView(recyclerView);
    }
}

