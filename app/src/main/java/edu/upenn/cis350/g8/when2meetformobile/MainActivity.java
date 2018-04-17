package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int HomeActivity_ID = 1;
    private static final String TAG = "When2MeetMain";

    private static final int RC_SIGN_IN = 123;
    private GoogleSignInClient mGoogleSignInClient;
    private String account_Num = "01";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        updateUI(GoogleSignIn.getLastSignedInAccount(this));

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button) {
            // sign in user
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            signInIntent.putExtra("accountNum", account_Num);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else if (view.getId() == R.id.sign_out_button) {
            // sign out the user
            mGoogleSignInClient.signOut()
                    .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateUI(null);
                        }
                    });
        } else if (view.getId() == R.id.home_page_button) {
            // go to the home page
            GoogleSignInAccount user = GoogleSignIn.getLastSignedInAccount(this);
            Intent i = new Intent(this, HomeScreenActivity.class);
            i.putExtra("accountKey", user.getId());
            startActivityForResult(i, HomeActivity_ID);

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // sign in the user
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                updateDB(account);
                updateUI(account);
                Log.d(TAG, "signInResult:success");
            } catch (ApiException e) {
                // firebase connection issue
                Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            }
        } else if (requestCode == HomeActivity_ID) {
            // navigate to the home page
            if (data.getBooleanExtra("logout", false)) {
                onClick(findViewById(R.id.sign_out_button));
            }
        }
    }

    /**
     * If the user already exists, will add the user to the database. Otherwise,
     * this method updates the data in the database with any altered user information.
     *
     * @param account user account to add to the database
     */
    private void updateDB(GoogleSignInAccount account) {
        Map<String, Object> userData = new HashMap<>();
        userData.put("name", account.getDisplayName());
        String name = account.getDisplayName();
        userData.put("userName", name);
        userData.put("phoneNumber", "");
        userData.put("description", "");
        //add default image for user
        Bitmap img = BitmapFactory.decodeResource(getResources(),
                R.drawable.image_preview);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
        userData.put("image", encoded);
        
        FirebaseFirestore.getInstance().collection("users").document(account.getId())
            .set(userData, SetOptions.merge())
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

    /**
     * If a user is logged in, display the log out button, otherwise display the
     * log in button.
     *
     * @param account user account to update UI with, {@code null} if no such user
     */
    private void updateUI(GoogleSignInAccount account) {
        findViewById(R.id.sign_in_button)
                .setVisibility(account == null ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.sign_out_button)
                .setVisibility(account == null ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.home_page_button)
                .setVisibility(account == null ? View.INVISIBLE : View.VISIBLE);
    }
}
