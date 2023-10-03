package com.example.tpnote21;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Button playButton;
    private ImageView favoriteIcon;
    private TextView songTitle;
    private Button previousButton;
    private Button nextButton;
    private ImageView downloadIcon;

    private boolean isPlaying = false;
    private boolean isFavorite = false;

    private MediaPlayer mediaPlayer;
    private static final int ADD_SONG_REQUEST_CODE = 1;

    private static final String CHANNEL_ID = "music_channel";
    private static final int NOTIFICATION_ID = 1;

    private List<String> songList;
    private int currentSongIndex;

    private FavoriteSongsDatabase favoriteSongsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        playButton = findViewById(R.id.playButton);
        favoriteIcon = findViewById(R.id.favoriteIcon);
        songTitle = findViewById(R.id.songTitle);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        downloadIcon = findViewById(R.id.downloadIcon);

        createNotificationChannel();

        // Initialize the song list
        songList = getSongList();

        favoriteSongsDatabase = FavoriteSongsDatabase.getInstance(this);



        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    pausePlayback();
                } else {
                    startPlayback();
                }
            }
        });
        favoriteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedSongTitle = songList.get(currentSongIndex);

                Intent intent = new Intent(MainActivity.this, FavoriteListActivity.class);
                intent.putExtra("selectedSong", selectedSongTitle); // Replace "Your selected song" with the actual selected song
                startActivity(intent);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPreviousSong();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNextSong();
            }
        });
        downloadIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddSongActivity.class);
                startActivityForResult(intent, ADD_SONG_REQUEST_CODE);
            }
        });
    }

    private void playPreviousSong() {
        if (currentSongIndex > 0) {
            currentSongIndex--;
            if (isPlaying) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                startPlayback();
            } else {
                updateSongTitle();
            }
        }
    }

    private void playNextSong() {
        if (currentSongIndex < songList.size() - 1) {
            currentSongIndex++;
            if (isPlaying) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
                startPlayback();
            } else {
                updateSongTitle();
            }
        }
    }

    private void updateSongTitle() {
        songTitle.setText(songList.get(currentSongIndex));
    }

    private List<String> getSongList() {
        List<String> songList = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {BaseColumns._ID, MediaStore.Audio.Media.TITLE};
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        Cursor cursor = contentResolver.query(uri, projection, selection, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);

            do {
                String songTitle = cursor.getString(titleColumn);
                songList.add(songTitle);
            } while (cursor.moveToNext());

            cursor.close();
        }

        return songList;
    }

    private void startPlayback() {
        // Check if there are songs available
        if (songList.isEmpty()) {
            return;
        }

        // Play the current song
        mediaPlayer = MediaPlayer.create(this, getSongUri(currentSongIndex));
        mediaPlayer.start();
        isPlaying = true;
        playButton.setText("Pause");

        // Show notification
        showNotification();

        // Update UI
        songTitle.setText(songList.get(currentSongIndex));
    }

    private void pausePlayback() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            playButton.setText("Play");
        }
    }

    private void addFavorite() {
        if (songList.isEmpty()) {
            Toast.makeText(MainActivity.this, "No files to add as favorite.", Toast.LENGTH_SHORT).show();
            return;
        }

        String currentSong = songList.get(currentSongIndex);
        favoriteSongsDatabase.addFavoriteSong(currentSong);
        isFavorite = true;
        favoriteIcon.setImageResource(R.drawable.ic_add_favorate);
    }

    private void removeFavorite() {
        String currentSong = songList.get(currentSongIndex);
        favoriteSongsDatabase.removeFavoriteSong(getApplicationContext(), currentSong);
        isFavorite = false;
        favoriteIcon.setImageResource(R.drawable.ic_remove_favorate);
    }

    private void showNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        // Create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Music Player")
                .setContentText("Now playing: " + songList.get(currentSongIndex))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(false)
                .setOngoing(true);

        // Add action buttons (Next, Previous, Pause)
        builder.addAction(R.drawable.ic_previous, "Previous", null);
        builder.addAction(R.drawable.ic_pause, "Pause", null);
        builder.addAction(R.drawable.ic_next, "Next", null);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Music Channel";
            String description = "Channel for Music Player notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private Uri getSongUri(int songIndex) {
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {BaseColumns._ID};
        String selection = MediaStore.Audio.Media.TITLE + "=?";
        String[] selectionArgs = {songList.get(songIndex)};
        Cursor cursor = contentResolver.query(uri, projection, selection, selectionArgs, null);

        if (cursor != null && cursor.moveToFirst()) {
            int idColumn = cursor.getColumnIndex(BaseColumns._ID);
            long id = cursor.getLong(idColumn);
            cursor.close();
            return Uri.withAppendedPath(uri, String.valueOf(id));
        }

        return null;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}







