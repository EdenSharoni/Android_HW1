package com.example.android_hw;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PopUpNameActivity extends AppCompatActivity implements View.OnClickListener {

    private Button done;
    private EditText name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_name);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * (0.7)), (int) (height * 0.4));
        done = findViewById(R.id.done);
        name = findViewById(R.id.name);
        done.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!name.getText().toString().isEmpty()) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("name", name.getText().toString());
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "please fill name", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
