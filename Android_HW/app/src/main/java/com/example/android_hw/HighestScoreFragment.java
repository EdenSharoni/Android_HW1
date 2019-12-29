package com.example.android_hw;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HighestScoreFragment extends DialogFragment {
    private final static String TAG = HighestScoreFragment.class.getSimpleName();
    private User localUser;
    private FirebaseFirestore db;
    private List<DocumentSnapshot> myListOfDocuments;
    private int numberOfUsers;
    private HashMap<User, Integer> users = new HashMap<>();
    private Map<User, Integer> unSortMap = new HashMap<User, Integer>();
    private ArrayList<User> localUserArrayList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            myListOfDocuments = task.getResult().getDocuments();

                            for (int i = 0; i < myListOfDocuments.size(); i++) {
                                unSortMap.put(myListOfDocuments.get(i).toObject(User.class), myListOfDocuments.get(i).toObject(User.class).getScore());
                            }

                            List<Map.Entry<User, Integer>> list = new LinkedList<Map.Entry<User, Integer>>(unSortMap.entrySet());

                            Collections.sort(list, new Comparator<Map.Entry<User, Integer>>() {
                                @Override
                                public int compare(Map.Entry<User, Integer> o1, Map.Entry<User, Integer> o2) {
                                    return o2.getValue().compareTo(o1.getValue());
                                }
                            });

                            for (Map.Entry<User, Integer> entry : list) {
                                localUserArrayList.add(entry.getKey());
                            }

                            if (localUserArrayList.size() > 10) {
                                numberOfUsers = 10;
                            } else {
                                numberOfUsers = localUserArrayList.size();
                            }
                            setXMLViewWithUsers();
                        }
                    }
                });

        return inflater.inflate(R.layout.fragment_highest_score, container, false);
    }

    public void setXMLViewWithUsers() {
        LinearLayout linearLayoutManager = new LinearLayout(getActivity());
        linearLayoutManager.setOrientation(LinearLayout.VERTICAL);
        linearLayoutManager.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        ImageView title = new ImageView(getActivity());
        title.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        title.setImageResource(R.drawable.highest_score_title);
        linearLayoutManager.addView(title);

        TableLayout tableLayout = new TableLayout(getActivity());
        tableLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TableLayout.LayoutParams tableParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        tableParams.weight = 1;
        tableParams.gravity = Gravity.CENTER_VERTICAL;

        TableRow.LayoutParams rowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        for (int i = 0; i < numberOfUsers; i++) {

            localUser = localUserArrayList.get(i);

            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(tableParams);

            ImageView player = new ImageView(getActivity());
            player.setLayoutParams(rowParams);
            player.setImageResource(R.drawable.player1);

            TextView name = new TextView(getActivity());
            name.setText(localUser.getName());
            name.setTextSize(20);

            TextView comma = new TextView(getActivity());
            comma.setText(", ");
            comma.setTextSize(20);

            TextView score = new TextView(getActivity());
            score.setText(String.valueOf(localUser.getScore()));
            score.setTextSize(20);

            tableRow.addView(player);
            tableRow.addView(name);
            tableRow.addView(comma);
            tableRow.addView(score);

            tableLayout.addView(tableRow);
        }

        linearLayoutManager.addView(tableLayout);

        ((LinearLayout) getView()).addView(linearLayoutManager);

    }
}
