package com.example.android_hw;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class HighestScoreFragment extends DialogFragment {
    private final static String TAG = HighestScoreFragment.class.getSimpleName();
    private LinearLayout.LayoutParams params;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_highest_score, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        LinearLayout linearLayoutManager = new LinearLayout(getActivity());
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        linearLayoutManager.setLayoutParams(params);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        ImageView title = new ImageView(getActivity());
        title.setLayoutParams(params);
        title.setImageResource(R.drawable.highest_score_title);
        linearLayoutManager.addView(title);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        params.weight = 1;

        TableLayout tableLayout = new TableLayout(getActivity());
        tableLayout.setLayoutParams(params);

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        tableParams.weight = 1;
        tableParams.gravity = Gravity.CENTER_VERTICAL;

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < 10; i++) {
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(tableParams);
            Log.e(TAG, "onResume: ");
            ImageView imageView = new ImageView(getActivity());
            imageView.setLayoutParams(rowParams);
            imageView.setImageResource(R.drawable.player1);

            TextView textView1 = new TextView(getActivity());
            textView1.setText("Eden Sharoni");
            textView1.setTextSize(20);

            TextView textView2 = new TextView(getActivity());
            textView2.setText("50");
            textView2.setTextSize(20);

            tableRow.addView(imageView);
            tableRow.addView(textView1);
            tableRow.addView(textView2);

            tableLayout.addView(tableRow);
        }

        linearLayoutManager.addView(tableLayout);


        ((LinearLayout) getView()).addView(linearLayoutManager);
    }
}
