package majd_hamdan.com.easyjob.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.job.Job;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.JobViewHolder> {

    public static class JobViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView poster;
        TextView address;
        TextView type;
        TextView hourlyPay;

        JobViewHolder(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.jobCard);
            poster = (TextView)itemView.findViewById(R.id.jobPoster);
            address = (TextView)itemView.findViewById(R.id.jobAddress);
            hourlyPay = (TextView)itemView.findViewById(R.id.jobHourlyPay);
            type = itemView.findViewById(R.id.jobType);
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
        // get address details from the job object
        String[] address_lines = jobs.get(i).address.split(",");
        String[] state = address_lines[2].split(" ");

        // build the card from the details
        jobViewHolder.poster.setText(jobs.get(i).creator_id);
        jobViewHolder.address.setText(address_lines[0] + ", " + address_lines[1] + ", " + state[1]);
        jobViewHolder.type.setText("Job Type: " + jobs.get(i).type);
        jobViewHolder.hourlyPay.setText("Pay: $" + jobs.get(i).hourlyPay + "/hour");
    }

    @Override
    public int getItemCount(){
        return jobs.size();
    }






}
