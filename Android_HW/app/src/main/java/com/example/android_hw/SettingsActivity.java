package com.example.android_hw;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private CheckBox vibrate;
    private CheckBox music;
    private ImageView backBtn;
    private boolean vibrateBoolean;
    private boolean musicBoolean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            vibrateBoolean = bundle.getBoolean(String.valueOf(R.string.vibrate));
            musicBoolean = bundle.getBoolean(String.valueOf(R.string.music));
        } else {
            vibrateBoolean = true;
            musicBoolean = true;
        }
        vibrate = findViewById(R.id.vibrateCheckbox);
        music = findViewById(R.id.musicCheckbox);
        backBtn = findViewById(R.id.backBtn);
        if (!vibrateBoolean) {
            vibrate.setChecked(false);
        }
        if (!musicBoolean) {
            music.setChecked(false);
        }

        backBtn.setOnClickListener(this);
        vibrate.setOnCheckedChangeListener(this);
        music.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(vibrate)) {
            if (buttonView.isChecked()) {
                vibrateBoolean = true;
            } else {
                vibrateBoolean = false;
            }
        }
        if (buttonView.equals(music)) {
            if (buttonView.isChecked()) {
                musicBoolean = true;
            } else {
                musicBoolean = false;
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra(String.valueOf(R.string.vibrate), vibrateBoolean);
        intent.putExtra(String.valueOf(R.string.music), musicBoolean);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
