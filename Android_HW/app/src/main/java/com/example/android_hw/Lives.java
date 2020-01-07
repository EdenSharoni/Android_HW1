package com.example.android_hw;

import android.content.Context;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageView;

public class Lives extends AppCompatImageView {

    public Lives(Context context) {
        super(context);
        this.setImageResource(R.drawable.heart);
        this.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }
}