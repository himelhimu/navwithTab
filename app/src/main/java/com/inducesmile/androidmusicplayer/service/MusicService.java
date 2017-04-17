package com.inducesmile.androidmusicplayer.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;

import com.inducesmile.androidmusicplayer.MusicActivity;
import com.inducesmile.androidmusicplayer.R;
import com.inducesmile.androidmusicplayer.models.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> songsList;
    private int currSongPosition;
    private String songTitle;
    private Random random;

    private final IBinder musicBind=new MusicBinder();
    private boolean shuffle;

    @Override
    public void onCreate() {
        super.onCreate();

        currSongPosition=0;
        songTitle="";
        random=new Random();
        mMediaPlayer=new MediaPlayer();
        
        initMusicPlayer();
    }


    public void setSong(int songIndex){
        currSongPosition=songIndex;
    }

    private void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(),PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    public void setSongsList(ArrayList<Song> songsList) {
        this.songsList = songsList;
    }

    public void setCurrSongPosition(int currSongPosition) {
        this.currSongPosition = currSongPosition;
    }


   public void playSong(){
        mMediaPlayer.reset();
        Song playSong=songsList.get(currSongPosition);
        long currSong=playSong.getId();

        Uri trackUri= ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);

        try {
            mMediaPlayer.setDataSource(getApplicationContext(),trackUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
       return musicBind;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mp.start();

        Intent noIntent=new Intent(this, MusicActivity.class);
        noIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,
                noIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder=new Notification.Builder(this);
        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.musicicon)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing");

        Notification notf=builder.build();
        startForeground(1,notf);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopForeground(true);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mMediaPlayer.reset();
        return false;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    public class MusicBinder extends Binder {
      public   MusicService getService(){
            return MusicService.this;
        }
    }


    public int getPosn(){
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDur(){
        return mMediaPlayer.getDuration();
    }

    public boolean isPng(){
        return mMediaPlayer.isPlaying();
    }

    public void pausePlayer(){
        mMediaPlayer.pause();
    }

    public void seek(int posn){
        mMediaPlayer.seekTo(posn);
    }

    public void go(){
        mMediaPlayer.start();
    }

    //skip to previous track
    public void playPrev(){
        currSongPosition--;
        if(currSongPosition<0) currSongPosition=songsList.size()-1;
        playSong();
    }

    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = currSongPosition;
            while(newSong==currSongPosition){
                newSong=random.nextInt(songsList.size());
            }
            currSongPosition=newSong;
        }
        else{
            currSongPosition++;
            if(currSongPosition>=songsList.size()) currSongPosition=0;
        }
        playSong();
    }
}
