package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class SessionDisplayActivity extends AppCompatActivity {

    private static final String TAG = "When2MeetSessDisp";
    private Meeting meeting;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_display);
        Intent i = this.getIntent();
        String meetingName = i.getStringExtra("MEETING");
        //readSessionData(meetingName);
        Map<String, User> users = new HashMap<String, User>();
        users.put("1", new User("Sang"));
        users.put("2", new User("Diana"));
        users.put("3", new User("Saniyah"));
        users.put("3", new User("Evie"));
        Map<Integer, HashSet<String>> allTimes = new HashMap<Integer, HashSet<String>>();
        HashSet<String> everyone = new HashSet<String>();
        everyone.add("2018/02/20 17");
        allTimes.put(4, everyone);
        HashSet<String> most = new HashSet<String>();
        most.add("2018/02/21 12");
        most.add("2018/02/22 18");
        allTimes.put(3, most);
        updateUI(users, allTimes);

        // sets visibility of special owner buttons based on mode
        type = i.getStringExtra("display");
        HorizontalScrollView scrollOwner = findViewById(R.id.scrollOwner);
        if (type.equals("joined")) {
            scrollOwner.setVisibility(View.INVISIBLE);
        }
        if (type.equals("created")) {
            scrollOwner.setVisibility(View.VISIBLE);
        }
    }

    /**
     * returns to previous screen on back
     * @param v current View
     */
    public void onBackButtonClick(View v) {
        Intent i = new Intent(this, SessionsActivity.class);
        setResult(RESULT_OK, i);
        finish();
    }

    /**
     * Goes to the EnterTimes Activity
     * @param v current View
     */
    public void onEnterTimesButtonClick(View v) {
        Toast.makeText(getApplicationContext(), "Going to Enter Times Page...",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * reads the data for this meeting based on the meetingName
     * if successful, meeting will hold the read data
     * @param meetingName the Name of the Meeting to read
     */
    private void readSessionData(String meetingName) {
        FirebaseFirestore.getInstance().collection("meetings")
                .whereEqualTo("name", meetingName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: No such meeting");
                        } else {
                            meeting = documentSnapshots.toObjects(Meeting.class).get(0);
                            Log.d(TAG, "onSuccess: Found your meeting!");
                            updateUI(meeting.getUsers(), meeting.getBestTimes());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error getting meeting data!!!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Updates the UI to reflect the data from the associated Meeting
     * @param users the map of users in this meeting
     * @param allTimes all the possible meeting times
     */
    public void updateUI(Map<String, User> users, Map<Integer, HashSet<String>> allTimes) {
            TextView txtPeople = findViewById(R.id.txtPeople);
            String people = "Respondents:";
            int counter = 1;
            for (User u : users.values()) {
                if (u.enteredTimes()) {
                    String name = u.getName();
                    people += "\n" + counter +  ". " + name;
                    counter++;
                }
            }
            txtPeople.setText(people);

            TextView txtNumPeople = findViewById(R.id.txtNumPeople);
            int numUsers = users.size();
            txtNumPeople.setText(numUsers + " people in this group.");

            TextView txtBestTimes = findViewById(R.id.txtBestTimes);
            String bestTimes = "Best Times To Meet: \n\n";

            for (int i = numUsers; i > numUsers / 2; i--) {
                if (allTimes.containsKey(i)) {
                    String bestTime = (double) i * 100 / numUsers + "% Free:\n";
                    for (String time : allTimes.get(i)) {
                        bestTime += time + ":00 \n";
                    }
                    bestTimes += bestTime + "\n";
                }
            }

            txtBestTimes.setText(bestTimes);
        }
}
