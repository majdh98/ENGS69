package majd_hamdan.com.easyjob.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import majd_hamdan.com.easyjob.job.Job;
import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.RVAdapter;
import majd_hamdan.com.easyjob.ViewJobsActivity;
import majd_hamdan.com.easyjob.job.AddJobActivity;

public class HistoryFragment extends Fragment {

    private String TAG = "mh";

    private RecyclerView view;
    private TextView welcomeMessage;
    private Button viewJobButtons;
    private Button addJob;

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference geofire_db;
    private GeoFire geoFire;


    private Location user_location;
    private String query_radius;




    // todo: this is a test, the job entries should come form the db
    private List<Job> jobs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get view
        View returnView = inflater.inflate(R.layout.fragment_history, container, false);



        // get ui elements
        welcomeMessage = (TextView)returnView.findViewById(R.id.welcome);
        viewJobButtons = (Button)returnView.findViewById(R.id.viewJobs);
        viewJobButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ViewJobsActivity.class));
            }
        });
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

        geofire_db = FirebaseDatabase.getInstance().getReference().child("geofire");
        geoFire = new GeoFire(geofire_db);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        user_location = new Location(LocationManager.GPS_PROVIDER);

        initJobs();
        initializeAdapter();

        return returnView;
    }


    // todo: this is only a test - read data from db instead
    private void initJobs(){
        jobs = new ArrayList<>();
        fetch_jobs();


//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(jobs);
        view.setAdapter(adapter);
    }


    @SuppressLint("MissingPermission")
    public void get_user_location(){
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            user_location = location;
                        }
                    }
                });
    }

    public void fetch_jobs(){
        Log.d(TAG, "fetch: ");

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(user_location.getLatitude(), user_location.getLongitude()), 3);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                Log.d(TAG, "onKeyEntered: " + String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));

                DatabaseReference offers_ref = FirebaseDatabase.getInstance().getReference("offers");
                Query offersQuery = offers_ref.child(key);
                offersQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
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


            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

    }

}
