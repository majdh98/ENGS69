package majd_hamdan.com.easyjob.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.job.Job;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.JobViewHolder> {

    public static class JobViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView street;
        TextView townState;
        TextView desctibtion;
        TextView typePay;
        TextView type;

        JobViewHolder(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.jobCard);
            street = (TextView)itemView.findViewById(R.id.streetText);
            townState = (TextView)itemView.findViewById(R.id.townText);
            desctibtion = itemView.findViewById(R.id.description);
            typePay = (TextView)itemView.findViewById(R.id.typePayText);
            type = itemView.findViewById(R.id.type);
        }

    }

    List<Job> jobs;

    public RVAdapter(List<Job> jobs){
        this.jobs = jobs;
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_job_card_view, viewGroup, false);
        JobViewHolder jvh = new JobViewHolder(v);
        return jvh;
    }

    @Override
    public void onBindViewHolder(JobViewHolder jobViewHolder, int i){
        String[] address_lines = jobs.get(i).address.split(",");
        String[] state = address_lines[2].split(" ");
        jobViewHolder.street.setText(address_lines[0]);
        jobViewHolder.townState.setText(address_lines[1] + ", " + state[1] );
        jobViewHolder.desctibtion.setText("Describtion: " + jobs.get(i).description);
        jobViewHolder.typePay.setText("Job Type: " + jobs.get(i).type);
        jobViewHolder.type.setText("Pay: " + jobs.get(i).hourlyPay + "/hour");
    }

    @Override
    public int getItemCount(){
        return jobs.size();
    }






}
