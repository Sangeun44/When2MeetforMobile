package edu.upenn.cis350.g8.when2meetformobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.Map;

public class JoinedSessionsActivity extends AppCompatActivity {

    public static final int JoinedSessionDisplayActivity_ID = 1;
    private Map<String, String> nameToID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joined_sessions);
        populateMap();
        createButtons();
    }

    public void populateMap() {
        //asynchronously retrieve all documents
        //ApiFuture<QuerySnapshot> future = db.collection("meetings").get();
        //List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        //for (QueryDocumentSnapshot document : documents) {
            //String name = document.getData().get("name");
            //nameToID.put(name, document.getId())
        //}
    }

    public void createButtons() {
        final Context context = this;
        LinearLayout main = findViewById(R.id.mainLinear);
        for (String s: nameToID.keySet()) {
            Button b = new Button(this);
            b.setText(s);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, JoinedSessionDisplayActivity.class);
                    Button btn = findViewById(view.getId());
                    String session = btn.getText().toString();
                    i.putExtra("SESSION", nameToID.get(session));
                    startActivityForResult(i, JoinedSessionDisplayActivity_ID);
                }
            });
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            main.addView(b, lp);
        }
    }

    public void onSessionButtonClick(View v) {
        Intent i = new Intent(this, JoinedSessionDisplayActivity.class);
        Button btn = findViewById(v.getId());
        String session = btn.getText().toString();
        i.putExtra("SESSION", nameToID.get(session));
        startActivityForResult(i, JoinedSessionDisplayActivity_ID);
    }
}
