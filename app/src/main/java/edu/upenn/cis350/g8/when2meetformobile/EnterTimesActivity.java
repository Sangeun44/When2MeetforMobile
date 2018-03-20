package edu.upenn.cis350.g8.when2meetformobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
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
    private List<Meeting> meetings;
    private String currentSession;
    private String userId = "";
    List<String> days;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userId = getIntent().getStringExtra("accountName");
        getMeetings();
        currentSession = getIntent().getStringExtra("MEETING");
        setContentView(R.layout.activity_enter_times);
        loadDates();
        loadSpinners();
    }

    private void getMeetings() {
        FirebaseFirestore.getInstance().collection("meetings").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                        } else {
                            meetings = documentSnapshots.toObjects(Meeting.class);
                            Log.d(TAG, "onSuccess: Found " + meetings.size() + " meetings!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void loadDates () {
        //finds the meeting that was clicked on, and loads the dates
        for (Meeting m : meetings) {
            if (m.getName().equals(currentSession)) {
                days = m.getDates();
                for (String day : days) {
                    // from Stack Exchange
                    LinearLayout myLayout = findViewById(R.id.datesBar);
                    TextView date = new TextView(this);
                    date.setText(day);
                    date.setLayoutParams(new LinearLayout.LayoutParams(
                            205,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    myLayout.addView(date);
                }
            }
        }
    }

    public void loadSpinners() {
        //finds the meeting clicked on, and loads selectors based on hi/lo times
        for (Meeting m : meetings) {
            if (m.getName().equals(currentSession)) {
                int numOptions = m.getDates().size();

                for (int i = 0; i < numOptions; i++) {
                    LinearLayout myLayout = findViewById(R.id.selectorBar);
                    //layout for new selector/plus button pair
                    LinearLayout child = new LinearLayout(this);
                    child.setOrientation(LinearLayout.VERTICAL);
                    child.setLayoutParams(new LinearLayout.LayoutParams(
                            200,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    child.setId(i);

                    //create spinner with times
                    //set the time interval array for all the spinners
                    List<String> spinnerArray = new ArrayList<>();

                    for (int j = 0; j < m.getHigh_time() - m.getLow_time(); j+=1) {
                        StringBuilder time = new StringBuilder();
                        time.append(m.getLow_time() + j);
                        spinnerArray.add(time.toString());
                    }

                    StringBuilder t = new StringBuilder();
                    t.append(m.getHigh_time());
                    spinnerArray.add(t.toString());

                    Spinner start = createSelector(spinnerArray, "START");
                    //set the time interval array for all the spinners

                    List<String> spinnerArr = new ArrayList<>();

                    for (int j = 0; j < m.getHigh_time() - m.getLow_time(); j+=1) {
                        StringBuilder time = new StringBuilder();
                        time.append(m.getLow_time() + j);
                        spinnerArr.add(time.toString());
                    }

                    StringBuilder r = new StringBuilder();
                    r.append(m.getHigh_time());
                    spinnerArr.add(r.toString());

                    Spinner end = createSelector(spinnerArray, "END");

                    child.addView(start);
                    child.addView(end);

                    //checkbox
                    CheckBox preferred = new CheckBox(this);
                    LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(150, 150);
                    layoutParams.gravity= Gravity.CENTER;
                    preferred.setLayoutParams(layoutParams);
                    child.addView(preferred);

                    ImageButton btn = createPlus(i);
                    child.addView(btn);
                    myLayout.addView(child);
                }
            }
        }
    }

    private Spinner createSelector(List<String> values, String which) {
        Spinner timesSelector = new Spinner(this);
        values.add(0, which);
        //display the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item, values);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timesSelector.setAdapter(adapter);

        timesSelector.setLayoutParams(new LinearLayout.LayoutParams(
                400,
                200));
        return timesSelector;
    }

    public ImageButton createPlus(int ID) {
        //finds the meeting that was clicked on, and loads the dates
        final ImageButton plus = new ImageButton(this);
        plus.setImageResource(R.drawable.plus);
        plus.setId(ID*100);
        //add more selectors when pressed
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                @SuppressLint("ResourceType") int ident = plus.getId() / 100;
                addSelector(ident);
            }
        });

        plus.setLayoutParams(new LinearLayout.LayoutParams(
                200,
                200));

        return plus;
    }

    private void addSelector(int ID) {
        LinearLayout myLayout = findViewById(ID);

        //create the selector
        for (Meeting m : meetings) {
            if (m.getName().equals(currentSession)) {

                List<String> spinnerArray = new ArrayList<>();

                for (int j = 0; j < m.getHigh_time() - m.getLow_time(); j+=1) {
                    StringBuilder time = new StringBuilder();
                    time.append(m.getLow_time() + j);
                    spinnerArray.add(time.toString());
                }

                StringBuilder t = new StringBuilder();
                t.append(m.getHigh_time());
                spinnerArray.add(t.toString());

                Spinner start = createSelector(spinnerArray, "START");
                //set the time interval array for all the spinners

                List<String> spinnerArr = new ArrayList<>();

                for (int j = 0; j < m.getHigh_time() - m.getLow_time(); j+=1) {
                    StringBuilder time = new StringBuilder();
                    time.append(m.getLow_time() + j);
                    spinnerArr.add(time.toString());
                }

                StringBuilder r = new StringBuilder();
                r.append(m.getHigh_time());
                spinnerArr.add(r.toString());

                Spinner end = createSelector(spinnerArray, "END");

                myLayout.addView(start, myLayout.getChildCount() - 1);
                myLayout.addView(end, myLayout.getChildCount() - 1);

                //checkbox
                CheckBox preferred = new CheckBox(this);
                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(150, 150);
                layoutParams.gravity= Gravity.CENTER;
                preferred.setLayoutParams(layoutParams);
                myLayout.addView(preferred, myLayout.getChildCount() - 1);
                }
        }

    }

    public void onEnterClick(View view) {
        String startTime, endTime;
        LinearLayout selectorBar = findViewById(R.id.selectorBar);
        for (int i = 0; i < selectorBar.getChildCount(); i++) {
            List<String> enteredTimes = new ArrayList<String>();
            LinearLayout column = (LinearLayout) selectorBar.getChildAt(i);
            for (int j = 0 ; j < column.getChildCount() - 2; j+=3) {
                if (column.getChildAt(j) instanceof Spinner) {
                    Spinner start = (Spinner) column.getChildAt(j);
                    if (column.getChildAt(j + 1) instanceof Spinner) {
                        Spinner end = (Spinner) column.getChildAt(j);
                        startTime = start.getSelectedItem().toString();
                        endTime = end.getSelectedItem().toString();
                        if (column.getChildAt(j + 2) instanceof CheckBox) {
                            //TODO: Use this data to implement preferred times functionality
                            CheckBox check = (CheckBox) column.getChildAt(j + 2);
                            if (check.isChecked()) { } else { }
                        }

                        enteredTimes.add(startTime);
                        enteredTimes.add(endTime);

                        if (startTime.compareTo(endTime) > 0) {
                            Toast.makeText(this, "Start times can not be greater" +
                                            " than end times. Please fix it and resubmit.",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        } else if (startTime.equals("START") ||
                                endTime.equals("END")) {
                            Toast.makeText(this, "Please fill in available times" +
                                            " before submitting.",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                }
            }
            Map<String, List<String>> values = new HashMap<>();
            values.put(days.get(i), enteredTimes);
            updateDB(values);
        }
        finish();
    }

    private void updateDB(Map<String, List<String>> data) {
        FirebaseFirestore.getInstance().collection("meetings").document(currentSession)
                .collection("users").document(userId)
                .set(data, SetOptions.merge())
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