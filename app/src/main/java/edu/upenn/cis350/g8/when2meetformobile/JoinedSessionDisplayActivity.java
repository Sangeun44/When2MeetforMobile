package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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


public class JoinedSessionDisplayActivity extends AppCompatActivity {

    private static final String TAG = "When2MeetJoinedSessDisp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_session_display);
        Intent i = this.getIntent();
        String meetingName = i.getStringExtra("MEETING");
        //readSessionData(meetingName);
//        List<User> users = new ArrayList<User>();
//        users.add(new User("Sang"));
//        users.add(new User("Diana"));
//        users.add(new User("Saniyah"));
//        users.add(new User("Evie"));
//        Map<Integer, HashSet<String>> allTimes = new HashMap<Integer, HashSet<String>>();
//        HashSet<String> everyone = new HashSet<String>();
//        everyone.add("2018/02/20 17");
//        allTimes.put(4, everyone);
//        HashSet<String> most = new HashSet<String>();
//        most.add("2018/02/21 12");
//        most.add("2018/02/22 18");
//        allTimes.put(3, most);
//        updateUI(users, allTimes);
    }

    public void onBackButtonClick(View v) {
        Intent i = new Intent(this, JoinedSessionsActivity.class);
        setResult(RESULT_OK, i);
        finish();
    }

    public void onEnterTimesButtonClick(View v) {
        Toast.makeText(getApplicationContext(), "Going to Enter Times Page...",
                Toast.LENGTH_SHORT).show();
    }

    private void readSessionData(String meetingName) {
        FirebaseFirestore.getInstance().collection("meetings").whereEqualTo("name", meetingName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: No such meeting");
                        } else {
                            Meeting m = documentSnapshots.toObjects(Meeting.class).get(0);
                            Log.d(TAG, "onSuccess: Found your meeting!");
                            updateUI(m.getUsers(), m.getBestTimes());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting meeting data!!!", Toast.LENGTH_LONG).show();
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
