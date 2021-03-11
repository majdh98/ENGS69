package majd_hamdan.com.easyjob;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

import majd_hamdan.com.easyjob.adapters.CreatedJobCardAdapter;
import majd_hamdan.com.easyjob.job.Job;

public class ViewJobsActivity extends AppCompatActivity {

    // todo: this is a test, the job entries should come form the db
    private List<Job> jobs;         // list of jobs available
    RecyclerView view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_jobs);

        // hide the action bar at the top
        getSupportActionBar().hide();

        // get recycler view
        view = findViewById(R.id.recyclerJobs);
        view.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        view.setLayoutManager(llm);

        initJobs();  // call the method to initialize the jobs
        initializeAdapter();   // initialize the adapter to display the jobs
    }


    // todo: this is only a test - read data from db instead
    private void initJobs(){
        jobs = new ArrayList<>();    // create the list
//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
//        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
    }

    private void initializeAdapter(){
        CreatedJobCardAdapter adapter = new CreatedJobCardAdapter(jobs);  // initialize and create the adapter to display
        view.setAdapter(adapter);
    }
}