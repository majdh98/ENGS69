package majd_hamdan.com.easyjob.job;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import majd_hamdan.com.easyjob.R;
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
}
