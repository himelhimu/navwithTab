package com.inducesmile.androidmusicplayer.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inducesmile.androidmusicplayer.R;
import com.inducesmile.androidmusicplayer.entities.SongObject;
import com.inducesmile.androidmusicplayer.models.Song;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongViewHolder>{

    private Context context;
    private List<Song> allSongs;

    public SongAdapter(Context context, List<Song> allSongs) {
        this.context = context;
        this.allSongs = allSongs;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_list_layout, parent, false);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song songs = allSongs.get(position);
        holder.songTitle.setText(songs.getTitle());
        holder.songAuthor.setText(songs.getArtist());
    }

    @Override
    public int getItemCount() {
        return allSongs.size();
    }

}
