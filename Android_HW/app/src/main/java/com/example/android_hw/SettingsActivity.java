package com.example.android_hw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
    private Button signOutBtn;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;

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
                setResult(Activity.RESULT_OK, intent);
                finish();
                break;
            case R.id.signOutBtn:
                signOut();
                break;
        }

    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        signOutBtn.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            signOutBtn.setVisibility(View.GONE);
        } else {
            signOutBtn.setVisibility(View.VISIBLE);
        }

    }
}
