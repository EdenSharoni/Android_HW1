package com.example.android_hw;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String id;
    private String name;
    private int score;
    private boolean vibrateSettings;
    private boolean musicSettings;


    public User() {
    }

    public User(String id, String name, int score, boolean vibrateSettings, boolean musicSettings) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.vibrateSettings = vibrateSettings;
        this.musicSettings = musicSettings;
    }

    public void setVibrateSettings(boolean vibrateSettings) {
        this.vibrateSettings = vibrateSettings;
    }

    public void setMusicSettings(boolean musicSettings) {
        this.musicSettings = musicSettings;
    }

    public boolean isVibrateSettings() {
        return vibrateSettings;
    }

    public boolean isMusicSettings() {
        return musicSettings;
    }


    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
