package com.example.android_hw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.OnTextChanged;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private CheckBox vibrate;
    private CheckBox music;
    private ImageView backBtn;
    private boolean vibrateBoolean;
    private boolean musicBoolean;
    private int vibrationNumber;
    private FirebaseUser user;
    private EditText userName;
    private SeekBar vibrateSeekBar;
    private TextView seekBarProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle bundle = getIntent().getExtras();
        vibrateSeekBar = findViewById(R.id.vibrateSeekBar);
        seekBarProcess = findViewById(R.id.seekBarProcess);
        vibrate = findViewById(R.id.vibrateCheckbox);
        music = findViewById(R.id.musicCheckbox);
        backBtn = findViewById(R.id.backBtn);
        userName = findViewById(R.id.userName);


        user = getInstance().getCurrentUser();

        if (user == null) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
            userName.addTextChangedListener(this);
        }
        if (bundle != null) {
            vibrateBoolean = bundle.getBoolean(String.valueOf(R.string.vibrate));
            musicBoolean = bundle.getBoolean(String.valueOf(R.string.music));
            userName.setText(bundle.getString("name"));
            vibrationNumber = bundle.getInt(getString(R.string.vibrationNumber));
        } else {
            vibrateBoolean = true;
            musicBoolean = true;
            vibrationNumber = 80;
        }

        vibrateSeekBar.setProgress(vibrationNumber);
        seekBarProcess.setText(Integer.toString(vibrationNumber));

        if (!vibrateBoolean) {
            vibrate.setChecked(false);
            vibrateSeekBar.setVisibility(View.GONE);
            seekBarProcess.setVisibility(View.GONE);
        }
        if (!musicBoolean) {
            music.setChecked(false);
        }

        vibrateSeekBar.setOnSeekBarChangeListener(this);
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
            if (vibrate.isChecked()) {
                if (vibrateSeekBar.getProgress() == 0) {
                    vibrateSeekBar.setProgress(80);
                }
                seekBarProcess.setVisibility(View.VISIBLE);
                vibrateSeekBar.setVisibility(View.VISIBLE);
            } else if (!vibrate.isChecked()) {
                seekBarProcess.setVisibility(View.GONE);
                vibrateSeekBar.setVisibility(View.GONE);
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
        intent.putExtra(getString(R.string.vibrationNumber), Integer.parseInt(seekBarProcess.getText().toString()));
        intent.putExtra("name", userName.getText().toString());
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
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarProcess.setText(String.valueOf(progress));
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
