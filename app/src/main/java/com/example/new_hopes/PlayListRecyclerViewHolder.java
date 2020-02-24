package com.example.new_hopes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


class PlayListRecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView playlistname;
    ImageView playlistPhoto;


    public PlayListRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        playlistPhoto=itemView.findViewById(R.id.playlist_photo);
        playlistname=itemView.findViewById(R.id.playlist_name_tv);
    }
}
