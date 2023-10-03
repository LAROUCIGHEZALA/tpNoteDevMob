package com.example.tpnote21;

    public class Song {
        public static final String COLUMN_ID = "_id";
        private int id;
        private String title;
        private String artist;
        private String duration;

        public Song(int id,String title, String artist, String duration) {
           this.id=id;
            this.title = title;
            this.artist = artist;
            this.duration = duration;

        }
        public int getId() {

            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getArtist() {
            return artist;
        }

        public String getDuration() {
            return duration;
        }

    }


