package com.example.tpnote21;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class AddSongActivity extends AppCompatActivity {
    private EditText urlInput;
    private Button downloadButton;
    private TextView downloadedSongTextView;

    private long downloadId; // Track the download ID for later reference
    private BroadcastReceiver downloadReceiver; // BroadcastReceiver to listen for download completion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_song);

        urlInput = findViewById(R.id.urlInput);
        downloadButton = findViewById(R.id.downloadButton);
        downloadedSongTextView = findViewById(R.id.downloadedSongTextView);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve the URL input provided by the user
                String songUrl = urlInput.getText().toString();

                // Perform the download using the songUrl and save the downloaded file to the desired location
                downloadSong(songUrl);
            }
        });
    }

    private void downloadSong(String songUrl) {
        // Check if the necessary permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
            return;
        }

        // Create a DownloadManager.Request with the given songUrl
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(songUrl));
        request.setTitle("Downloading Song");
        request.setDescription("Please wait while the song is being downloaded...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "MuSongs/" + System.currentTimeMillis() + ".mp3");

        // Enqueue the download and save the download ID
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        downloadId = downloadManager.enqueue(request);

        // Register a BroadcastReceiver to listen for download completion
        registerDownloadReceiver();
    }

    private void registerDownloadReceiver() {
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {
                    // Download completed for the requested song
                    // Update the UI accordingly
                    String downloadedSongTitle =  getDownloadedSongTitle(downloadId);
                    if (downloadedSongTitle != null) {
                        // Update the UI with the downloaded song information
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                downloadedSongTextView.setText("Downloaded Song: " + downloadedSongTitle);
                            }
                        });
                    } else {
                        Toast.makeText(AddSongActivity.this, "Failed to get the downloaded song title", Toast.LENGTH_SHORT).show();
                    }

                    // Unregister the BroadcastReceiver
                    unregisterDownloadReceiver();
                }
            }
        };

        // Register the BroadcastReceiver to listen for download completion
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
    }

    private void unregisterDownloadReceiver() {
        // Unregister the BroadcastReceiver
        if (downloadReceiver != null) {
            unregisterReceiver(downloadReceiver);
            downloadReceiver = null;
        }
    }

    private String getDownloadedSongTitle(long downloadId) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int titleIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TITLE);
            String title = cursor.getString(titleIndex);
            cursor.close();
            return title;
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the BroadcastReceiver when the activity is destroyed
        unregisterDownloadReceiver();
    }}