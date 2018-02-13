package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class JoinedSessionDisplayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_session_display);
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
}
