package com.example.tpnote21;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

class FavoriteSongsAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> favoriteSongsList;
    private FavoriteSongsDatabase favoriteSongsDatabase;

    public FavoriteSongsAdapter(FavoriteListActivity activity, List<String> favoriteSongsList) {
        super(activity, 0, favoriteSongsList);
        this.context = activity.getApplicationContext();
        this.favoriteSongsList = favoriteSongsList;
        favoriteSongsDatabase = FavoriteSongsDatabase.getInstance(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_favorite_song, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.songTitleTextView = convertView.findViewById(R.id.songTitleTextView);
            viewHolder.removeButton = convertView.findViewById(R.id.removeButton);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final String songTitle = favoriteSongsList.get(position);
        viewHolder.songTitleTextView.setText(songTitle);

        viewHolder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSong(songTitle);
            }
        });

        return convertView;
    }

    private void removeSong(String songTitle) {
        favoriteSongsList.remove(songTitle);
        notifyDataSetChanged();

        // Remove the song from the database
        favoriteSongsDatabase.removeFavoriteSong(context.getApplicationContext(), songTitle);
    }

    private static class ViewHolder {
        TextView songTitleTextView;
        Button removeButton;
    }
}
