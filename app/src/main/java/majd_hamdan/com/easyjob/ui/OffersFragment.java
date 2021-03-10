package majd_hamdan.com.easyjob.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import majd_hamdan.com.easyjob.R;

import majd_hamdan.com.easyjob.adapters.GeneralJobCardAdapter;
import majd_hamdan.com.easyjob.authentication.User;
import majd_hamdan.com.easyjob.helper.PermissionUtils;
import majd_hamdan.com.easyjob.job.AddJobActivity;
import majd_hamdan.com.easyjob.job.Job;
import majd_hamdan.com.easyjob.job.JobDetailsActivity;

import static majd_hamdan.com.easyjob.ui.HistoryFragment.AVALIABLE_JOB_KEY;
import static majd_hamdan.com.easyjob.ui.HistoryFragment.JOB_TAG;

public class OffersFragment extends Fragment implements OnMapReadyCallback {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    //Map and location variables
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private DatabaseReference geofire_db;
    private GeoFire geoFire;
    private Location initial_location;


    private RecyclerView view;
    private TextView welcomeMessage;
    private Button viewJobButtons;
    private Button addJob;
    private List<Job> jobs;

    private RadioButton mapToggle;
    private RadioButton listToggle;

    private int items_queried;
    private int items_retrieved;

    private String userFirstName;


    String TAG = "mh";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_offers, container, false);


        // GET UI ELEMENTS

        // welcome message
        welcomeMessage = (TextView)returnView.findViewById(R.id.welcome);

        // fetch user information to update welcome message
        fetch_user_info_for_welcome(welcomeMessage);


        // toggle switch
        mapToggle = (RadioButton)returnView.findViewById(R.id.Maps);
        listToggle = (RadioButton)returnView.findViewById(R.id.offer);


        mapToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listToggle.setSelected(false);
                mapToggle.setSelected(true);
                returnView.findViewById(R.id.list_view).setVisibility(View.GONE);
                returnView.findViewById(R.id.map_view).setVisibility(View.VISIBLE);
            }
        });

        listToggle.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                listToggle.setSelected(true);
                mapToggle.setSelected(false);
                returnView.findViewById(R.id.list_view).setVisibility(View.VISIBLE);
                returnView.findViewById(R.id.map_view).setVisibility(View.GONE);
            }
        });

        welcomeMessage = (TextView)returnView.findViewById(R.id.welcome);
        addJob = (Button) returnView.findViewById(R.id.createJob);
        addJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), AddJobActivity.class));
            }
        });

        // get recycler view
        view = returnView.findViewById(R.id.recycler);
        view.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        view.setLayoutManager(llm);
        items_queried = 0;
        items_retrieved = 0;
        initJobs();

        return returnView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        //sync the google map fragment
        mapFragment.getMapAsync(this);


        geofire_db = FirebaseDatabase.getInstance().getReference().child("geofire");
        geoFire = new GeoFire(geofire_db);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        createLocationRequest();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    initial_location = location;
                    // Update UI with location data
                    // ...
                }
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        //check for permission and enable location layer.
        enableMyLocation();

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            initial_location = location;
                            LatLng latLng = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                            // Logic to handle location object
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            fetch_offers();
                        }
                    }
                });
    }

    //ListView--------------------------------------------------------------------------------------
    private void initJobs(){
        jobs = new ArrayList<>();
    }

    private void initializeAdapter(){
        GeneralJobCardAdapter adapter = new GeneralJobCardAdapter(jobs);
        view.setAdapter(adapter);
        adapter.setOnItemClickListener(new GeneralJobCardAdapter.OnItemClickListener()
        {
            @Override
            public void onMoreDetailsClick(int position) {
                Intent intent = new Intent(getActivity(), JobDetailsActivity.class);
                intent.putExtra(JOB_TAG, AVALIABLE_JOB_KEY);
                startActivity(intent);
            }
        });
    }


    //Database--------------------------------------------------------------------------------------
    public static void fetch_user_info_for_welcome(TextView toSetWelcome){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("users");
        Query userQuery = users_ref.child(userId);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    // get ui element for welcome message and populate with user info
                    toSetWelcome.setText("Hello, " + user.firstName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //fetch offers around default radius of user from firebase
    public void fetch_offers(){
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(initial_location.getLatitude(), initial_location.getLongitude()), 3);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                if(getContext() != null){
                    items_queried++;

                    DatabaseReference offers_ref = FirebaseDatabase.getInstance().getReference("offers");
                    Query offersQuery = offers_ref.child(key);
                    offersQuery.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            items_retrieved++;
                            Job job = dataSnapshot.getValue(Job.class);
                            if(job != null){
                                jobs.add(job);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }


            @Override
            public void onKeyExited(String key) {
                Log.d(TAG, String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d(TAG, String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {

                //once the data has been queried, check if the loaded data count equals the
                //queried data. If not, wait until all data is loaded then initializeAdapter

                if(items_queried == items_retrieved){
                    //populate list
                    initializeAdapter();
                    
                    //populate map
                    populate_map();
                }else{
                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            onGeoQueryReady();
                        }
                    }, 1000);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d(TAG, "There was an error with this query: " + error);
            }
        });

    }
    
    public void populate_map(){
        Log.d(TAG, "populate_map: ");
        for(int i = 0; i<jobs.size(); i++){
            LatLng job_location = getLocationFromAddress(jobs.get(i).address);
            Marker marker = map.addMarker(
                    new MarkerOptions()
                            .position(job_location)
                            .title(jobs.get(i).type)
                            .snippet("$"+jobs.get(i).hourlyPay));
            marker.showInfoWindow();
        }
        
    }

    public LatLng getLocationFromAddress(String address){
        try {
            Geocoder selected_place_geocoder = new Geocoder(getActivity());
            List<Address> addresses;

            addresses = selected_place_geocoder.getFromLocationName(address, 1);

            if (addresses == null) {
                return null;
            } else {
                Address location = addresses.get(0);
                LatLng job_location = new LatLng(location.getLatitude(), location.getLongitude());
                return job_location;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //location functions----------------------------------------------------------------------------

    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        //update location every 10 sec
        locationRequest.setInterval(10000);
        //get as accurate as possible location (enable the use of gps)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }


    //Permession------------------------------------------------------------------------------------

    //Check if location permissions are granted.Enables the My Location layer if the fine location
    //permission has been granted. Else, ask for permission.
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
//            // Display a dialog with rationale and ask for permission
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.permission_rationale_location)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // After click on Ok, request the permission.
                            // Permission to access the location is missing. Show rationale and request permission
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, null)
                    .show();

        }
    }

    //handle the result of requestPermission() called in enablemyLocation()
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d("mh", "onRequestPermissionsResult: ");
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Enable the my location layer if the permission has been granted.
                enableMyLocation();

            } else {
                // Permission was denied. Display an error message
                // Display the missing permission error dialog when the fragments resume.
                PermissionUtils.PermissionDeniedDialog
                        .newInstance(true).show(getChildFragmentManager(), "dialog");
            }
        }
    }

}
