package edu.upenn.cis350.g8.when2meetformobile;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class CreateSessionActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    final static String TAG = "CreateSessionAct";

    FirebaseFirestore database;
    DocumentReference ref;

    ArrayList<String> daysSelected;
    ArrayList<String> datesSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createsession);

        database = FirebaseFirestore.getInstance();
        ref = database.collection("my_collection").document();
        ((TextView) findViewById(R.id.code)).setText(ref.getId());

        daysSelected = new ArrayList<>();
        datesSelected = new ArrayList<>();
        initDates();

        initSpinner((Spinner) findViewById(R.id.mode), R.array.mode);
        initSpinner((Spinner) findViewById(R.id.earliest), R.array.hours);
        initSpinner((Spinner) findViewById(R.id.latest), R.array.hours);
    }

    private void initDates() {
        for(int i = 0; i < 35; i++) {
            Date dt = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.add(Calendar.DATE, i);
            dt = c.getTime();
            String date = new SimpleDateFormat("MM/dd").format(dt);

            int id = getResources().getIdentifier("btn" + i, "id", getPackageName());
            ((Button) findViewById(id)).setText(date);
        }
    }

    private void initSpinner(Spinner spinner, int arrayId) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    //select weekdays
    public void onSelectWeekdays(View view) {
        String name = ((Button) view).getText().toString();
        if (daysSelected.contains(name)) {
            daysSelected.remove(name);
            view.getBackground().setColorFilter(Color.parseColor("#66999999"),
                    PorterDuff.Mode.DARKEN);
        } else {
            daysSelected.add(name);
            view.getBackground().setColorFilter(Color.parseColor("#9900ff00"),
                    PorterDuff.Mode.DARKEN);
        }
    }

    //select dates
    public void onSelectDates(View view) {
        String date = ((Button) view).getText().toString() + "/" +
                Calendar.getInstance().get(Calendar.YEAR);

        if (datesSelected.contains(date)) {
            datesSelected.remove(date);
            view.getBackground().setColorFilter(Color.parseColor("#66999999"),
                    PorterDuff.Mode.DARKEN);
        } else {
            datesSelected.add(date);
            view.getBackground().setColorFilter(Color.parseColor("#9900ff00"),
                    PorterDuff.Mode.DARKEN);
        }
        Collections.sort(datesSelected);
    }

    //clicked on the create event button
    public void onClickCreateButton(View view) {
        String eventName = ((EditText) findViewById(R.id.eventName)).getText().toString();
        String modeStr = ((Spinner) findViewById(R.id.mode)).getSelectedItem().toString();
        int t1 = changeTime(((Spinner) findViewById(R.id.earliest)).getSelectedItem().toString());
        int t2 = changeTime(((Spinner) findViewById(R.id.latest)).getSelectedItem().toString());

        if (eventName.length() == 0) {
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to enter in the event name!", Toast.LENGTH_SHORT).show();
        } else if (modeStr.equals("Specific Date") && datesSelected.isEmpty()) {
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to select dates!", Toast.LENGTH_SHORT).show();
        } else if (modeStr.equals("Days of the Week") && daysSelected.isEmpty()) {
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to select days!", Toast.LENGTH_SHORT).show();
        } else if (t1 >= t2) {
            Toast.makeText(CreateSessionActivity.this,
                    "Remember to choose valid times!", Toast.LENGTH_SHORT).show();
        } else {
            updateDB(eventName, t1, t2);
            finish();
        }
    }

    private int changeTime(String time) {
        int t = Integer.parseInt(time.substring(0,2).trim());
        if (time.contains("pm") && t < 12) {
            return t + 12;
        } else if(time.contains("am") && t == 12) {
            return t + 12;
        } else {
            return t;
        }
    }

    private void updateDB(String eventName, int t1, int t2) {
        String owner_id = getIntent().getStringExtra("accountKey");
        HashMap<String, User> users = new HashMap<>();
        Meeting meet = new Meeting(users, datesSelected, t2, t1, eventName, owner_id);
        meet.addUsers(owner_id);

        database.collection("meetings").document(ref.getId())
                .set(meet, SetOptions.merge())
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
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getId() == R.id.mode) {
            boolean isDate = adapterView.getSelectedItem().toString().equals("Specific Date");
            findViewById(R.id.weekdays).setVisibility(isDate ? View.INVISIBLE : View.VISIBLE);
            findViewById(R.id.month).setVisibility(isDate ? View.VISIBLE : View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}