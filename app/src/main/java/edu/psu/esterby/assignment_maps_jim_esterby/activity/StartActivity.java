package edu.psu.esterby.assignment_maps_jim_esterby.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;

import edu.psu.esterby.assignment_maps_jim_esterby.R;
import edu.psu.esterby.assignment_maps_jim_esterby.broadcast.MapBroadcastReceiver;
import edu.psu.esterby.assignment_maps_jim_esterby.model.DataItem;

public class StartActivity extends AppCompatActivity {

    Button mNavigate;
    EditText mLatitude;
    EditText mLongitude;
    EditText mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mNavigate = (Button)findViewById(R.id.buttonNavigate);
        mLatitude = (EditText)findViewById(R.id.editTextLatitude);
        mLongitude = (EditText)findViewById(R.id.editTextLongitude);
        mLocation = (EditText)findViewById(R.id.editTextLocation);

        mNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);

                Double dLatitude = Double.parseDouble(mLatitude.getText().toString());
                Double dLongitude = Double.parseDouble(mLongitude.getText().toString());
                String Location = mLocation.getText().toString();

                Serializable item = new DataItem(dLatitude, Location, dLongitude);
                intent.putExtra("MAP_LOCATION", item);

                // explicit intent
                Intent send = new Intent(StartActivity.this, MapBroadcastReceiver.class);
                send.putExtra("MAP_LOCATION", item);

                startActivity(intent);
            }
        });
    }
}
