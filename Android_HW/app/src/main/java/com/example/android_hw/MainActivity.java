package com.example.android_hw;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private ImageView play;
    private ImageView help;
    private ImageView exit;
    private Intent intent;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        play = findViewById(R.id.playBtn);
        help = findViewById(R.id.helpBtn);
        exit = findViewById(R.id.exitBtn);
        play.setOnClickListener(this);
        help.setOnClickListener(this);
        exit.setOnClickListener(this);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    @Override
    public void onClick(View v) {
        vibe.vibrate(20);
        switch (v.getId()) {
            case R.id.playBtn:
                intent = new Intent(getApplicationContext(), GameActivity.class);
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
