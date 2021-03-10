package majd_hamdan.com.easyjob.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ButtonBarLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.job.Job;

public class GeneralJobCardAdapter extends RecyclerView.Adapter<GeneralJobCardAdapter.JobViewHolder> {

    // setup to handle the button press
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onMoreDetailsClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView poster;
        TextView address;
        TextView type;
        TextView hourlyPay;
        Button moreDetails;

        JobViewHolder(View itemView, final OnItemClickListener listener){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.jobCard);
            poster = (TextView)itemView.findViewById(R.id.jobPoster);
            address = (TextView)itemView.findViewById(R.id.jobAddress);
            hourlyPay = (TextView)itemView.findViewById(R.id.jobHourlyPay);
            type = itemView.findViewById(R.id.jobType);
            moreDetails = (Button)itemView.findViewById(R.id.jobMoreDetails);

            // handle button press
            moreDetails.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // todo: implement function to handle the press
                }
            });
        }

    }

    List<Job> jobs;

    public GeneralJobCardAdapter(List<Job> jobs){
        this.jobs = jobs;
    }

    @Override
    public JobViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.general_job_card_view, viewGroup, false);
        JobViewHolder jvh = new JobViewHolder(v, mListener);
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
