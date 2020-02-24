package com.example.new_hopes;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


class SongPlayRecyclerViewHolder extends RecyclerView.ViewHolder {
    TextView sportName;
    ImageView songPhoto;


    public SongPlayRecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        songPhoto=itemView.findViewById(R.id.image_song);
    }
}
