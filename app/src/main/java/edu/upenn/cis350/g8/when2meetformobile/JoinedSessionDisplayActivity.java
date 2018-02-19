package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



public class JoinedSessionDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_session_display);
        Intent i = this.getIntent();
        String meetingID = i.getStringExtra("SESSION");
        updateUI(readSessionData(meetingID));
    }

    public void onHomeButtonClick(View v) {
        Intent i = new Intent(this, JoinedSessionsActivity.class);
        setResult(RESULT_OK, i);
        finish();
    }

    public void onEnterTimesButtonClick(View v) {
        Toast.makeText(getApplicationContext(), "Going to Enter Times Page...",
                Toast.LENGTH_SHORT).show();
    }

    public Meeting readSessionData(String meetingID) {
        //DocumentReference docRef = db.collection("meetings").document(meetingID);
        // asynchronously retrieve the document
        //ApiFuture<DocumentSnapshot> future = docRef.get();
        // block on response
        //DocumentSnapshot document = future.get();
        //Meeting meeting = null;
        //if (document.exists()) {
            // convert document to Meeting object
            //meeting = document.toObject(Meeting.class);
            //return meeting;
        //} else {
            //return null;
        //}
        return null;
    }

    public void updateUI(Meeting meeting) {
        if (meeting != null) {
            TextView txtPeople = findViewById(R.id.txtPeople);
            String people = "Respondents:";
            int counter = 1;
            for (User u : meeting.getUsers()) {
                if (u.enteredTimes()) {
                    String name = ""; //FirebaseFirestore.getInstance().collection("users").document()
                    people += "\n" + counter +  " " + name;
                    counter++;
                }
            }
            txtPeople.setText(people);

            TextView txtNumPeople = findViewById(R.id.txtNumPeople);
            txtNumPeople.setText(meeting.getNumUsers() + " people in this group.");

            // TODO: add best times info
        }
    }
}
