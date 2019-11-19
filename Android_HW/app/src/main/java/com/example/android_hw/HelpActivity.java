package com.example.android_hw;

import android.content.Context;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class HelpActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView back;
    private Vibrator vibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        back = findViewById(R.id.backBtn);
        back.setOnClickListener(this);
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onClick(View v) {
        vibe.vibrate(20);
        finish();
    }
}
