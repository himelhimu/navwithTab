package com.inducesmile.androidmusicplayer.fragment;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;

import com.inducesmile.androidmusicplayer.R;
import com.inducesmile.androidmusicplayer.adapter.SongAdapter;
import com.inducesmile.androidmusicplayer.controller.MusicController;
import com.inducesmile.androidmusicplayer.entities.SongObject;
import com.inducesmile.androidmusicplayer.models.Song;
import com.inducesmile.androidmusicplayer.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class SongFragment extends Fragment implements MediaController.MediaPlayerControl {
    private ArrayList<Song> songsList;
    private MusicService mMusicService;
    private MusicController mMusicController;
    private Intent playIntent;
    private boolean playbackPaused;

    public SongFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        getActivity().setTitle("Songs");
        RecyclerView songRecyclerView = (RecyclerView)view.findViewById(R.id.song_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        songRecyclerView.setLayoutManager(linearLayoutManager);
        songRecyclerView.setHasFixedSize(true);
        songsList= (ArrayList<Song>) getSongsList();
        SongAdapter mAdapter = new SongAdapter(getActivity(), songsList);
        songRecyclerView.setAdapter(mAdapter);

     //   setupController();

        return view;
    }


    public void songPicked(View view){
        mMusicService.setSong(Integer.parseInt(view.getTag().toString()));
        mMusicService.playSong();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        mMusicController.show(0);
    }


    private void playNext(){
        mMusicService.playNext();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        mMusicController.show(0);
    }

    private void playPrev(){
        mMusicService.playPrev();
        if(playbackPaused){
            setupController();
            playbackPaused=false;
        }
        mMusicController.show(0);
    }

    private boolean mMusicBound;
    private ServiceConnection musicServiceConnect=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder  musicBinder= (MusicService.MusicBinder) service;
            mMusicService=musicBinder.getService();

            mMusicService.setSongsList(songsList);
            mMusicBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mMusicBound=false;
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupController();

    }

    private void setupController() {
        mMusicController=new MusicController(getContext());
        mMusicController.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        mMusicController.setMediaPlayer(this);
        mMusicController.setAnchorView(getView().findViewById(R.id.song_list));
        mMusicController.setEnabled(true);

        if (playIntent==null){
            playIntent=new Intent(getActivity(),MusicService.class);
            getActivity().bindService(playIntent,musicServiceConnect, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);

        }
    }

    public List<Song> getSongsList() {
        List<Song> allSongList = new ArrayList<Song>();
        ContentResolver musicResolver=getActivity().getContentResolver();
        Uri musicUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor mCursor=musicResolver.query(musicUri,null,null,null,null);

        if (mCursor!=null && mCursor.moveToFirst()){
            do {
                Song song=new Song(mCursor.getLong(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)),
                        mCursor.getString(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                allSongList.add(song);
            }while (mCursor.moveToNext());

        }

        /*recentSongs.add(new SongObject("Adele", "Someone Like You", ""));
        recentSongs.add(new SongObject("Adele", "Someone Like You", ""));
        recentSongs.add(new SongObject("Adele", "Someone Like You", ""));
        recentSongs.add(new SongObject("Adele", "Someone Like You", ""));
        recentSongs.add(new SongObject("Adele", "Someone Like You", ""));
        recentSongs.add(new SongObject("Adele", "Someone Like You", ""));*/
        return allSongList;
    }

    @Override
    public void start() {
mMusicService.go();
    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(int pos) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
