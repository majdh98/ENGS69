package majd_hamdan.com.easyjob.ui;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.geofire.GeoFire;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import majd_hamdan.com.easyjob.adapters.GeneralJobCardAdapter;
import majd_hamdan.com.easyjob.job.Job;
import majd_hamdan.com.easyjob.R;

public class HistoryFragment extends Fragment {

    private String TAG = "mh";

    private RecyclerView currentView;
    private RecyclerView pastView;
    private RecyclerView createdView;
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

    // toggling between views
    RadioButton current;
    RadioButton created;
    RadioButton past;

    // todo: this is a test, the job entries should come form the db
    private List<Job> pastJobs;
    private List<Job> currentJobs;
    private List<Job> createdJobs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get view
        View returnView = inflater.inflate(R.layout.fragment_history, container, false);

        // get ui elements
        welcomeMessage = (TextView)returnView.findViewById(R.id.welcome);
        current = (RadioButton)returnView.findViewById(R.id.Maps);
        created = (RadioButton)returnView.findViewById(R.id.created);
        past = (RadioButton)returnView.findViewById(R.id.past);

        // set on click listeners
        current.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                current.setSelected(true);
                created.setSelected(false);
                past.setSelected(false);
                returnView.findViewById(R.id.currentRecycler).setVisibility(View.VISIBLE);
                returnView.findViewById(R.id.createdRecycler).setVisibility(View.GONE);
                returnView.findViewById(R.id.pastRecycler).setVisibility(View.GONE);
            }
        });

        created.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                created.setSelected(true);
                current.setSelected(false);
                past.setSelected(false);
                returnView.findViewById(R.id.createdRecycler).setVisibility(View.VISIBLE);
                returnView.findViewById(R.id.currentRecycler).setVisibility(View.GONE);
                returnView.findViewById(R.id.pastRecycler).setVisibility(View.GONE);
            }
        });

        past.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                past.setSelected(true);
                current.setSelected(false);
                created.setSelected(false);
                returnView.findViewById(R.id.pastRecycler).setVisibility(View.VISIBLE);
                returnView.findViewById(R.id.currentRecycler).setVisibility(View.GONE);
                returnView.findViewById(R.id.createdRecycler).setVisibility(View.GONE);
            }
        });


        // set recycler views

        // current
        currentView = returnView.findViewById(R.id.currentRecycler);
        currentView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        currentView.setLayoutManager(llm);

        // created
        createdView = returnView.findViewById(R.id.createdRecycler);
        createdView.setHasFixedSize(true);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        createdView.setLayoutManager(llm2);

        // past
        pastView = returnView.findViewById(R.id.pastRecycler);
        pastView.setHasFixedSize(true);
        LinearLayoutManager llm3 = new LinearLayoutManager(getContext());
        pastView.setLayoutManager(llm3);


        geofire_db = FirebaseDatabase.getInstance().getReference().child("geofire");
        geoFire = new GeoFire(geofire_db);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        user_location = new Location(LocationManager.GPS_PROVIDER);

        items_queried = 0;
        items_retrieved = 0;


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();

        // fetch current, past and created jobs
        initJobs();

        return returnView;
    }

    private void initJobs(){
        pastJobs = new ArrayList<>();
        createdJobs = new ArrayList<>();
        currentJobs = new ArrayList<>();

        fetch_old_jobs();
        // todo: fetch past and created jobs
    }

    private void initializePastAdapter(){
        GeneralJobCardAdapter adapter = new GeneralJobCardAdapter(pastJobs);
        currentView.setAdapter(adapter);
    }

    //gets user location then fetch jobs
    @SuppressLint("MissingPermission")
    public void fetch_old_jobs(){
        fetch();
    }

    //fetch offers around default radius of user from firebase
    public void fetch(){

        DatabaseReference offers_ref = FirebaseDatabase.getInstance().getReference("users");
        Query offersQuery = offers_ref.child(userId).child("offers_created");
        offersQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String prevChildKey) {
                items_retrieved++;
                Job job = dataSnapshot.getValue(Job.class);
                if(job != null){
                    // todo: like past jobs are fetched here,
                    // todo: fetch current and created jobs
                    // todo: note that created jobs uses the normal adapter, i.e. GeneralJobCardAdapter
                    // todo: same as past, just include feedback

                    pastJobs.add(job);
                    initializePastAdapter();
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
