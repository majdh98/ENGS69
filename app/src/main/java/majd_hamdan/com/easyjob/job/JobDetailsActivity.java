package majd_hamdan.com.easyjob.job;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.authentication.User;
import majd_hamdan.com.easyjob.ui.HistoryFragment;


public class JobDetailsActivity extends AppCompatActivity {

    private int job_code;//tells which view we are dealing with(past, current or created)

    private Job job;

    String TAG = "mh";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);


        //initiate view ui


        if(savedInstanceState != null){
            job_code = savedInstanceState.getInt(HistoryFragment.JOB_TAG);
            job = (Job) savedInstanceState.getSerializable(HistoryFragment.JOB_KEY);
        }else{
            job_code = getIntent().getIntExtra(HistoryFragment.JOB_TAG, 0);
            job = (Job) getIntent().getSerializableExtra(HistoryFragment.JOB_KEY);
        }

        if(job_code == HistoryFragment.CURRENT_JOB_KEY){
            findViewById(R.id.current_view).setVisibility(View.VISIBLE);
            initiate_current_ui();
        }else if(job_code == HistoryFragment.PAST_JOB_KEY){
            findViewById(R.id.past_view).setVisibility(View.VISIBLE);
            initiate_past_ui();
        }else if(job_code == HistoryFragment.CREATED_JOB_KEY){
            findViewById(R.id.created_view).setVisibility(View.VISIBLE);
            initiate_created_ui();
        }else{
            findViewById(R.id.avaliable_job_view).setVisibility(View.VISIBLE);
            initiate_avaliable_job_ui();
        }
    }


    public void initiate_current_ui(){

        TextView type = findViewById(R.id.job_type);
        TextView description = findViewById(R.id.description);
        TextView job_pay = findViewById(R.id.job_pay);
        TextView name = findViewById(R.id.creator_name);
        TextView location = findViewById(R.id.location);
        type.setText("Job Type: " + job.type);
        description.setText("Job Desctribtion: " + job.description);
        job_pay.setText("$" + job.hourlyPay);
        fetch_creator_info(name, job.creator_id);
        location.setText(job.address);

    }

    public void initiate_past_ui(){

    }

    public void initiate_created_ui(){

    }

    public void initiate_avaliable_job_ui(){

    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(HistoryFragment.JOB_TAG, job_code);
        outState.putSerializable(HistoryFragment.JOB_KEY, job);
    }

    public void fetch_creator_info(TextView view, String creator_id ){

        DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("users");
        Query userQuery = users_ref.child(creator_id);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    view.setText("Creator Name: " + user.firstName + " " + user.lastName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
