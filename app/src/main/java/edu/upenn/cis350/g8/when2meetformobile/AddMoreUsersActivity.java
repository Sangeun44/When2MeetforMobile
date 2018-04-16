package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sang on 3/30/18.
 */

public class AddMoreUsersActivity extends AppCompatActivity {
    private String TAG = "addmoreUsersActivity";
    private FirebaseFirestore database;

    private Meeting meeting;
    private String meeting_ID;
    private String user_ID = "";
    private String ownerName = "";

    List<String> days;

    HashMap<String, String> usersToAdd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = FirebaseFirestore.getInstance();

        Intent i = getIntent();
        user_ID = i.getStringExtra("accountName");
        meeting_ID = i.getStringExtra("MEETING");
        usersToAdd = new HashMap<String, String>();

        getMeetings();
        Log.d("meeting id: ",  meeting_ID);
        setContentView(R.layout.activity_addmoreusers);
    }

    private void getMeetings() {
        // get the meeting in the database
        database.collection("meetings").document(meeting_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            meeting = documentSnapshots.toObject(Meeting.class);
                            Log.d(TAG,"onSuccess: Found meeting!");
                            TextView name = (TextView) findViewById(R.id.meetingName);
                            name.setText(meeting.getName());
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
     * click ok
     **/
    public void onClickOk(View view) {
        EditText inputs = (EditText) findViewById(R.id.userNames);
        String inputStr = inputs.getText().toString();
        String[] inputArr = inputStr.split("\n");

        //get owner's name from user_id
        getOwnerName();

        //get users from the data base and put it into a arraylist
        Log.d(TAG, "owner: " + ownerName);
        for(int i = 0; i < inputArr.length; ++i) {
            if(!ownerName.equals(inputArr[i])) {
                getUserID(inputArr[i]);
            } else {
                Toast.makeText(getApplicationContext(), "You're adding yourself",
                        Toast.LENGTH_LONG).show();
            }
        }
        finish();
    }

    /**
     * get user from database
     * accountName = user name
     */
    private void getOwnerName() {
        Log.d(TAG, "user id " + user_ID);
        FirebaseFirestore.getInstance().collection("users").document(user_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            String name = documentSnapshots.get("name").toString();
                            ownerName = name;
                            Log.d(TAG,"onSuccess: Found user owner!");
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
     * get user from database
     * accountName = user name
     */
    private void getUserID(String userName) {
        Log.d(TAG, userName);
        database.collection("users")
                .whereEqualTo("name", userName).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Toast.makeText(AddMoreUsersActivity.this,
                                    "No such user", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                        } else {
                            for (DocumentSnapshot thisDoc : documentSnapshots.getDocuments()) {
                                Log.d( TAG,"User ID: " + thisDoc.getId() + " User name: " + thisDoc.get("name"));
                                usersToAdd.put(thisDoc.getId(), thisDoc.get("name").toString());
                                //update database
                                updateDB(meeting_ID);
                            }
                            Log.d(TAG,"onSuccess: Found user!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "Error getting data!!!", Toast.LENGTH_LONG).show();
                    }
                });

    }

    /**
     * Updates the meeting in the database, merging the new user data with the old.
     *
     * @param meeting_ID ID of the meeting to update in the database
     */
    private void updateDB(String meeting_ID) {
        // add users
        for(Map.Entry<String, String> user : usersToAdd.entrySet()) {
           meeting.addUsers(user.getKey());
           Log.d(TAG, "NUM OF USERS: " + meeting.getNumUsers());
        }
        Toast.makeText(getApplicationContext(), "Adding Users...",
                Toast.LENGTH_LONG).show();

        // add back to database
        FirebaseFirestore.getInstance().collection("meetings").document(meeting_ID)
                .set(meeting, SetOptions.merge())
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
