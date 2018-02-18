package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

/**
 * Created by Sang on 2/15/18.
 */

public class CreateSessionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    final static int createSession_ID = 1; //main id

    private Spinner mode; //dates or days
    private Spinner earliest;
    private Spinner latest;
    private Spinner type; //code or email

    //string storage
    private String modeStr;
    private String earliestStr;
    private String latestStr;
    private String typeStr;
    private String codeStr;
    private String eventName;

    //storage
    ArrayList<View> daysSelected;
    ArrayList<View> datesSelected;
    ArrayList<String> emailList;

    public CreateSessionActivity() {
        daysSelected = new ArrayList<View>();
        datesSelected = new ArrayList<View>();
        emailList = new ArrayList<String>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createsession);

        //populate 4 spinners
        //mode
        populateMode();
        //earliest
        populateEarliest();
        //latest
        populateLatest();
        //type
        populateType();

        //same onItemSelected
        mode.setOnItemSelectedListener(this);
        earliest.setOnItemSelectedListener(this);
        latest.setOnItemSelectedListener(this);
        type.setOnItemSelectedListener(this);

        //event name
        EditText text = (EditText)findViewById(R.id.eventName);
        eventName = (String) text.toString();

        //set up dates
        setUpDates();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // for each spinner and their relative choices
        switch(parent.getId()) {
            case R.id.mode:
                //mode = date or days
                modeStr = mode.getSelectedItem().toString();
                View week = findViewById(R.id.weekdays);
                View month = findViewById(R.id.month);
                if(modeStr.equals("Specific Date")) {
                    week.setVisibility(View.INVISIBLE);
                    month.setVisibility(View.VISIBLE);
                }
                else {
                    week.setVisibility(View.VISIBLE);
                    month.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.earliest:
                //earliest time
                earliestStr = earliest.getSelectedItem().toString();
                break;
            case R.id.latest:
                //latest time
                latestStr = latest.getSelectedItem().toString();
                break;
            case R.id.choice:
                //email or code?
                typeStr = type.getSelectedItem().toString();
                View codeV = findViewById(R.id.code);
                View codedV = findViewById(R.id.coded);
                if(typeStr.equals("code")) {
                    //code
                    codeV.setVisibility(View.VISIBLE);
                    codedV.setVisibility(View.VISIBLE);
                    String code = createRandomCode();
                    ((TextView) codedV).setText(code);
                    codeStr = code;
                }
                else {
                    //email
                    codeV.setVisibility(View.INVISIBLE);
                    codedV.setVisibility(View.INVISIBLE);
                    startEmailActivity(); //start email activity when user chooses email
                }
                break;
        }
    }

    //start emailActivity to insert emails when user chooses email
    public void startEmailActivity() {
        Intent i = new Intent(this, EmailActivity.class);
        startActivityForResult(i, createSession_ID);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == createSession_ID) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                emailList = data.getStringArrayListExtra("result");
                Log.d("first", "" + emailList.size());
            }
        }
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        Log.d("try", "check it");
    }

    //select weekdays
    public void onSelectWeekdays(View view) {
        if(daysSelected.contains(view)) {
            daysSelected.remove(view);
            view.setBackgroundColor(Color.LTGRAY);
        }
        else {
            daysSelected.add(view);
            view.setBackgroundColor(Color.GREEN);
        }
    }

    //select dates
    public void onSelectDates(View view) {
        if(datesSelected.contains(view)) {
            datesSelected.remove(view);
            view.setBackgroundColor(Color.LTGRAY);
        }
        else {
            datesSelected.add(view);
            view.setBackgroundColor(Color.GREEN);
        }
    }

    //clicked on the create event button
    public void onClickCreateButton(View view) {
        Log.d("first", "first");
        Log.d("first", earliestStr);
        Log.d("first", latestStr);
        Log.d("first", codeStr);

        //check event name
        EditText eventN = (EditText) findViewById(R.id.eventName);
        if(eventN.getText().toString().length() > 0) {
            eventName = eventN.getText().toString();
        }
        else if (eventN.getText().toString().length() == 0){
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to enter in the event name!",
                    Toast.LENGTH_SHORT).show();
        }

        //check number of dates selected if
        if(modeStr.equals("Specific Date") && datesSelected.isEmpty()) {
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to select dates!",
                    Toast.LENGTH_SHORT).show();
        }
        else if(modeStr.equals("Days of the Week") && daysSelected.isEmpty()) {
            //check number of days selected if
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to select days!",
                    Toast.LENGTH_SHORT).show();
        }
        //check if the email list is filled if
        if(typeStr.equals("email") && emailList.isEmpty() || emailList.size() < 0) {
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to enter in emails!",
                    Toast.LENGTH_SHORT).show();
        }
    }

    //code making for code sending
    public String createRandomCode() {
        String str = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        StringBuilder build = new StringBuilder();
        while(build.toString().length() < 4) {
            Random rand = new Random();
            int value = rand.nextInt(36);
            char c = str.charAt(value);
            build.append(c);
        }
        return build.toString();
    }

    //set up dates
    public void setUpDates() {
        for(int i = 0; i < 35; i++) {
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, i);
            dt = c.getTime();

            Resources res = getResources();
            String name = "btn" + i;
            int id = res.getIdentifier(name, "id", this.getPackageName());
            Log.d("check id", "" + id);
            Button btn = findViewById(id);
            btn.setText(dt.toString().substring(4,10));
        }
    }

///--------spinner fill

    //fill mode
    public void populateMode() {
        mode = (Spinner) findViewById(R.id.mode);

        //Create an ArrAdapt using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.mode, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        mode.setAdapter(adapter);
    }

    //fill earliest
    public void populateEarliest() {
        earliest = (Spinner) findViewById(R.id.earliest);

        //Create an ArrAdapt using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        earliest.setAdapter(adapter);
    }

    //fill latest
    public void populateLatest() {
        latest = (Spinner) findViewById(R.id.latest);
        //Create an ArrAdapt using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.hours, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        latest.setAdapter(adapter);
    }

    //fill type
    public void populateType() {
        type = (Spinner) findViewById(R.id.choice);
        //Create an ArrAdapt using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type, android.R.layout.simple_spinner_item);
        //Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply the adapter to the spinner
        type.setAdapter(adapter);
    }
}
