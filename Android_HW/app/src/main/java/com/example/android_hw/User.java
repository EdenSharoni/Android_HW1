package com.example.android_hw;

import android.location.Address;
import android.location.Location;

import java.io.Serializable;
import java.util.List;

public class User implements Serializable {
    private String id;
    private String name;
    private int score;
    private boolean musicSettings;
    private int vibrationNumber;
    private String controls;
    private double latitude;
    private double longitude;

    public User() {
    }

    public User(String id, String name, int score, boolean musicSettings, int vibrationNumber, String controls, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.score = score;
        this.musicSettings = musicSettings;
        this.vibrationNumber = vibrationNumber;
        this.controls = controls;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setControls(String controls) {
        this.controls = controls;
    }

    public String getControls() {
        return controls;
    }

    public void setVibrationNumber(int vibrationNumber) {
        this.vibrationNumber = vibrationNumber;
    }

    public int getVibrationNumber() {
        return vibrationNumber;
    }

    public void setMusicSettings(boolean musicSettings) {
        this.musicSettings = musicSettings;
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
