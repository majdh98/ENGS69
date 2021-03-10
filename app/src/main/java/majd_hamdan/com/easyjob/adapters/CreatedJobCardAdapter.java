package majd_hamdan.com.easyjob.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.job.Job;

public class CreatedJobCardAdapter extends RecyclerView.Adapter<CreatedJobCardAdapter.JobControl> {

    // setup to handle the button press
    private OnItemClickListener mListener;

    public interface OnItemClickListener{
        void onDetailsClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    public static class JobControl extends RecyclerView.ViewHolder{

        CardView cv;
        TextView jobTitle;
        TextView address;
        TextView jobTaken;
        TextView jobTakenBy;
        Button deleteBtn;
        Button detailsBtn;

        JobControl(View itemView, final OnItemClickListener listener){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.jobCardControl);
            jobTitle = (TextView)itemView.findViewById(R.id.createdJobType);
            address = (TextView)itemView.findViewById(R.id.createdAddressID);
            jobTaken = (TextView)itemView.findViewById(R.id.jobTakenBoolID);
            jobTakenBy = (TextView)itemView.findViewById(R.id.createdTakenBy);
            deleteBtn = (Button)itemView.findViewById(R.id.deleteButton);
            detailsBtn = (Button)itemView.findViewById(R.id.detailsButton);

            // handle the button presses
            deleteBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    // todo: implement function to handle the press
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            detailsBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDetailsClick(position);
                        }
                    }
                }
            });
        }
    }

    List<Job> jobs;

    public CreatedJobCardAdapter(List<Job> jobs){
        this.jobs = jobs;
    }

    @Override
    public JobControl onCreateViewHolder(ViewGroup group, int i){
        View v = LayoutInflater.from(group.getContext()).inflate(R.layout.created_job_card_view, group, false);
        JobControl jvh = new JobControl(v, mListener);
        return jvh;
    }

    @Override
    public void onBindViewHolder(JobControl holder, int i){
        // todo: get job details from the db
        String[] address_lines = jobs.get(i).address.split(",");
        String[] state = address_lines[2].split(" ");

        // todo: build the card from the details
        holder.jobTitle.setText(jobs.get(i).type);
        holder.address.setText(address_lines[0] + ", " + address_lines[1] + ", " + state[1]);

        // check whether or not the job has been taken
        if(!jobs.get(i).isAvaliable){
            // if the job has not yet been taken
            holder.jobTaken.setText("Not yet");
            holder.jobTakenBy.setVisibility(View.GONE);
        } else {
            // if the job has been taken
            holder.jobTaken.setText("Yes");
            holder.jobTakenBy.setVisibility(View.VISIBLE);

            // todo: set the text to the person that took the job
            holder.jobTakenBy.setText(jobs.get(i).worker_id);
        }
    }

    @Override
    public int getItemCount(){
        return jobs.size();
    }

}
