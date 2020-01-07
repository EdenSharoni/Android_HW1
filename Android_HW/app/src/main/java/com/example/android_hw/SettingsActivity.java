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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;
import butterknife.OnTextChanged;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher, SeekBar.OnSeekBarChangeListener {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    @BindView(R.id.musicCheckbox)
    CheckBox music;
    @BindView(R.id.userName)
    EditText userName;
    @BindView(R.id.vibrateSeekBar)
    SeekBar vibrateSeekBar;
    @BindView(R.id.seekBarProcess)
    TextView seekBarProcess;
    @BindView(R.id.screenBtn)
    ImageView screenBtn;
    @BindView(R.id.motionBtn)
    ImageView motionBtn;

    private User localUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        initSettings();
    }

    private void initSettings() {
        vibrateSeekBar.setOnSeekBarChangeListener(this);

        if (getInstance().getCurrentUser() == null) {
            userName.setVisibility(View.GONE);
        } else {
            userName.setVisibility(View.VISIBLE);
            userName.addTextChangedListener(this);
        }

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            localUser = (User) bundle.get(getString(R.string.localUser));
            userName.setText(localUser.getName());
            vibrateSeekBar.setProgress(localUser.getVibrationNumber());
            seekBarProcess.setText(String.valueOf(vibrateSeekBar.getProgress()));
            if (localUser.getControls().equals(getString(R.string.screen))) {
                screenBtn.setBackground(getDrawable(R.color.red));
            } else {
                motionBtn.setBackground(getDrawable(R.color.red));
            }
        } else {
            localUser = new User();
            userName.setText("");
            vibrateSeekBar.setProgress(80);
            seekBarProcess.setText("80");
            screenBtn.setBackground(getDrawable(R.color.red));
        }

        if (!localUser.isMusicSettings()) {
            music.setChecked(false);
        }
    }

    @Override
    @OnCheckedChanged(R.id.musicCheckbox)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        localUser.setMusicSettings(music.isChecked());
    }

    @Override
    @OnClick({R.id.saveBtn, R.id.motionBtn, R.id.screenBtn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.motionBtn:
                localUser.setControls(getString(R.string.motion));
                motionBtn.setBackground(getDrawable(R.color.red));
                screenBtn.setBackground(getDrawable(R.color.fui_transparent));
                break;
            case R.id.screenBtn:
                localUser.setControls(getString(R.string.screen));
                screenBtn.setBackground(getDrawable(R.color.red));
                motionBtn.setBackground(getDrawable(R.color.fui_transparent));
                break;
            case R.id.saveBtn:
                saveSettings();
                break;
        }
    }

    private void saveSettings() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        if (localUser != null) {
            if (!userName.getText().toString().isEmpty())
                localUser.setName(userName.getText().toString());
            intent.putExtra(getString(R.string.localUser), localUser);
        }
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    @OnTextChanged(R.id.userName)
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    @OnTextChanged(R.id.userName)
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    @OnTextChanged(R.id.userName)
    public void afterTextChanged(Editable s) {
        if (userName.getText().toString().isEmpty() && getInstance().getCurrentUser() != null) {
            Toast.makeText(getApplicationContext(), "please fill name", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBarProcess.setText(String.valueOf(progress));
        localUser.setVibrationNumber(vibrateSeekBar.getProgress());
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed() {
        saveSettings();
    }
}
