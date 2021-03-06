package majd_hamdan.com.easyjob;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import majd_hamdan.com.easyjob.job.Job;

public class RvAdapterControl extends RecyclerView.Adapter<RvAdapterControl.JobControl> {

    public static class JobControl extends RecyclerView.ViewHolder{

        CardView cv;
        TextView street;
        TextView townState;
        ImageView jobPhoto;
        TextView typePay;
        Button dropBtn;

        JobControl(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.jobCardControl);
            street = (TextView)itemView.findViewById(R.id.streetTextControl);
            townState = (TextView)itemView.findViewById(R.id.townTextControl);
            jobPhoto = (ImageView)itemView.findViewById(R.id.jobImageViewControl);
            typePay = (TextView)itemView.findViewById(R.id.typePayTextControl);
            dropBtn = (Button)itemView.findViewById(R.id.buttonJobsControl);
            dropBtn.setBackgroundColor(Color.RED);
        }
    }

    List<Job> jobs;

    public RvAdapterControl(List<Job> jobs){
        this.jobs = jobs;
    }

    @Override
    public JobControl onCreateViewHolder(ViewGroup group, int i){
        View v = LayoutInflater.from(group.getContext()).inflate(R.layout.job_control_card_view, group, false);
        JobControl jvh = new JobControl(v);
        return jvh;
    }

    @Override
    public void onBindViewHolder(JobControl holder, int i){
        holder.street.setText("ssssssssss");
        holder.townState.setText("ssssssssss");
        holder.jobPhoto.setImageResource(0);
        holder.typePay.setText(jobs.get(i).type + " - $" + String.valueOf(jobs.get(i).hourlyPay) + "/hr");
    }

    @Override
    public int getItemCount(){
        return jobs.size();
    }

}
