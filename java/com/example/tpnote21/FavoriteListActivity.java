package com.example.tpnote21;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class FavoriteListActivity extends AppCompatActivity {
    private ListView favoriteListView;
    private List<String> favoriteSongsList;
    private FavoriteSongsDatabase favoriteSongsDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);


        favoriteListView = findViewById(R.id.favoriteListView);


        favoriteSongsDatabase = FavoriteSongsDatabase.getInstance(this);
        favoriteSongsList = favoriteSongsDatabase.getFavoriteSongs();

        // Retrieve the selected song from the intent
        String selectedSong = getIntent().getStringExtra("selectedSong");
        if (selectedSong != null) {
            favoriteSongsDatabase.addFavoriteSong(selectedSong);
            favoriteSongsList.add(selectedSong);
        }

        FavoriteSongsAdapter favoriteSongsAdapter = new FavoriteSongsAdapter(this, favoriteSongsList);
        favoriteListView.setAdapter(favoriteSongsAdapter);
        favoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click
                Song selectedSong = (Song) parent.getItemAtPosition(position);
                String selectedSongTitle = selectedSong.getTitle();
                // Implement your logic here for playing the selected song
            }
        });

        favoriteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle long item click
                Song selectedSong = (Song) parent.getItemAtPosition(position);
                String selectedSongTitle = selectedSong.getTitle();
                showRemoveConfirmationDialog(selectedSongTitle);
                return true;
            }
        });

        updateUI();
    }

    private void updateUI() {
        if (favoriteSongsList.isEmpty()) {
            favoriteListView.setVisibility(View.GONE);
        } else {
            favoriteListView.setVisibility(View.VISIBLE);

        }
    }
    private void showRemoveConfirmationDialog(final String songTitle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Song")
                .setMessage("Are you sure you want to remove this song from favorites?")
                .setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        favoriteSongsDatabase.removeFavoriteSong(getApplicationContext(), songTitle);

                        favoriteSongsList.remove(songTitle);
                        FavoriteSongsAdapter favoriteSongsAdapter = (FavoriteSongsAdapter) favoriteListView.getAdapter();
                        if (favoriteSongsAdapter != null) {
                            favoriteSongsAdapter.notifyDataSetChanged();
                        }
                        updateUI();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

}
