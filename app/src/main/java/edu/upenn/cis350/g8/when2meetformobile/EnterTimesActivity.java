package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Evie on 3/9/18.
 */

public class EnterTimesActivity extends AppCompatActivity {
    public static final int EnterTimesActivity_ID = 3917;
    private static final String TAG = "When2MeetJoinedSessions";
    private Meeting meeting;
    private String currentSession;
    private String userId = "";
    List<String> days;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        userId = i.getStringExtra("accountName");
        currentSession = i.getStringExtra("MEETING");
        getMeetings();
        setContentView(R.layout.activity_enter_times);
    }

    private void getMeetings() {
        // get the meeting in the database
        FirebaseFirestore.getInstance().collection("meetings").document(currentSession).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            meeting = documentSnapshots.toObject(Meeting.class);
                            Log.d(TAG,"onSuccess: Found meeting!");
                            loadChoice();
                        } else {
                            Log.d(TAG, "onSuccess: No Such meeting");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting data!!!",
                                Toast.LENGTH_LONG).show();
                    }
                });

    }



    public void loadChoice() {
        //finds the meeting clicked on, and loads selectors based on hi/lo times

        //enables horizontal scrolling
        HorizontalScrollView hView = new HorizontalScrollView(this);
        LinearLayout myLayout = findViewById(R.id.selectorBar);
        hView.addView(myLayout);

        days = meeting.getDates();
        int i = 0;
        for (String day : days) {

            //layout for new selector/plus button pair
            LinearLayout child = new LinearLayout(this);
            ScrollView sView  = new ScrollView(this);

            //gets the date and adds it to the ScrollView
            TextView weekDay = new TextView(this);
            weekDay.setText(day);
            sView.addView(weekDay);

            child.setOrientation(LinearLayout.HORIZONTAL);
            child.setLayoutParams(new LinearLayout.LayoutParams(
                    200,
                    LinearLayout.LayoutParams.MATCH_PARENT));
            child.setId(i);


            for (int j = 0; j < meeting.getHigh_time() + 1 - meeting.getLow_time(); j += 1) {
                //time + checkbox
                StringBuilder time = new StringBuilder();
                time.append(meeting.getLow_time() + j);

                TextView timeView = new TextView(this);
                timeView.setText(time.toString());
                child.addView(timeView);

                CheckBox select = new CheckBox(this);
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(150, 150);
                //layoutParams.gravity = Gravity.CENTER;
                select.setLayoutParams(layoutParams);
                child.addView(select);

                sView.addView(child);

            }
            myLayout.addView(sView);
            i+=1;
        }
    }


    public void onEnterClick(View view) {
        LinearLayout selectorBar = findViewById(R.id.selectorBar);
        ArrayList<String> enteredTimes = new ArrayList<>();
        for (int i = 0; i < selectorBar.getChildCount(); i++) {
            ScrollView sView = (ScrollView) selectorBar.getChildAt(i);
            for (int j = 0; j < sView.getChildCount(); j++) {
                TextView time = (TextView) sView.getChildAt(0);
                CheckBox selected = (CheckBox) sView.getChildAt(1);
                if (selected.isChecked()) {
                    enteredTimes.add(time.toString());
                }
            }
        }
        if (enteredTimes.isEmpty()) {
            Log.d(TAG, "Empty!!");
        }
        User users = new User(userId, enteredTimes);
        meeting.addUsers(userId, users);
        updateDB(meeting);
        finish();
    }

    private void updateDB(Meeting meet) {
        //add back to database
        FirebaseFirestore.getInstance().collection("meetings").document(currentSession)
                .set(meet, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}
