package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Sang on 4/4/18.
 */

public class ViewUserActivity extends AppCompatActivity {
    private String TAG = "ViewUserActivity";
    private FirebaseFirestore database;

    private Meeting meeting;

    private String meeting_ID;
    private String user_ID = "";

    private String ownerName = "";
    private int numUsers;

    List<String> days;
    TreeMap<String, String> usersToView;
    TreeMap<String, Bitmap> imagesToView;

    private LinearLayout names;
    private LinearLayout images;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        Intent i = this.getIntent();

        meeting_ID = i.getStringExtra("MEETING"); //meeting_ID
        user_ID = i.getStringExtra("accountKey"); //owner_ID

        usersToView = new TreeMap<String, String>();
        imagesToView = new TreeMap<String, Bitmap>();

        names = (LinearLayout) findViewById(R.id.listUsers);
        images = (LinearLayout) findViewById(R.id.listImages);

        database = FirebaseFirestore.getInstance();
        readSessionData(meeting_ID);
    }

    /**
     * Return to previous screen.
     *
     * @param v current {@code View}
     */
    public void onBackButtonClick(View v) {
        finish();
    }

    /**
     * Read the data from a meeting, loading any parsed data into {@code meeting}.
     *
     * @param meetingID ID of the meeting to be read
     */
    private void readSessionData(String meetingID) {
        // get the meeting in the database
        database.collection("meetings").document(meetingID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            Log.d(TAG, "onSuccess: Found meeting!");
                            meeting = documentSnapshots.toObject(Meeting.class);

                            TextView meeting_ID = (TextView) findViewById(R.id.meetingName);
                            meeting_ID.setText(meeting.getName()); //set display name to meetingName

                            updateUI(meeting.getUsers());
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

    private void getUserName(final String user_ID) {
        database.collection("users").document(user_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            String name = documentSnapshots.get("userName").toString();
                            usersToView.put(user_ID, name);
                            Log.d(TAG, "onSuccess: Found user name!" + user_ID + name);
                            updateUIListView(name);
                            getImages(user_ID);
                        } else {
                            Log.d(TAG, "onSuccess: No Such owner");
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

    private void getImages(final String user_ID) {
        database.collection("users").document(user_ID).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            User you = documentSnapshots.toObject(User.class);

                            if (you.getImage() != null) {
                                byte[] decodedString = Base64.decode(you.getImage(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray
                                        (decodedString, 0, decodedString.length);
                                updateUIImages(decodedByte);
                            }
                            else {
                                Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                                Bitmap bmp = Bitmap.createBitmap(2, 2, conf); // this creates a MUTABLE bitmap
                                updateUIImages(bmp);
                            }
                            Log.d(TAG, "onSuccess: Found user name!" + user_ID);
                        } else {
                            Log.d(TAG, "onSuccess: No Such owner");
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
     * Update the UI to reflect the data loaded into {@code meeting}.
     */
    public void updateUI(Map<String, InternalUser> users) {
        TextView meetingName = findViewById(R.id.meetingName);
        meetingName.setTextSize(50);
        TextView numPeople = findViewById(R.id.userNum);
        numPeople.setTextSize(30);
        int numUsers = users.size();
        numPeople.setText(numUsers + " people in this group.");

        for (String id : users.keySet()) {
            getUserName(id);
        }
    }

    public void updateUIListView(String name) {
        TextView newText = new TextView(this);
        newText.setText(name);
        newText.setTextSize(40);
        newText.setGravity(Gravity.BOTTOM);
        names.addView(newText);
    }

    public void updateUIImages(Bitmap bmp) {
        Log.d(TAG, " bmp " + bmp.getByteCount());
        if(bmp.getByteCount() < 50) {
            ImageView imageView = new ImageView(this);
            int width = 100;
            int height = 100;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
            imageView.setLayoutParams(parms);
            imageView.setBackgroundResource(R.drawable.image_preview);

            images.addView(imageView);
        } else {
            ImageView imageView = new ImageView(this);
            int width = 100;
            int height = 100;
            LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width, height);
            imageView.setLayoutParams(parms);
            imageView.setBackground(new BitmapDrawable(getResources(), bmp));

            images.addView(imageView);
        }
    }



}

