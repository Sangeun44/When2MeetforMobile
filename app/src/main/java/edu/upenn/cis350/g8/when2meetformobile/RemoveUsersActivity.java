package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class RemoveUsersActivity extends AppCompatActivity {
    private static final String TAG = "When2MeetRemoveUsers";

    private String meetingID;
    private String ownerID;
    private Meeting meeting;
    private List<String> usersToRemove;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_users);
        Intent i = this.getIntent();
        meetingID = i.getStringExtra("MEETING"); //meeting_ID
        ownerID = i.getStringExtra("accountKey"); //owner_ID
        usersToRemove = new ArrayList<String>();
        readSessionData(meetingID);
    }

    /**
     * Read the data from a meeting, loading any parsed data into {@code meeting}.
     *
     * @param meetingID ID of the meeting to be read
     */
    private void readSessionData(String meetingID) {
        // get the meeting in the database
        FirebaseFirestore.getInstance().collection("meetings").document(meetingID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            meeting = documentSnapshots.toObject(Meeting.class);
                            for (String ID: meeting.getUsers().keySet()) {
                                getUserName(ID);
                            }
                            Log.d(TAG,"onSuccess: Found meeting!");
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
        FirebaseFirestore.getInstance().collection("users").document(user_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            String name = documentSnapshots.get("userName").toString();
                            if (!user_ID.equals(ownerID)) {
                                createCheckbox(name, user_ID);
                            }
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

    private void createCheckbox(String name, final String user_id) {
        LinearLayout mainLayout = findViewById(R.id.llRmUsers);
        CheckBox myBox = new CheckBox(this);
        myBox.setText(name);
        myBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    usersToRemove.add(user_id);
                } else {
                    usersToRemove.remove(user_id);
                }
            }
        });
        mainLayout.addView(myBox);
    }

    public void onClickSubmitButton(View v) {
        Toast.makeText(this, TextUtils.join(",", usersToRemove), Toast.LENGTH_SHORT).show();
        meeting.removeUsers(usersToRemove);

        FirebaseFirestore.getInstance().collection("meetings").document(meetingID)
                .set(meeting)
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

        Intent i = this.getIntent();
        setResult(RESULT_OK, i);
        finish();
    }

}
