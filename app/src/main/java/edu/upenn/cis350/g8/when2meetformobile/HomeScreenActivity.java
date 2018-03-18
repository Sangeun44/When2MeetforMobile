package edu.upenn.cis350.g8.when2meetformobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evie on 2/18/18.
 */

public class HomeScreenActivity extends AppCompatActivity {

    public static final int JoinedSessionActivity_ID = 3;
    public static final int CreateSessionActivity_ID = 4;
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

    }

    public void onJoinedSessionsButtonClick(View view) {
        Intent i = new Intent(this, JoinedSessionsActivity.class);
        int user_id = i.getIntExtra("accountNum", 0);
        i.putExtra("accountNum", user_id);
        startActivityForResult(i, JoinedSessionActivity_ID);
    }

    public void onLogoutButtonClick(View view) {
        //return to login page
        finish();
    }

    public void onCreateButtonClick(View view) {
        //Sang's page
        Intent intent = getIntent();
        int user_id = intent.getIntExtra("accountNum", 0);
        //CreateSessionActivity
        Intent i = new Intent(this, CreateSessionActivity.class);
        i.putExtra("accountNum", user_id);
        startActivityForResult(i, CreateSessionActivity_ID);
    }

    public void onProfileButtonClick(View view) {

    }

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

    private void onJoinButtonClick(View view) {
        Intent intent = getIntent();
        int user_id = intent.getIntExtra("code", 0);
        //joinSessionActivity
        Intent i = new Intent(this, CreateSessionActivity.class);
        i.putExtra("code", user_id);
        startActivityForResult(i, joinwithcode_ID);
    }

    private void checkForCode(String m) {
        final String TAG = "FireBase";
        String meetingName = m;

        FirebaseFirestore.getInstance().collection("meetings").whereEqualTo("name", meetingName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: No such meeting");
                        } else {
                            Meeting m = documentSnapshots.toObjects(Meeting.class).get(0);
                            Log.d(TAG, "onSuccess: Found your meeting!");
                            updateDB(m);
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

    private void updateDB(Meeting m) {
        ArrayList<User> users = new ArrayList<User>();

        DocumentReference ref =  FirebaseFirestore.getInstance().collection("meetings").document();
        String meetingIDStr = ref.getId();

        //get user from sign in activity
        Intent intent = getIntent();
        int account_id = intent.getIntExtra("accountNum", 0);

        FirebaseFirestore mDatabase = FirebaseFirestore.getInstance();
        mDatabase.collection("meetings").;
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "DocumentSnapshot successfully written!");
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.w(TAG, "Error writing document", e);
//                        }
//                    });
    }
}
