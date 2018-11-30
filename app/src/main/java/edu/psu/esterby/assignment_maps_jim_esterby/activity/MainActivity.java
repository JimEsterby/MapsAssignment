package edu.psu.esterby.assignment_maps_jim_esterby.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.psu.esterby.assignment_maps_jim_esterby.R;
import edu.psu.esterby.assignment_maps_jim_esterby.broadcast.MapBroadcastReceiver;
import edu.psu.esterby.assignment_maps_jim_esterby.model.DataItem;
import edu.psu.esterby.assignment_maps_jim_esterby.model.MapLocation;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FirebaseDatabase database;
    private DatabaseReference database_root;
    private static final String TAG = "MainActivity";
    private static final String LATITUDE = "Latitude";
    private static final String LONGITUDE = "Longitude";
    private static final String LOCATION = "Location";

    private final String LOG_MAP = "GOOGLE_MAPS";

    private DataItem passed_item;  // used to hold input data

    private GoogleMap AppGoogleMap;

    // Google Maps
    private LatLng currentLatLng;
    private SupportMapFragment mapFragment;
    private Marker currentMapMarker;

    // TODO: Broadcast receivers
    // Broadcast Receiver
    private IntentFilter intentFilter = null;
    private MapBroadcastReceiver broadcastReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        passed_item = (DataItem) intent.getSerializableExtra("MAP_LOCATION");

        firebaseLoadData();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // set up broadcast receiver

        // long string argument to this function is in AndroidManifest.xml
        intentFilter = new IntentFilter("edu.psu.esterby.assignment_maps_jim_esterby.action.NEW_MAP_LOCATION_BROADCAST");
        broadcastReceiver = new MapBroadcastReceiver();
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(broadcastReceiver);
        super.onStop();
    }

    private void firebaseLoadData() {  // Test Firebase connection
        database = FirebaseDatabase.getInstance();
        database_root = database.getReference();  // root node of the database

        database_root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // iterate thru the database
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    // each item should have 3 fields: Location, Latitude, and Longitude
                    // TODO: code to deal with malformed database items
                    Log.d(LOCATION, (String) item.child(LOCATION).getValue());
                    Log.d(LATITUDE, "Value: " + item.child(LATITUDE).getValue());
                    Log.d(LONGITUDE, "Value: " + item.child(LONGITUDE).getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Failed to read value(s)
                Log.d(TAG, "Failed to read value");
            }
        });
    }

    // Step 1 - Set up initial configuration for the map.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        AppGoogleMap = googleMap;

        //       Intent intent = getIntent();
        //      String location = intent.getStringExtra("LOCATION");
        String location = passed_item.getLocation();
        Double latitude = passed_item.getLatitude();
        Double longitude = passed_item.getLongitude();

        // Let's try Dodge City, Kansas...
        //Double latitude = intent.getDoubleExtra("LATITUDE", 37.765469);
        //Double longitude = intent.getDoubleExtra("LONGITUDE", -100.015167);

        // Set initial positioning (Latitude / longitude)
        currentLatLng = new LatLng(latitude, longitude);

        googleMap.addMarker(new MarkerOptions()
                .position(currentLatLng)
                .title(location)
        );

        createCustomMapMarkers(googleMap,
                new LatLng(passed_item.getLatitude(), passed_item.getLongitude()),
                passed_item.getLocation(),
                "");

        // Set the camera focus on the current LatLtn object, and other map properties.
        mapCameraConfiguration(googleMap);
        useMapClickListener(googleMap);
        useMarkerClickListener(googleMap);
        useOnMarkerDragListener(googleMap);
        usePoiClickListener(googleMap);

        // Add Firebase markers
        loadData();
    }

    /**
     * Step 2 - Set a few properties for the map when it is ready to be displayed.
     * Zoom position varies from 2 to 21.
     * Camera position implements a builder pattern, which allows to customize the view.
     * Bearing - screen rotation ( the angle needs to be defined ).
     * Tilt - screen inclination ( the angle needs to be defined ).
     **/
    private void mapCameraConfiguration(GoogleMap googleMap) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLatLng)
                .zoom(14)
                .bearing(0)
                .build();

        // Camera that makes reference to the maps view
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        googleMap.animateCamera(cameraUpdate, 3000, new GoogleMap.CancelableCallback() {

            @Override
            public void onFinish() {
                Log.i(LOG_MAP, "googleMap.animateCamera:onFinish is active");
            }

            @Override
            public void onCancel() {
                Log.i(LOG_MAP, "googleMap.animateCamera:onCancel is active");
            }
        });
    }

    /**
     * Step 3 - Reusable code
     * This method is called every time the use wants to place a new marker on the map.
     **/
    private void createCustomMapMarkers(GoogleMap googleMap, LatLng latlng, String title, String snippet) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlng) // coordinates
                .title(title) // location name
                .snippet(snippet); // location description

        // Update the global variable (currentMapMarker)
        currentMapMarker = googleMap.addMarker(markerOptions);
    }

    // Step 4 - Define a new marker based on a Map click (uses onMapClickListener)
    private void useMapClickListener(final GoogleMap googleMap) {

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latltn) {
                Log.i(LOG_MAP, "setOnMapClickListener");

                if (currentMapMarker != null) {
                    // Remove current marker from the map.
                    currentMapMarker.remove();
                }
                // The current marker is updated with the new position based on the click.
                createCustomMapMarkers(
                        googleMap,
                        new LatLng(latltn.latitude, latltn.longitude),
                        "New Marker",
                        "Listener onMapClick - new position"
                                + "lat: " + latltn.latitude
                                + " lng: " + latltn.longitude);
            }
        });
    }

    // Step 5 - Use OnMarkerClickListener for displaying information about the MapLocation
    private void useMarkerClickListener(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            // If FALSE, when the map should have the standard behavior (based on the android framework)
            // When the marker is clicked, it wil focus / centralize on the specific point on the map
            // and show the InfoWindow. IF TRUE, a new behavior needs to be specified in the source code.
            // However, you are not required to change the behavior for this method.
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.i(LOG_MAP, "setOnMarkerClickListener");

                return false;
            }
        });
    }

    // implementations of extra listeners
    private void usePoiClickListener(GoogleMap googleMap) {
        googleMap.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Log.i(LOG_MAP, poi.placeId);
            }
        });
    }

    private void useOnMarkerDragListener(GoogleMap googleMap) {
        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker m) {

            }

            @Override
            public void onMarkerDragEnd(Marker m) {
                Log.i(LOG_MAP, "onMarkerDragListener");

            }

            @Override
            public void onMarkerDragStart(Marker m) {

            }
        });
    }

    private void triggerBroadcastMessageFromFirebase(DataItem item) {
        Intent send = new Intent(this, MapBroadcastReceiver.class);
        send.putExtra("MAP_LOCATION", item);
    }

    public void createMarkersFromFirebase(GoogleMap googleMap){
    }

    private void loadData(){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference();

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                DataItem item = dataSnapshot.getValue(DataItem.class);

                // Send notification
                triggerBroadcastMessageFromFirebase(item);

                // Create marker for item on the map
                createCustomMapMarkers(AppGoogleMap,
                        new LatLng(item.getLatitude(), item.getLongitude()),
                        item.getLocation(), "");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
