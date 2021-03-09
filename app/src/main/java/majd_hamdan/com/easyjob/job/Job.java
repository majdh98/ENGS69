package majd_hamdan.com.easyjob.job;

import android.location.Location;



public class Job {

    //must be decalred public for firebase
    public String location_key;
    public String address;
    public String description;
    public String type;
    public String hourlyPay;
    public boolean isDone_worker;//confirmation from the worker
    public boolean isDone_creator;//confirmation from the creator
    public boolean isAvaliable;
    public String worker_feedback;
    public String creator_feedback;
    public String creator_id;
    public String worker_id;

    //required for db
    public Job() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Job(String description, String type, String location_key, String address, String hourlyPay, String creator_id){
        this.description = description;
        this.type = type;
        this.location_key = location_key;
        this.address = address;
        this.hourlyPay = hourlyPay;
        this.creator_id = creator_id;
        //default values
        isDone_creator = false;
        isDone_worker = false;
        isAvaliable = false;
        worker_feedback = "";
        creator_feedback = "";
        worker_id = "";

    }

}

