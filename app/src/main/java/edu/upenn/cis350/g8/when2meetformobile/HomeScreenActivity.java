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
import com.google.firebase.firestore.FirebaseFirestore;
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

    public static final int JoinedSessionActivity_ID = 3;
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

    }

//    public void onJoinedSessionsButtonClick(View view) {
//        Intent i = new Intent(this, JoinedSessionsActivity.class);
//        int user_id = i.getIntExtra("accountNum", 0);
//        i.putExtra("accountNum", user_id);
//        startActivityForResult(i, JoinedSessionActivity_ID);
//    }

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
    private void checkForCode(String m) {
        Log.d(TAG, m);
        final String meetingName = "m";

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
    private void updateDB(String m) {
        final ArrayList<User> users = new ArrayList<User>();
        final GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference meetingDocRef = database.collection("meetings").document(m);
        String meetingIDStr = meetingDocRef.getId();
        Log.d(TAG, meetingIDStr);

        DocumentReference docRef = database.collection("meetings").document(m);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        //document
                        String documentStr = document.getData().toString();

                        int firstBracket = documentStr.indexOf("[");
                        int secondBracket = documentStr.indexOf("]");
                        String usersList = documentStr.substring(firstBracket + 1, secondBracket);

                        String[] userArray = usersList.split(",");

                        List<String> listOfUsers = new ArrayList<String>(Arrays.asList(userArray));

                        User newUser = new User();
                        //get user's id from sign in activity
                        if (acct != null) {
                            String personName = acct.getDisplayName();
                            newUser = new User(personName);
                        }

                        //add new user to the list of users in the meeting
                        listOfUsers.add(newUser.getName());

                        //update the list on firebase
                        document.getReference().update(
                                "users", listOfUsers
                        );

                        //exclaim that it works
                        Toast.makeText(HomeScreenActivity.this,
                                "Your Meeting has been added",
                                Toast.LENGTH_LONG).show();

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}
