package majd_hamdan.com.easyjob.job;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

    // to get a job creator's number of that is necessary
    private String creatorNumber;

    String TAG = "mh";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_details);

        // set the app bar color and title
        setTitle("Job Details");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#002171")));

        // used to suppress the keyboard from popping up when code runs
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


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
            findViewById(R.id.past_view).setVisibility(View.GONE);
            findViewById(R.id.created_view).setVisibility(View.GONE);
            // initiate_current_ui();
        }else if(job_code == HistoryFragment.PAST_JOB_KEY){
            findViewById(R.id.past_view).setVisibility(View.VISIBLE);
            findViewById(R.id.currentView).setVisibility(View.GONE);
            findViewById(R.id.created_view).setVisibility(View.GONE);
            initiate_past_ui();
        }else if(job_code == HistoryFragment.CREATED_JOB_KEY){
            findViewById(R.id.created_view).setVisibility(View.VISIBLE);
            findViewById(R.id.past_view).setVisibility(View.GONE);
            findViewById(R.id.current_view).setVisibility(View.GONE);
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
        fetch_user_info(name, job.creator_id);
        location.setText(job.address);

    }

    public void initiate_past_ui(){

    }

    public void initiate_created_ui(){

        EditText job_type;
        EditText job_pay;
        EditText job_description;
        EditText address_field;
        EditText city_field;
        EditText zipcode_field;
        EditText state_field;
        EditText country_field;
        EditText worker;
        job_type = (EditText) findViewById(R.id.jobtypeField);
        job_pay = (EditText) findViewById(R.id.payField);
        job_description = (EditText) findViewById(R.id.descriptionField);
        address_field = (EditText) findViewById(R.id.addressField);
        city_field = (EditText) findViewById(R.id.cityField);
        zipcode_field = (EditText) findViewById(R.id.zipcodeField);
        country_field = (EditText) findViewById(R.id.countryField);
        state_field = (EditText) findViewById(R.id.stateField);
        worker = (EditText) findViewById(R.id.worker);

        job_type.setText(job.type);
        job_description.setText(job.description);
        job_pay.setText(job.hourlyPay);

        String[] add = job.address.split(",");
        String[] state_zip = add[2].split(" ");
        address_field.setText(add[0]);
        city_field.setText(add[1]);
        state_field.setText(state_zip[1]);
        country_field.setText(add[3]);
        zipcode_field.setText(state_zip[2]);



        if(job.isAvaliable){
            job_description.setEnabled(false);
            job_pay.setEnabled(false);
            job_type.setEnabled(false);
            state_field.setEnabled(false);
            address_field.setEnabled(false);
            city_field.setEnabled(false);
            zipcode_field.setEnabled(false);
            country_field.setEnabled(false);
            worker.setVisibility(View.VISIBLE);
            findViewById(R.id.drop_worker).setVisibility(View.VISIBLE);
            fetch_user_info(worker, job.worker_id);

        }




    }

    public void initiate_avaliable_job_ui(){

    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(HistoryFragment.JOB_TAG, job_code);
        outState.putSerializable(HistoryFragment.JOB_KEY, job);
    }

    public void fetch_user_info(TextView view, String creator_id ){

        DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("users");
        Query userQuery = users_ref.child(creator_id);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                creatorNumber = user.phoneNumber;

                if (user != null) {
                    view.setText("Creator Name: " + user.firstName + " " + user.lastName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    //Buttons======================================================================================
    public void onCancelClicked(View view){
        finish();
    }

    public void onContactCreatorClicked(View view){
        // call phone activity to call the creator of the job
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+creatorNumber));
        startActivity(intent);
    }

}
