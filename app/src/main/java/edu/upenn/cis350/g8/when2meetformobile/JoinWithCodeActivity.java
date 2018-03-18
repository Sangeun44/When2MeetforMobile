package edu.upenn.cis350.g8.when2meetformobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sang on 3/17/18.
 * Activity for entering code for joining the session
 */

public class JoinWithCodeActivity extends AppCompatActivity {

    public static final int JoinedSessionDisplayActivity_ID = 2;
    private static final String TAG = "When2MeetJoinedSessions";
    private List<Meeting> myMeetings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joiningwithcode);
    }

    public void onClickOk() {
        EditText eventN = (EditText) findViewById(R.id.code);
        Intent resultIntent = new Intent();
        //TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putExtra("code", eventN.getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onClickCancel() {
        finish();
    }

}