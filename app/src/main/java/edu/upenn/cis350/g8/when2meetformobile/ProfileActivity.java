package edu.upenn.cis350.g8.when2meetformobile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Evie on 4/6/18.
 */

public class ProfileActivity extends AppCompatActivity{
        public static final int ProfileActivity_ID = 4675047;
        public static final int GET_FROM_GALLERY = 3;
        private static Handler mHandler = new Handler();
        final String TAG = "Profile";
        private User you;
        private Bitmap image;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);
            getUser();
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

                            //populate views with what's currently in the db
                            EditText name = findViewById(R.id.name);
                            name.setText(you.getName());
                            EditText number = findViewById(R.id.phoneNumber);
                            number.setText(you.getPhoneNumber());
                            EditText description = findViewById(R.id.description);
                            description.setText(you.getDescription());

                            if (!you.getImage().isEmpty()) {
                                ImageButton img = findViewById(R.id.profileImage);
                                byte[] decodedString = Base64.decode(you.getImage(), Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray
                                        (decodedString, 0, decodedString.length);
                                img.setBackground(new BitmapDrawable(getResources(), decodedByte));
                            }
                            Log.d(TAG,"onSuccess: Found user!");
                        } else {
                            Log.d(TAG, "onSuccess: No Such user");
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

            String encoded = "";

            if (image != null) {
                //save new image to db - Stack exchange!
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream .toByteArray();
                encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            }

            if (!encoded.isEmpty()) {
                updatedYou.setImage(encoded);
            }

            updateDB(updatedYou);

            Toast.makeText(getApplicationContext(), "Profile successfully updated!",
                    Toast.LENGTH_SHORT).show();

            mHandler.postDelayed(new Runnable() {
                public void run() {
                    finish();
                }
            }, Toast.LENGTH_SHORT + 3000);
        }

        //Stack exchange

    protected void onImageButtonClick (View view) {
            startActivityForResult(new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                //set image button bg to image
                ImageButton profileImage = findViewById(R.id.profileImage);
                profileImage.setBackground(new BitmapDrawable(getResources(), image));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



        protected void onClickCancel(View view) {
            finish();
        }

    private void updateDB(User user) {
        //add to database
        String userId = getIntent().getStringExtra("accountId");

        //clean out what was in the db
        FirebaseFirestore.getInstance().collection("users").document(userId)
                .set(null, SetOptions.merge())
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

        //add new stuff
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
