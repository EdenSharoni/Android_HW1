package com.example.android_hw;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Functions extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getLegoHeight() {
        return getResources().getDrawable(R.drawable.blue_lego).getMinimumHeight();
    }

    public int getLegoWidth() {
        return getResources().getDrawable(R.drawable.player1).getMinimumWidth();
    }
}
