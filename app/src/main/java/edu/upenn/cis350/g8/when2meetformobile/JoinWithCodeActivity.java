package edu.upenn.cis350.g8.when2meetformobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class JoinWithCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joiningwithcode);
    }

    public void onClickOK2(View view) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("code", ((EditText) findViewById(R.id.code)).getText().toString());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public void onClickCancel2(View view) {
        finish();
    }
}