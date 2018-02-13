package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class JoinedSessionsActivity extends AppCompatActivity {

    public static final int JoinedSessionDisplayActivity_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_sessions);
    }

    public void onSessionButtonClick(View v) {
        Intent i = new Intent(this, JoinedSessionDisplayActivity.class);
        Button btn = findViewById(v.getId());
        String session = btn.getText().toString();
        i.putExtra("SESSION", session);
        startActivityForResult(i, JoinedSessionDisplayActivity_ID);
    }
}
