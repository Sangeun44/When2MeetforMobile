package edu.upenn.cis350.g8.when2meetformobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Sang on 2/16/18.
 */

public class EmailActivity extends AppCompatActivity {
    ArrayList<String> emailsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailsList = new ArrayList<String>();
    }

    public void onClickOk(View view) {
        EditText fill = (EditText) findViewById(R.id.emails);

        //check if there are emails filled in
        if(fill.getText().toString().length() == 0) {
            Toast.makeText(EmailActivity.this,
                    "Remember to enter in emails",
                    Toast.LENGTH_SHORT).show();
        } else {
            //split the text field into new lines
            String emails = fill.getText().toString();
            String[] array = emails.split("\\n");
            check(array);

            //return email list to main create activity
            Intent returnIntent = new Intent();
            returnIntent.putStringArrayListExtra("result", emailsList);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    public final static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void check(String[] array) {
        for (int i = 0; i < array.length; i++){
            if (array[i].contains("@")){
                emailsList.add(array[i]);
            }
        }
    }
}
