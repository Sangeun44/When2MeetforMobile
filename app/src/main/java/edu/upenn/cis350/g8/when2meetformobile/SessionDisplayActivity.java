package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SessionDisplayActivity extends AppCompatActivity {
    public static final int EnterTimesActivity_ID = 8;
    public static final int AddMoreUsersActivity_ID = 9;
    public static final int AddTimesActivity_ID = 10;
    public static final int ViewUserActivity_ID = 11;
    public static final int RemoveUsersActivity_ID = 12;

    private static final String TAG = "When2MeetSessDisp";

    private int waiting;

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
     * Navigate to the enter times page view
     * @param v current {@code View}
     */
    public void onEnterTimesButtonClick(View v) {
        Intent i = new Intent(this, EnterTimesActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountName", userID);
        startActivityForResult(i, EnterTimesActivity_ID);
    }

    /**
     * Navigate to the add times page view
     * @param v current {@code View}
     */
    public void onAddTimesButtonClick(View v) {
        Intent i = new Intent(this, AddTimesActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountKey", userID);
        startActivityForResult(i, AddTimesActivity_ID);
    }

    /**
     * Navigate to the remove users page view
     * @param v current {@code View}
     */
    public void onRemoveUsersButtonClick(View v) {
        Intent i = new Intent(this, RemoveUsersActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountKey", userID);
        startActivityForResult(i, RemoveUsersActivity_ID);
    }

    /**
     * Read the data from a meeting, loading any parsed data into {@code meeting}.
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
                        waiting = meeting.getNumUsers();
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

    /**
     * Get the user's name and update the map and UI for them
     * @param user_ID the user's ID
     */
    private void getUserName(final String user_ID) {
        Log.d(TAG, "user id " + user_ID);
        database.collection("users").document(user_ID).get()
            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshots) {
                    if (documentSnapshots.exists()) {
                        String name = documentSnapshots.get("userName").toString();
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
     * Navigate to the add users page view
     * @param view current {@code View}
     */
    public void addUserButton(View view) {
        Intent i = new Intent(this, AddMoreUsersActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountName", userID);
        startActivityForResult(i, AddMoreUsersActivity_ID);
    }

    /**
     * Navigate to the view users page view
     * @param view current {@code View}
     */
    public void viewUsersButton(View view) {
        Intent i = new Intent(this, ViewUserActivity.class);
        i.putExtra("MEETING", meetingID);
        i.putExtra("accountName", userID);
        startActivityForResult(i, ViewUserActivity_ID);
    }


    /**
     * update the UI with the people in the meeting read from the database
     */
    public void updateUIPeople() {
        Map<String, InternalUser> users = meeting.getUsers();

        TextView txtNumPeople = findViewById(R.id.txtNumPeople);
        int numUsers = users.size();
        txtNumPeople.setText(numUsers + " people in this group.");

        TextView txtPeople = findViewById(R.id.txtPeople);
        String peopleList = "";
        int counter = 1;

        for (String id : users.keySet()) {
            InternalUser u = users.get(id);
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
    public void updateUI(Map<String, InternalUser> users, Map<Integer, HashSet<String>> allTimes) {

        for (String id : users.keySet()) {
            getUserName(id);
        }

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

    /**
     * Finds the best time for all participants to meet.
     *
     * @return datetime string
     */
    private String genBestTimeStr() {
        Map<Integer, HashSet<String>> times = meeting.getBestTimes();
        for (int i = meeting.getNumUsers(); i >= 0; i--) {
            if (times.get(i) != null) {
                return times.get(i).iterator().next();
            }
        }
        return "No Time";
    }

    /**
     * Decrement the number of notifications still needing to be added. If all notifications
     * have been added to the database, then remove the meeting and return to the homepage.
     */
    private void decWaiting() {
        waiting--;
        if (waiting == 0) {
            FirebaseFirestore.getInstance().collection("meetings").document(meetingID).delete();
            Intent data = new Intent();
            data.putExtra("deleted",true);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    /**
     * Selects the best meeting time, removing the session from the database
     * and notifying all participants of the selection on their home screens.
     *
     * @param v end meeting button
     */
    public void pickTimeEndMeeting(View v) {
        Set<String> users = meeting.getUsers().keySet();
        final String bestTimeStr = meeting.getName() + ": " + genBestTimeStr() + ":00";
        for (final String user : users) {
            FirebaseFirestore.getInstance().collection("notifs").document(user).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        ArrayList<String> strs;
                        if (!documentSnapshot.exists()) {
                            strs = new ArrayList<>();
                        } else {
                            strs = (ArrayList<String>) documentSnapshot.get("notifs");
                        }
                        if (strs == null) {
                            strs = new ArrayList<>();
                        }
                        strs.add(bestTimeStr);
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("notifs", strs);
                        FirebaseFirestore.getInstance().collection("notifs").document(user)
                            .set(userData, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    decWaiting();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
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

}
