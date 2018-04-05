package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Sang on 4/4/18.
 */

public class ViewUserActivity extends AppCompatActivity {
        private String TAG = "ViewUserActivity";
        private FirebaseFirestore database;

        private Meeting meeting;

        private String meeting_ID;
        private String user_ID = "";

        private String ownerName = "";
        private int numUsers;

        List<String> days;
        HashMap<String, String> usersToView;

        private ListView ls;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view_users);
            Intent i = this.getIntent();

            meeting_ID = i.getStringExtra("MEETING"); //meeting_ID
            user_ID = i.getStringExtra("accountKey"); //owner_ID

            usersToView = new HashMap<String, String>();
            ls = (ListView) findViewById(R.id.listUsers);

            database = FirebaseFirestore.getInstance();
            readSessionData(meeting_ID);
        }

        /**
        * Return to previous screen.
        *
        * @param v current {@code View}
        */
        public void onBackButtonClick(View v) {
            finish();
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
                            TextView meeting_ID = (TextView) findViewById(R.id.meetingName);
                            meeting_ID.setText(meeting.getName()); //set display name to meetingName
                            updateUI(meeting.getUsers());
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
                                usersToView.put(user_ID, name);
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
        * Update the UI to reflect the data loaded into {@code meeting}.
        */
        public void updateUI(Map<String, User> users) {
            for (String id : users.keySet()) {
                getUserName(id);
            }
        }

        public void updateUIPeople() {
            Map<String, User> users = meeting.getUsers();

            TextView numPeople = findViewById(R.id.userNum);
            int numUsers = users.size();
            numPeople.setText(numUsers + " people in this group.");

            List<String> listUsers = new ArrayList<String>();

            for (String id : users.keySet()) {
                listUsers.add(usersToView.get(id));
                Log.d(TAG, usersToView.get(id));
            }

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, listUsers);
            ls.setAdapter(arrayAdapter);
        }



}

