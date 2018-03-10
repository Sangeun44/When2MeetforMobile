package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evie on 3/9/18.
 */

public class EnterTimesActivity extends AppCompatActivity {
    public static final int EnterTimesActivity_ID = 3917;
    private static final String TAG = "When2MeetJoinedSessions";
    private List<Meeting> meetings;
    private String currentSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: need to make sure we pass this from joinedSessionDisplayActivity
        int userID = getIntent().getIntExtra("accountName", 0);
        populateMap(userID);
        currentSession = getIntent().getStringExtra("MEETING");

        //TODO: Make a view that dynamically loads the correct spinners based on high, low, dates
        setContentView(R.layout.activity_enter_times);
        loadDates();
        loadSpinners();

        //TODO: Implement plus button functionality to add more times

        //TODO: Implement a way to save this information into a reasonable format
    }

    private void populateMap(int ownerID) {
        FirebaseFirestore.getInstance().collection("meetings").whereEqualTo("owner", ownerID).get()
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
                List<String> days = m.getDates();
                for (String day : days) {
                    // from Stack Exchange
                    LinearLayout myLayout = findViewById(R.id.datesBar);
                    TextView date = new TextView(this);
                    date.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
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
                    Spinner timesSelector = new Spinner(this);
                    //adapted from Stack Exchange
                    //TODO: Figure out the format of hi/lo times

                    //set the time interval array for all the spinners
                    List<String> spinnerArray =  new ArrayList<>();

                    for (int j = 0; j < m.getHigh_time() - m.getLow_time(); j+=100) {
                        StringBuilder time = new StringBuilder();
                        time.append(m.getLow_time() + j);
                        spinnerArray.add(time.toString());
                    }

                    StringBuilder t = new StringBuilder();
                    t.append(m.getHigh_time());
                    spinnerArray.add(t.toString());

                    //display the spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            this, android.R.layout.simple_spinner_item, spinnerArray);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    timesSelector.setAdapter(adapter);

                    timesSelector.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));
                    myLayout.addView(timesSelector);
                }
            }
        }
    }

    public void loadPlus() {
        //finds the meeting that was clicked on, and loads the dates
        for (Meeting m : meetings) {
            if (m.getName().equals(currentSession)) {
                List<String> days = m.getDates();
                for (int k = 0; k < m.getDates().size(); k++) {
                    // from Stack Exchange
                    LinearLayout myLayout = findViewById(R.id.plusBar);
                    final ImageButton plus = new ImageButton(this);
                    plus.setImageResource(R.drawable.plus);
                    plus.setId(k);
                    //add more selectors when pressed
                    plus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //TODO: Add new selector in the right position
                            addSelector(plus.getId());
                        }
                    });

                    plus.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT));

                    myLayout.addView(plus);
                }
            }
        }
    }

    public void addSelector(int ID) {
        //TODO: Implement this
    }

}
