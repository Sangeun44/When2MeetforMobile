package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddTimesActivity extends AppCompatActivity {

    private static final String TAG = "When2MeetAddTimes";

    private String meetingID;
    private String userID;
    private Meeting meeting;
    private List<String> selection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_times);
        Intent i = this.getIntent();
        meetingID = i.getStringExtra("MEETING");
        userID = i.getStringExtra("accountKey");
        readSessionData(meetingID);
        selection = new ArrayList<String>();
    }


    /**
     * Initialize the date buttons.
     */
    private void initSelection() {
        List<String> dates = meeting.getDates();
        String ending = "/" + Calendar.getInstance().get(Calendar.YEAR);
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");

        if (!meeting.getIsDays()) {
            int btnCount = 0;
            int i = 1;
            while (btnCount < 30 && i < 30) {
                c.setTime(dt);
                c.add(Calendar.DATE, i);
                String date = sdf.format(c.getTime());
                if (!dates.contains(date + ending)) {
                    int id = getResources().getIdentifier("btn" + btnCount, "id", getPackageName());
                    ((Button) findViewById(id)).setText(date);
                    btnCount++;
                }
                i++;
            }
        } else {
            for (String date : dates) {
                String day = DayData.getDayOfWeekFromDateString(date);
                int id = getResources().getIdentifier(day, "id", getPackageName());
                ((Button) findViewById(id)).setEnabled(false);
            }
        }

    }

    /**
     * initialize the given spinner
     * @param type denotes the spinner (either "early" or "late")
     */
    private void initSpinner(String type) {
        List<String> items = new ArrayList<String>();
        Spinner s;
        if (type.equals("early")) {
            s = (Spinner) findViewById(R.id.spnEarly);
            int low_time = meeting.getLow_time();
            for (int i = 0; i <= low_time; i++) {
                items.add(changeTime(i));
            }
        } else {
            s = (Spinner) findViewById(R.id.spnLate);
            int high_time = meeting.getHigh_time();
            for (int i = high_time; i <= 24 ; i++) {
                items.add(changeTime(i));
            }
        }

        String[] arraySpinner = items.toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
    }

    /**
     * Read the data from a meeting, loading any parsed data into {@code meeting}.
     *
     * @param meetingID ID of the meeting to be read
     */
    private void readSessionData(String meetingID) {
        // get the meeting in the database
        FirebaseFirestore.getInstance().collection("meetings").document(meetingID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            meeting = documentSnapshots.toObject(Meeting.class);
                            Log.d(TAG,"onSuccess: Found meeting!");
                            findViewById(R.id.weekdays).setVisibility(meeting.getIsDays() ? View.VISIBLE : View.INVISIBLE);
                            findViewById(R.id.month).setVisibility(meeting.getIsDays() ? View.INVISIBLE : View.VISIBLE);
                            initSelection();
                            initSpinner("early");
                            initSpinner("late");
                            setCurrentData();
                        } else {
                            Log.d(TAG, "onSuccess: No Such meeting");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Error getting data!!!",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * sets the text displaying the current data for this meeting
     */
    private void setCurrentData() {
        TextView meetingData = (TextView) findViewById(R.id.txtCurrentDates);
        StringBuilder currentData = new StringBuilder();
        currentData.append("Current Dates and Times: \n");
        currentData.append("Times from " + changeTime(meeting.getLow_time()) + " to " +
                changeTime(meeting.getHigh_time()));
        currentData.append("\nDates: ");

        for (String date : meeting.getDates()) {
            if (meeting.getIsDays()) {
                String day = DayData.getDayOfWeekFromDateString(date);
                currentData.append(day + " ");
            } else {
                currentData.append(date + " ");
            }

        }
        meetingData.setText(currentData.toString());
    }

    /**
     * Updates currently selected weekdays.
     *
     * @param view weekday button pressed
     */
    public void onSelectWeekdays(View view) {
        String name = ((Button) view).getText().toString();
        if (selection.contains(name)) {
            selection.remove(name);
            view.getBackground().setColorFilter(Color.parseColor("#66999999"),
                    PorterDuff.Mode.DARKEN);
        } else {
            selection.add(name);
            view.getBackground().setColorFilter(Color.parseColor("#9900ff00"),
                    PorterDuff.Mode.DARKEN);
        }
    }

    /**
     * Updates currently selected dates.
     *
     * @param view date button pressed
     */
    public void onSelectDates(View view) {
        String date = ((Button) view).getText().toString() + "/" +
                Calendar.getInstance().get(Calendar.YEAR);

        if (selection.contains(date)) {
            selection.remove(date);
            view.getBackground().setColorFilter(Color.parseColor("#66999999"),
                    PorterDuff.Mode.DARKEN);
        } else {
            selection.add(date);
            view.getBackground().setColorFilter(Color.parseColor("#9900ff00"),
                    PorterDuff.Mode.DARKEN);
        }
        Collections.sort(selection);
    }

    /**
     * Changes the time string to an integer representation in military time.
     *
     * @param time time string
     * @return hour in military time
     */
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

    /**
     * Changes the int time to the time string
     * @param time the hour in military time
     * @return the time String (ie "12 pm")
     */
    private String changeTime(int time) {
        if (time == 24 || time == 0) {
            return "12 am";
        } else if (time > 12 && time < 24) {
            return (time - 12 + " pm");
        } else  if (time == 12) {
            return "12 pm";
        } else {
            return (time + " am");
        }
    }

    /**
     * changes all of the days (ie "mon" ) to dates (ie "04/23/2018")
     * @return a list of Strings in dd/mm/yyyy format
     */
    private List<String> changeDaysToDates() {
        List<String> datesFromDays = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
        String ending = "/" + Calendar.getInstance().get(Calendar.YEAR);
        Date dt = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dt);
        int weekYear = c.getWeekYear();
        int weekOfYear = c.get(Calendar.WEEK_OF_YEAR) + 1;
        for (String s: selection) {
            c.setWeekDate(weekYear, weekOfYear, DayData.getDateFromDay(s.toLowerCase()));
            dt = c.getTime();
            String date = sdf.format(dt) + ending;
            datesFromDays.add(date);
        }
        return datesFromDays;
    }

    /**
     * Updates the meeting with the new times and adds the new dates
     * Overwrites old meeting in the database with new data
     * @param view the view of the submit button
     */
    public void onClickCreateButton(View view) {
        int minTime = changeTime(((Spinner) findViewById(R.id.spnEarly)).getSelectedItem().toString());
        int maxTime = changeTime(((Spinner) findViewById(R.id.spnLate)).getSelectedItem().toString());
        meeting.setLow_time(minTime);
        meeting.setHigh_time(maxTime);
        if (meeting.getIsDays()) {
            meeting.addDates(changeDaysToDates());
        } else {
            meeting.addDates(selection);
        }

        FirebaseFirestore.getInstance().collection("meetings").document(meetingID)
                .set(meeting, SetOptions.merge())
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

        Intent i = this.getIntent();
        setResult(RESULT_OK, i);
        finish();
    }

}
