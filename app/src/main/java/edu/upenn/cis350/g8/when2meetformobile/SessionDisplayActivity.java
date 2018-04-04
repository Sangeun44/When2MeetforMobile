package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SessionDisplayActivity extends AppCompatActivity {
    public static final int EnterTimesActivity_ID = 8;
    public static final int AddMoreUsersActivity_ID = 9;
    public static final int AddTimesActivity_ID = 10;
    private static final String TAG = "When2MeetSessDisp";

    private Meeting meeting;
    private boolean isOwner;
    private String meetingID;
    private String userID;

    private HashMap<String, String> usersInName;

    private FirebaseFirestore database;

    private ListView ls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_display);
        Intent i = this.getIntent();
        usersInName = new HashMap<String, String>();
        ls = (ListView) findViewById(R.id.listBestTimes);
        meetingID = i.getStringExtra("MEETING"); //meeting_ID
        userID = i.getStringExtra("accountKey"); //owner_ID

        database = FirebaseFirestore.getInstance();
        Log.d(TAG, meetingID);
        readSessionData(meetingID);

        // sets visibility of special owner buttons based on mode
        isOwner = i.getBooleanExtra("isOwner", false);
        LinearLayout scrollOwner = findViewById(R.id.scrollOwner);
        scrollOwner.setVisibility(isOwner ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Return to previous screen.
     *
     * @param v current {@code View}
     */
    public void onBackButtonClick(View v) {
        Intent i = new Intent(this, SessionsActivity.class);
        setResult(RESULT_OK, i);
        finish();
    }

    /**
     * Navigate to the enter times page view.
     *
     * @param v current {@code View}
     */
    public void onEnterTimesButtonClick(View v) {
        Toast.makeText(getApplicationContext(), "Going to Enter Times Page...",
                Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, EnterTimesActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountName", userID);
        startActivityForResult(i, EnterTimesActivity_ID);
    }

    public void onAddTimesButtonClick(View v) {
        Toast.makeText(getApplicationContext(), "Going to Add Times Page...",
                Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, AddTimesActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountKey", userID);
        startActivityForResult(i, AddTimesActivity_ID);
    }

    /**
     * Read the data from a meeting, loading any parsed data into {@code meeting}.
     *
     * @param meetingID ID of the meeting to be read
     */
    private void readSessionData(String meetingID) {
        // get the meeting in the database
        database.collection("meetings").document(meetingID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            meeting = documentSnapshots.toObject(Meeting.class);
                            Log.d(TAG, "onSuccess: Found meeting!");
                            updateUI(meeting.getUsers(), meeting.getBestTimes());
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

    private void getUserName(final String user_ID) {
        Log.d(TAG, "user id " + user_ID);
        database.collection("users").document(user_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            String name = documentSnapshots.get("name").toString();
                            usersInName.put(user_ID, name);
                            updateUIPeople();
                            Log.d(TAG, "onSuccess: Found user name!" + user_ID + name);
                        } else {
                            Log.d(TAG, "onSuccess: No Such owner");
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

    /**
     * Add user to the session
     * Add More Users button, it should display a screen that
     * lists the code for the event
     * with an option to add more usernames/emails.
     */
    public void addUserButton(View view) {
        Toast.makeText(getApplicationContext(), "Going to Enter More Users Page...",
                Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, AddMoreUsersActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountName", userID);
        startActivityForResult(i, AddMoreUsersActivity_ID);
    }

    public void updateUIPeople() {
        Map<String, User> users = meeting.getUsers();

        TextView txtNumPeople = findViewById(R.id.txtNumPeople);
        int numUsers = users.size();
        txtNumPeople.setText(numUsers + " people in this group.");

        TextView txtPeople = findViewById(R.id.txtPeople);
        String peopleList = "";
        int counter = 1;

        for (String id : users.keySet()) {
            User u = users.get(id);
            if (u.enteredTimes()) {
                peopleList += "\n" + counter + ". " + usersInName.get(id);
                counter++;
            }
        }
        txtPeople.setText(peopleList);
    }

    /**
     * Update the UI to reflect the data loaded into {@code meeting}.
     */
    public void updateUI(Map<String, User> users, Map<Integer, HashSet<String>> allTimes) {

        for (String id : users.keySet()) {
            getUserName(id);
        }

        String bestTimes = "";

        int numUsers = users.size();
        List<String> list_times = new ArrayList<String>();

        Log.d(TAG, "user size: " + users.size());

        for (int i = numUsers; i > numUsers / 2; i--) {
            if (allTimes.containsKey(i)) {
                String bestTime = (double) i * 100 / numUsers + "% Free:";
                list_times.add(bestTime);
                for (String time : allTimes.get(i)) {
                    String right = time + ":00";
                    list_times.add(right);
                    Log.d(TAG, right);
                }
            }
        }

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, list_times);
        ls.setAdapter(arrayAdapter);
    }


}
