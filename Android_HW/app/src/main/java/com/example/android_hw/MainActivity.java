package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    @OnClick({R.id.playBtn, R.id.helpBtn, R.id.exitBtn})
    public void onClick(View v) {
        ((Vibrator) getSystemService(Context.VIBRATOR_SERVICE)).vibrate(20);
        switch (v.getId()) {
            case R.id.playBtn:
                Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.helpBtn:
                intent = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(intent);
                break;
            case R.id.exitBtn:
                finish();
                break;
        }
    }
}
