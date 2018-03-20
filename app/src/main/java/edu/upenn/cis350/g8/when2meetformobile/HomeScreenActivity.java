package edu.upenn.cis350.g8.when2meetformobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Evie on 2/18/18.
 */

public class HomeScreenActivity extends AppCompatActivity {
    private List<Meeting> myMeetings = new ArrayList<Meeting>();
    private Meeting meet;
    private Meeting curr_meet = new Meeting();

    public static final int SessionActivity_ID = 3;
    public static final int CreateSessionActivity_ID = 4;
    final String TAG = "FireBase";
    static final int joinwithcode_ID = 1;  // The request code for joining with a code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void updateNotifications (String message) {
        TextView text = findViewById(R.id.notifications);
        text.append(message);
    }

    public void onMySessionsButtonClick(View view) {

        Intent i = getIntent();
        String user_id = i.getStringExtra("accountKey");

        Intent i2 = new Intent(this, SessionsActivity.class);
        i2.putExtra("type", "created");
        i2.putExtra("accountKey", user_id);

        startActivityForResult(i2, SessionActivity_ID);
    }

    public void onJoinedSessionsButtonClick(View view) {
        Intent i = getIntent();
        String user_id = i.getStringExtra("accountKey");

        Intent i2 = new Intent(this, SessionsActivity.class);
        i2.putExtra("type", "joined");
        i2.putExtra("accountKey", user_id);

        startActivityForResult(i2, SessionActivity_ID);
    }

    public void onLogoutButtonClick(View view) {
        //return to login page
        finish();
    }

    public void onCreateButtonClick(View view) {
        //Sang's page
        Intent intent = getIntent();
        String user_id = intent.getStringExtra("accountKey");
        Log.d(TAG, user_id);
        //CreateSessionActivity
        Intent i = new Intent(this, CreateSessionActivity.class);
        i.putExtra("accountKey", user_id);
        startActivityForResult(i, CreateSessionActivity_ID);
    }

    public void onProfileButtonClick(View view) {

    }

    //Sang iteration #2
    //join with a code calls code entering activity
    public void onJoinButtonClick(View view) {
        Intent intent = getIntent();
        int user_id = intent.getIntExtra("code", 0);
        //joinSessionActivity
        Intent i = new Intent(this, JoinWithCodeActivity.class);
        i.putExtra("code", user_id);
        startActivityForResult(i, joinwithcode_ID);
    }

    //after code is entered and pressed ok
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        switch(requestCode) {
            case (joinwithcode_ID) : {
                if (resultCode == Activity.RESULT_OK) {
                    String returnCode = data.getStringExtra("code");
                    checkForCode(returnCode);
                }
                break;
            }
        }
    }

    //check if the code exists in the database
    private void checkForCode(String meeting_ID) {
        final String meetingName = meeting_ID;

        //db
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        //meeting doc
        DocumentReference usersRef = database.collection("meetings").document(meetingName);
        //check if doc is there
        usersRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Log.d(TAG, "Meeting exists");
                    updateDB(meetingName);
                } else {
                    Toast.makeText(HomeScreenActivity.this,
                            "Meeting is not available",
                            Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Meeting does not exist");
                }
            }
        });
    }

    //update the database with the user into the meeting session
    private void updateDB(String meeting_ID) {
        Intent he = getIntent();
        String account_id = he.getStringExtra("accountKey");

        final ArrayList<User> users = new ArrayList<User>();
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

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

//        Meeting curr_meet = readSessionData(meeting_ID);
        curr_meet.addUsers(account_id);

        //add back to database
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

        //Exclaim that it works
        Toast.makeText(HomeScreenActivity.this,
                "Added the meeting to your joined sessions",
                Toast.LENGTH_LONG).show();
    }

    /**
     * reads the data for this meeting based on the meetingName
     * if successful, meeting will hold the read data
     * @param meetingID the ID of the Meeting to read
     */
    private Meeting readSessionData(String meetingID) {
        DocumentSnapshot docSnap = FirebaseFirestore.getInstance().collection("meetings")
                .document(meetingID).get().getResult();
        if (docSnap.exists()) {
            return docSnap.toObject(Meeting.class);
        } else {
            Log.d(TAG, "Meeting does not exist");
            return null;
        }
    }
}
