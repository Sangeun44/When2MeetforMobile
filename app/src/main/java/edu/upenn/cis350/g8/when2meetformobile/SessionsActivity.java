package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SessionsActivity extends AppCompatActivity {

    public static final int SessionDisplayActivity_ID = 2;
    private static final String TAG = "When2MeetSessions";
    private Map<String, Meeting> myMeetings;
    String userID;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sessions);
        Intent i = getIntent();
        userID = i.getStringExtra("accountKey");
        type = i.getStringExtra("type");
        myMeetings = new HashMap<String, Meeting>();
        populateMeetings();

        //createButtons();
    }

    /**
     * Populates the list of Meetings that is used to generate buttons
     * based on the type of sessions folder
     */
    private void populateMeetings() {
        if (type.equals("joined")) {
            FirebaseFirestore.getInstance().collection("meetings").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            if (documentSnapshots.isEmpty()) {
                                Log.d(TAG, "onSuccess: LIST EMPTY");
                            } else {
                                for (DocumentSnapshot thisDoc : documentSnapshots.getDocuments()) {
                                    Meeting m = thisDoc.toObject(Meeting.class);

                                    if (m.containsUserNotAsOwner(userID)) {
                                        myMeetings.put(thisDoc.getId(), m);

                                    }
                                }

                                int numMeetings =  myMeetings.size();
                                Log.d(TAG,"onSuccess: Found " + numMeetings + " meetings!");

                                for (String id: myMeetings.keySet()) {
                                    Log.d( TAG,"ID: " + id + "Meeting name: " + myMeetings.get(id).getName());
                                }

                                createButtons();
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

        if (type.equals("created")) {
            FirebaseFirestore.getInstance().collection("meetings")
                    .whereEqualTo("owner", userID).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot documentSnapshots) {
                            if (documentSnapshots.isEmpty()) {
                                Log.d(TAG, "onSuccess: LIST EMPTY");
                            } else {
                                for (DocumentSnapshot thisDoc : documentSnapshots.getDocuments()) {
                                    Meeting m = thisDoc.toObject(Meeting.class);
                                    Log.d( TAG,"ID: " + thisDoc.getId() + "Meeting name: " + m.getName());
                                    myMeetings.put(thisDoc.getId(), m);
                                }
                                int numMeetings =  myMeetings.size();
                                Log.d(TAG,"onSuccess: Found " + numMeetings + " meetings!");

                                createButtons();
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


    }

    /**
     * Creates a button for every Meeting in myMeetings and
     * sets it up to pass on the ID of the meeting to the display Activity
     */
    public void createButtons() {
        final Context context = this;
        LinearLayout main = findViewById(R.id.mainLinear);

        if (myMeetings != null) {
            for (final String id: myMeetings.keySet()) {
                final Meeting m = myMeetings.get(id);
                Button b = new Button(this);
                b.setText(m.getName());
                Log.d(TAG,"button created: " + m.getName());

                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, SessionDisplayActivity.class);
                        intent.putExtra("MEETING", id);
                        intent.putExtra("type", type);
                        intent.putExtra("accountKey", userID);
                        startActivityForResult(intent, SessionDisplayActivity_ID);
                    }
                });
                LinearLayout.LayoutParams lp =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                main.addView(b, lp);
            }
        }
    }


}
