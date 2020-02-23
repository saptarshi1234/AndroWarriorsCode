package com.example.new_hopes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

class SongsListRecyclerAdapter
        extends RecyclerView.Adapter<SongsListViewHolder> {

    List<SongsListData> list
            = Collections.emptyList();

    Context context;

    public SongsListRecyclerAdapter(List<SongsListData> list,
                                Context context)
    {
        this.list = list;
        this.context = context;
    }

    @Override
    public SongsListViewHolder
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
                .inflate(R.layout.song_name_layout,
                        parent, false);

        SongsListViewHolder viewHolder
                = new SongsListViewHolder(photoView);
        return viewHolder;
    }

    @Override
    public void
    onBindViewHolder(final SongsListViewHolder viewHolder,
                     final int position)
    {

        viewHolder.songNameText
                .setText(list.get(position).songName);
        viewHolder.songArtistText
                .setText(list.get(position).songArtist);
        viewHolder.songPhotoIV.setImageResource(list.get(position).songPhoto);

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

    // Sample data for RecyclerView

}

