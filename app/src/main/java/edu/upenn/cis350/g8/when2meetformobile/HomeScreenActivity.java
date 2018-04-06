package edu.upenn.cis350.g8.when2meetformobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class HomeScreenActivity extends AppCompatActivity {
    static final int joinwithcode_ID = 1;  // The request code for joining with a code
    public static final int SessionActivity_ID = 3;
    public static final int CreateSessionActivity_ID = 4;
    final String TAG = "FireBase";

    private List<Meeting> myMeetings = new ArrayList<>();
    private Meeting curr_meet = new Meeting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        loadNotifs();
    }

    /**
     * Displays no notifications.
     *
     * @param layout linear layout to set
     */
    private void setEmptyNotif(LinearLayout layout) {
        TextView tv = new TextView(this);
        tv.setText("None!");
        layout.addView(tv);
    }

    /**
     * Load notifications for completed meetings.
     */
    private void loadNotifs() {
        Intent i = getIntent();
        final String user_id = i.getStringExtra("accountKey");
        final AppCompatActivity context = this;
        FirebaseFirestore.getInstance().collection("notifs").document(user_id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        LinearLayout layout = findViewById(R.id.notifications);
                        if (documentSnapshot.exists()) {
                            try {
                                // get notification strings from db
                                ArrayList<String> strs =
                                        (ArrayList<String>) documentSnapshot.get("notifs");
                                // put notifications on screen
                                for (String str : strs) {
                                    TextView tv = new TextView(context);
                                    tv.setText(str);
                                    layout.addView(tv);
                                }
                                if (strs.isEmpty()) {
                                    setEmptyNotif(layout);
                                }
                            } catch (Exception e) {
                                setEmptyNotif(layout);
                            }
                        } else {
                            setEmptyNotif(layout);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // do nothing
                    }
                });
    }

    /**
     * Runs the activity to view meetings.
     *
     * @param isOwner true if viewing owned meetings, false otherwise
     */
    private void viewMeetings(boolean isOwner) {
        Intent i = getIntent();
        String user_id = i.getStringExtra("accountKey");

        Intent i2 = new Intent(this, SessionsActivity.class);
        i2.putExtra("isOwner", isOwner);
        i2.putExtra("accountKey", user_id);

        startActivityForResult(i2, SessionActivity_ID);
    }

    /**
     * Runs the activity to view owned meetings.
     *
     * @param view current {@code View}
     */
    public void onMySessionsButtonClick(View view) {
        viewMeetings(true);
    }

    /**
     * Runs the activity to view unowned meetings.
     *
     * @param view current {@code View}
     */
    public void onJoinedSessionsButtonClick(View view) {
        viewMeetings(false);
    }

    /**
     * Logs the current user out of the platform.
     *
     * @param view current {@code View}
     */
    public void onLogoutButtonClick(View view) {
        Intent data = new Intent();
        data.putExtra("logout",true);
        setResult(RESULT_OK, data);
        finish();
    }

    /**
     * Runs the activity to create a new meeting.
     *
     * @param view current {@code View}
     */
    public void onCreateButtonClick(View view) {
        Intent intent = getIntent();
        String user_id = intent.getStringExtra("accountKey");
        Log.d(TAG, user_id);

        Intent i = new Intent(this, CreateSessionActivity.class);
        i.putExtra("accountKey", user_id);
        startActivityForResult(i, CreateSessionActivity_ID);
    }

    /**
     * Runs the activity to join a new meeting.
     *
     * @param view current {@code View}
     */
    public void onJoinButtonClick(View view) {
        Intent intent = getIntent();
        int user_id = intent.getIntExtra("code", 0);

        Intent i = new Intent(this, JoinWithCodeActivity.class);
        i.putExtra("code", user_id);
        startActivityForResult(i, joinwithcode_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case (joinwithcode_ID) : {
                if (resultCode == Activity.RESULT_OK) {
                    String returnCode = data.getStringExtra("code");
                    checkForCode(returnCode);
                }
                break;
            }
        }
    }

    /**
     * Attempts to add the current user to the specified meeting via a code.
     *
     * @param meeting_ID ID of meeting to update
     */
    private void checkForCode(String meeting_ID) {
        final String meetingName = meeting_ID;
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference usersRef = database.collection("meetings").document(meetingName);
        usersRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Meeting exists");
                    updateDB(meetingName);
                } else {
                    Toast.makeText(HomeScreenActivity.this,
                            "Meeting is not available", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Meeting does not exist");
                }
            }
        });
    }

    /**
     * Updates the meeting in the database, merging the new user data with the old.
     *
     * @param meeting_ID ID of the meeting to update in the database
     */
    private void updateDB(String meeting_ID) {
        Intent he = getIntent();
        String account_id = he.getStringExtra("accountKey");

        // get the meeting in the database
        FirebaseFirestore.getInstance().collection("meetings").document(meeting_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (!documentSnapshots.exists()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                        } else {
                            curr_meet = documentSnapshots.toObject(Meeting.class);
                            int numMeetings =  myMeetings.size();
                            Log.d(TAG,"onSuccess: Found " + numMeetings + " meetings!");
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

        // add users
        curr_meet.addUsers(account_id);

        // add back to database
        FirebaseFirestore.getInstance().collection("meetings").document(meeting_ID)
                .set(curr_meet, SetOptions.merge())
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

        Toast.makeText(HomeScreenActivity.this,
                "Added the meeting to your joined sessions", Toast.LENGTH_LONG).show();
    }
}
