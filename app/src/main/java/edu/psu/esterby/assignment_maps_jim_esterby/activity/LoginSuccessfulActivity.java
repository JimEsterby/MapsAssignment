package edu.psu.esterby.assignment_maps_jim_esterby.activity;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.psu.esterby.assignment_maps_jim_esterby.R;

public class LoginSuccessfulActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference database_root;
    private static final String TAG = "LoginSuccessfulActivity";
    private static final String Latitude = "Latitude";
    private static final String Longitude = "Longitude";
    private static final String Location = "Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_successful);

        // Recover the data from the other activity passed through the intent.
        String user_email = getIntent().getStringExtra("USER_EMAIL");
        String provider = getIntent().getStringExtra("PROVIDER_ID");

        Log.d("PARAMS", user_email);
        Log.d("PARAMS", provider);

        firebaseLoadData();
    }

    private void firebaseLoadData() {
        database = FirebaseDatabase.getInstance();
        database_root = database.getReference();  // root node of the database

        database_root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // iterate thru the database
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    // each item should have 3 fields: Location, Latitude, and Longitude
                    // TODO: code to deal with malformed database items
                    Log.d(Location, (String)item.child(Location).getValue());
                    Log.d(Latitude, "Value: " + item.child(Latitude).getValue());
                    Log.d(Longitude, "Value: " + item.child(Longitude).getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value(s)
                Log.d(TAG, "Failed to read value");
            }
        });
    }
}
