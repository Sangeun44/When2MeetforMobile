package edu.upenn.cis350.g8.when2meetformobile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evie on 4/6/18.
 */

public class ProfileActivity extends AppCompatActivity{
        public static final int ProfileActivity_ID = 4675047;
        private static Handler mHandler = new Handler();
        final String TAG = "Profile";
        private User you;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);
            getUser();

            //populate views with what's currently in the db
            EditText name = findViewById(R.id.name);
            name.setText(you.getName());
            EditText number = findViewById(R.id.phoneNumber);
            number.setText(you.getPhoneNumber());
            EditText description = findViewById(R.id.description);
            description.setText(you.getDescription());
        }

    private void getUser() {
        // get the user from the database
        String userId = getIntent().getStringExtra("accountId");
        FirebaseFirestore.getInstance().collection("users").document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshots) {
                        if (documentSnapshots.exists()) {
                            you = documentSnapshots.toObject(User.class);
                            Log.d(TAG,"onSuccess: Found meeting!");
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

        protected void onClickSubmit(View view) {
            EditText name = findViewById(R.id.name);
            EditText number = findViewById(R.id.phoneNumber);
            EditText description = findViewById(R.id.description);

            User updatedYou = new User();
            updatedYou.setName(name.getText().toString());
            updatedYou.setNumber(number.getText().toString());
            updatedYou.setDescription(description.getText().toString());

            updateDB(updatedYou);

            Toast.makeText(getApplicationContext(), "Profile successfully updated!",
                    Toast.LENGTH_SHORT).show();

            mHandler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, Toast.LENGTH_SHORT + 3000);
        }

        protected void onClickCancel(View view) {
            finish();
        }

    private void updateDB(User user) {
        //add to database
        String userId = getIntent().getStringExtra("accountId");
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .set(user, SetOptions.merge())
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

    }
