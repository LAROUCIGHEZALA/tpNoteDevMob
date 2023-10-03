/*package com.example.tpnote21;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.tpnote21.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicPlayerService extends Service {

    private static final String CHANNEL_ID = "MusicPlayerChannel";
    private static final int NOTIFICATION_ID = 1;
    private static final String ACTION_PLAY = "com.example.tpnote21.ACTION_PLAY";
    private static final String ACTION_PAUSE = "com.example.tpnote21.ACTION_PAUSE";
    private static final String ACTION_NEXT = "com.example.tpnote21.ACTION_NEXT";
    private static final String ACTION_PREVIOUS = "com.example.tpnote21.ACTION_PREVIOUS";

    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private int songIndex;
    private boolean isPlaying = false;
    private boolean isPaused = false;
    private boolean isCompleted = false;

    private NotificationManagerCompat notificationManager;
    private MediaSessionCompat mediaSessionCompat;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        notificationManager = NotificationManagerCompat.from(this);

        // Create a MediaSessionCompat
        mediaSessionCompat = new MediaSessionCompat(this, "MusicPlayerSession");
        mediaSessionCompat.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onPlay() {
                super.onPlay();
                play();
            }

            @Override
            public void onPause() {
                super.onPause();
                pause();
            }

            @Override
            public void onSkipToNext() {
                super.onSkipToNext();
                next();
            }

            @Override
            public void onSkipToPrevious() {
                super.onSkipToPrevious();
                previous();
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public void setSongIndex(int songIndex) {
        this.songIndex = songIndex;
    }

    public void play() {
        if (!isPlaying && !isPaused) {
            try {
                Song song = songList.get(songIndex);
                // Prepare and start the media player
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.getFilePath());
                mediaPlayer.prepare();
                mediaPlayer.start();

                isPlaying = true;
                isPaused = false;
                isCompleted = false;

                showNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (isPaused) {
            mediaPlayer.start();
            isPlaying = true;
            isPaused = false;
            isCompleted = false;

            showNotification();
        }
    }

    public void pause() {
        if (isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            isPaused = true;

            showNotification();
        }
    }

    public void next() {
        if (songIndex < songList.size() - 1) {
            songIndex++;
            play();
        } else {
            // Reached the end of the playlist
            // You can handle what to do in this case
        }
    }

    public void previous() {
        if (songIndex > 0) {
            songIndex--;
            play();
        } else {
            // Reached the beginning of the playlist
            // You can handle what to do in this case
        }
    }

    private void showNotification() {
        Song song = songList.get(songIndex);

        // Create a pending intent for the notification
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create intents for the media controls
        Intent playIntent = new Intent(ACTION_PLAY);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent pauseIntent = new Intent(ACTION_PAUSE);
        PendingIntent pausePendingIntent = PendingIntent.getBroadcast(this, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent nextIntent = new Intent(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent previousIntent = new Intent(ACTION_PREVIOUS);
        PendingIntent previousPendingIntent = PendingIntent.getBroadcast(this, 0, previousIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create remote views for the custom notification layout
        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_layout);
        notificationLayout.setImageViewResource(R.id.notification_icon, R.drawable.ic_music);
        notificationLayout.setTextViewText(R.id.notification_title, song.getTitle());
        notificationLayout.setTextViewText(R.id.notification_artist, song.getArtist());
        notificationLayout.setOnClickPendingIntent(R.id.notification_play, playPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notification_pause, pausePendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notification_next, nextPendingIntent);
        notificationLayout.setOnClickPendingIntent(R.id.notification_previous, previousPendingIntent);

        // Create a notification channel for Android 8.0 and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Music Player", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(channel);
        }

        // Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_music)
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(isPlaying);

        // Display the notification
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        notificationManager.cancel(NOTIFICATION_ID);
    }
}*/