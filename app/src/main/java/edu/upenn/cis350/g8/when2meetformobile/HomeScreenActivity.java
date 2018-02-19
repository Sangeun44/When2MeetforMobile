package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Evie on 2/18/18.
 */

public class HomeScreenActivity extends AppCompatActivity {

    public static final int JoinedSessionActivity_ID = 1;

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
        startActivityForResult(i, JoinedSessionActivity_ID);
    }

    public void onLogoutButtonClick(View view) {
        //return to login page
        finish();
    }

    public void onCreateButtonClick(View view) {
        //Sang's page
        //CreateSessionActivity
    }

    public void onProfileButtonClick(View view) {

    }

    public void onJoinButtonClick(View view) {

    }
}
