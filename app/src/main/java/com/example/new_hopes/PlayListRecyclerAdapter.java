package com.example.new_hopes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class PlayListRecyclerAdapter
        extends RecyclerView.Adapter<PlayListRecyclerViewHolder> {

    List<PlayListData> list
            = Collections.emptyList();

    Context context;

    public PlayListRecyclerAdapter(List<PlayListData> list,
                                   Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public PlayListRecyclerViewHolder
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

        PlayListRecyclerViewHolder viewHolder
                = new PlayListRecyclerViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final PlayListRecyclerViewHolder viewHolder, final int position) {
        viewHolder.playlistPhoto.setImageResource(list.get(position).playlistPhoto);
        viewHolder.playlistname.setText(list.get(position).playlistName);


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

