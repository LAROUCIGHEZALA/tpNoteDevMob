package com.example.tpnote21;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends BaseAdapter {

    private Context context;
    private List<Song> songList;

    public SongAdapter(Context context, List<Song> songList) {
        this.context = context;
        this.songList = songList;
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        }

        TextView txtSongTitle = convertView.findViewById(R.id.txtSongTitle);
        TextView txtSongArtist = convertView.findViewById(R.id.txtSongArtist);

        Song song = songList.get(position);

        txtSongTitle.setText(song.getTitle());
        txtSongArtist.setText(song.getArtist());

        return convertView;
    }
}
