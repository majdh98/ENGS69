package majd_hamdan.com.easyjob.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.ButtonBarLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.authentication.User;
import majd_hamdan.com.easyjob.job.Job;
import majd_hamdan.com.easyjob.job.JobDetailsActivity;

public class GeneralJobCardAdapter extends RecyclerView.Adapter<GeneralJobCardAdapter.JobViewHolder> {


    private Context context;
    // setup to handle the button press
    private OnItemClickListener mListener;


    public interface OnItemClickListener{
        void onMoreDetailsClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){

        mListener = listener;

    }

    public static class JobViewHolder extends RecyclerView.ViewHolder{

        // references for the job view
        CardView cv;
        TextView poster;
        TextView address;
        TextView type;
        TextView hourlyPay;
        Button moreDetails;

        JobViewHolder(View itemView, final OnItemClickListener listener){

            super(itemView);
            // initialize the views for the job holder UI

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
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onMoreDetailsClick(position);
                        }
                    }
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

        // set the poster's name
        fetch_creator_detail(jobs.get(i).creator_id, jobViewHolder.poster);
        jobViewHolder.address.setText(address_lines[0] + ", " + address_lines[1] + ", " + state[1]);
        jobViewHolder.type.setText("Job Type: " + jobs.get(i).type);
        jobViewHolder.hourlyPay.setText("Pay: $" + jobs.get(i).hourlyPay);
    }

    @Override
    public int getItemCount(){
        return jobs.size();
    }

    // HELPER METHOD =====================================================
    // Get the name of the job creator
    public static void fetch_creator_detail(String creatorID, TextView posterName){
        // find the job creator's information

        DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("users");
        Query userQuery = users_ref.child(creatorID);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                   posterName.setText("Creator: " + user.firstName + " " + user.lastName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }






}
