package majd_hamdan.com.easyjob;

import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.JobViewHolder> {

    public static class JobViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView street;
        TextView townState;
        ImageView jobPhoto;
        TextView typePay;

        JobViewHolder(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.jobCard);
            street = (TextView)itemView.findViewById(R.id.streetText);
            townState = (TextView)itemView.findViewById(R.id.townText);
            jobPhoto = (ImageView)itemView.findViewById(R.id.jobImageView);
            typePay = (TextView)itemView.findViewById(R.id.typePayText);
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
        jobViewHolder.street.setText(jobs.get(i).street);
        jobViewHolder.townState.setText(jobs.get(i).townState);
        jobViewHolder.jobPhoto.setImageResource(jobs.get(i).photoID);
        jobViewHolder.typePay.setText(jobs.get(i).type + " - $" + String.valueOf(jobs.get(i).hourlyPay) + "/hr");
    }

    @Override
    public int getItemCount(){
        return jobs.size();
    }

}
