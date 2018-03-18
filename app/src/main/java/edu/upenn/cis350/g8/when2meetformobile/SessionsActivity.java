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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JoinedSessionsActivity extends AppCompatActivity {

    public static final int JoinedSessionDisplayActivity_ID = 2;
    private static final String TAG = "When2MeetJoinedSessions";
    private List<Meeting> myMeetings;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_sessions);
        Intent i = new Intent(this, JoinedSessionDisplayActivity.class);
        userID = i.getStringExtra("accountNum");
        //populateMap(userID);
        myMeetings = new ArrayList<Meeting>();
        myMeetings.add(new Meeting(null, null, 8, 20, "Test Event", "10"));
        createButtons();
    }

    private void populateMap(String ownerID) {
        FirebaseFirestore.getInstance().collection("meetings").whereEqualTo("owner", ownerID).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        if (documentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                        } else {
                            myMeetings = documentSnapshots.toObjects(Meeting.class);
                            Log.d(TAG, "onSuccess: Found " + myMeetings.size() + " meetings!");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting data!!!", Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void createButtons() {
        final Context context = this;
        LinearLayout main = findViewById(R.id.mainLinear);

        if (myMeetings != null) {
            for (int i = 0; i < myMeetings.size(); i++) {
                final Meeting m = myMeetings.get(i);
                Button b = new Button(this);
                b.setText(m.getName());
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(context, JoinedSessionDisplayActivity.class);
                        i.putExtra("MEETING", m.getName());
                        startActivityForResult(i, JoinedSessionDisplayActivity_ID);
                    }
                });
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                main.addView(b, lp);
            }
        }
    }


}
