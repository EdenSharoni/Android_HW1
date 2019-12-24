package com.example.android_hw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private CheckBox vibrate;
    private CheckBox music;
    private ImageView backBtn;
    private EditText userName;
    private SeekBar vibrateSeekBar;
    private TextView seekBarProcess;
    private User localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        findById();
        getBundleAndGetUser();
        setOnClick();
    }

    private void setOnClick() {
        vibrateSeekBar.setOnSeekBarChangeListener(this);
        backBtn.setOnClickListener(this);
        vibrate.setOnCheckedChangeListener(this);
        music.setOnCheckedChangeListener(this);
    }

    private void getBundleAndGetUser() {
        Bundle bundle = getIntent().getExtras();
        if (getInstance().getCurrentUser() == null) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
            userName.addTextChangedListener(this);
        }

        if (bundle != null) {
            localUser = (User) bundle.get("local User");
            userName.setText(localUser.getName());
            vibrateSeekBar.setProgress(localUser.getVibrationNumber());
            seekBarProcess.setText(String.valueOf(vibrateSeekBar.getProgress()));
        } else {
            localUser = new User();
            userName.setText("");
            vibrateSeekBar.setProgress(80);
            seekBarProcess.setText("80");
        }

        if (!localUser.isVibrateSettings()) {
            vibrate.setChecked(false);
            vibrateSeekBar.setVisibility(View.GONE);
            seekBarProcess.setVisibility(View.GONE);
        }
        if (!localUser.isMusicSettings()) {
            music.setChecked(false);
        }
    }

    private void findById() {
        vibrateSeekBar = findViewById(R.id.vibrateSeekBar);
        seekBarProcess = findViewById(R.id.seekBarProcess);
        vibrate = findViewById(R.id.vibrateCheckbox);
        music = findViewById(R.id.musicCheckbox);
        backBtn = findViewById(R.id.backBtn);
        userName = findViewById(R.id.userName);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.equals(vibrate)) {
            if (vibrate.isChecked()) {
                localUser.setVibrateSettings(true);
                if (vibrateSeekBar.getProgress() == 0) {
                    vibrateSeekBar.setProgress(80);
                }
                seekBarProcess.setVisibility(View.VISIBLE);
                vibrateSeekBar.setVisibility(View.VISIBLE);
            } else {
                localUser.setVibrateSettings(false);
                seekBarProcess.setVisibility(View.GONE);
                vibrateSeekBar.setVisibility(View.GONE);
            }
        }
        if (buttonView.equals(music)) {
            localUser.setMusicSettings(music.isChecked());
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (localUser != null) {
            intent.putExtra(getString(R.string.localUser), localUser);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (userName.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "please fill name", Toast.LENGTH_LONG).show();
        } else {
            localUser.setName(userName.getText().toString());
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarProcess.setText(String.valueOf(progress));
        localUser.setVibrationNumber(vibrateSeekBar.getProgress());
        if (progress == 0) {
            vibrate.setChecked(false);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
