package majd_hamdan.com.easyjob.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import majd_hamdan.com.easyjob.job.Job;
import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.adapters.RVAdapter;
import majd_hamdan.com.easyjob.ViewJobsActivity;
import majd_hamdan.com.easyjob.job.AddJobActivity;

public class HistoryFragment extends Fragment {

    private String TAG = "mh";

    private RecyclerView view;
    private TextView welcomeMessage;
    private Button viewJobButtons;
    private Button addJob;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;

    private FusedLocationProviderClient fusedLocationClient;
    private DatabaseReference geofire_db;
    private GeoFire geoFire;


    private Location user_location;
    private String query_radius;
    private int items_queried;
    private int items_retrieved;




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



        // get recycler view
        view = returnView.findViewById(R.id.recycler);
        view.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        view.setLayoutManager(llm);

        geofire_db = FirebaseDatabase.getInstance().getReference().child("geofire");
        geoFire = new GeoFire(geofire_db);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        user_location = new Location(LocationManager.GPS_PROVIDER);

        items_queried = 0;
        items_retrieved = 0;


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        initJobs();


        return returnView;
    }



    private void initJobs(){
        jobs = new ArrayList<>();
        fetch_old_jobs();

    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(jobs);
        view.setAdapter(adapter);
    }

    //gets user location then fetch jobs
    @SuppressLint("MissingPermission")
    public void fetch_old_jobs(){
        fetch();
    }

    //fetch offers around default radius of user from firebase
    public void fetch(){

        DatabaseReference offers_ref = FirebaseDatabase.getInstance().getReference("users");
        Log.d(TAG, "fetch: " + userId);
        Query offersQuery = offers_ref.child(userId).child("offers_created");
        offersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                items_retrieved++;
                Job job = dataSnapshot.getValue(Job.class);
                if(job != null){
                    jobs.add(job);
                    initializeAdapter();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

    }

}
