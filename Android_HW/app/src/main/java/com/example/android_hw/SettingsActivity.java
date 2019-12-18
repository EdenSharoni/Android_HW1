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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.OnTextChanged;

import static com.google.firebase.auth.FirebaseAuth.getInstance;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, TextWatcher {
    private static final String TAG = SettingsActivity.class.getSimpleName();
    private CheckBox vibrate;
    private CheckBox music;
    private ImageView backBtn;
    private boolean vibrateBoolean;
    private boolean musicBoolean;
    private ImageView signOutBtn;
    private FirebaseUser user;
    private EditText userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle bundle = getIntent().getExtras();
        userName = findViewById(R.id.userName);
        userName.addTextChangedListener(this);
        if (bundle != null) {
            vibrateBoolean = bundle.getBoolean(String.valueOf(R.string.vibrate));
            musicBoolean = bundle.getBoolean(String.valueOf(R.string.music));
            userName.setText(bundle.getString("name"));
        } else {
            vibrateBoolean = true;
            musicBoolean = true;
        }
        vibrate = findViewById(R.id.vibrateCheckbox);
        music = findViewById(R.id.musicCheckbox);
        backBtn = findViewById(R.id.backBtn);
        signOutBtn = findViewById(R.id.signOutBtn);

        if (!vibrateBoolean) {
            vibrate.setChecked(false);
        }
        if (!musicBoolean) {
            music.setChecked(false);
        }

        backBtn.setOnClickListener(this);
        vibrate.setOnCheckedChangeListener(this);
        music.setOnCheckedChangeListener(this);
        signOutBtn.setOnClickListener(this);
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
        switch (v.getId()) {
            case R.id.backBtn:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(String.valueOf(R.string.vibrate), vibrateBoolean);
                intent.putExtra(String.valueOf(R.string.music), musicBoolean);
                intent.putExtra("name", userName.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.signOutBtn:
                signOut();
                break;
        }
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();
        signOutBtn.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        user = getInstance().getCurrentUser();
        if (user == null) {
            signOutBtn.setVisibility(View.GONE);
        } else {
            signOutBtn.setVisibility(View.VISIBLE);
        }
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
}
