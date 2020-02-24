package com.example.new_hopes;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class SongsListViewHolder extends RecyclerView.ViewHolder {

    TextView songNameText,songArtistText;
    ImageView songPhotoIV;

    public SongsListViewHolder(@NonNull View itemView) {
        super(itemView);
        songNameText=itemView.findViewById(R.id.songName);
        songArtistText=itemView.findViewById(R.id.songArtist);
        songPhotoIV=itemView.findViewById(R.id.songPhoto);
    }
}