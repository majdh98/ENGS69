package majd_hamdan.com.easyjob.ui;

import android.content.Intent;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

import majd_hamdan.com.easyjob.Job;
import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.RVAdapter;
import majd_hamdan.com.easyjob.ViewJobsActivity;
import majd_hamdan.com.easyjob.job.AddJobActivity;

public class HistoryFragment extends Fragment {

    RecyclerView view;
    TextView welcomeMessage;
    Button viewJobButtons;
    Button addJob;

    private String fragment_tag = "tag";

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

        initJobs();
        initializeAdapter();

        return returnView;
    }


    // todo: this is only a test - read data from db instead
    private void initJobs(){
        jobs = new ArrayList<>();
        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
        jobs.add(new Job("43 Main Street", "Hanover, NH", R.drawable.ic_launcher_background, "Mow Lawn", 15.0));
    }

    private void initializeAdapter(){
        RVAdapter adapter = new RVAdapter(jobs);
        view.setAdapter(adapter);
    }

}
