package majd_hamdan.com.easyjob;

public class Job {

    public String street;
    public String townState;
    public int photoID;
    public String type;
    public Double hourlyPay;

    //required for db
    public Job() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Job(String str, String twnSte, int pID, String ty, Double pay){
        this.street = str;
        this.townState = twnSte;
        this.photoID = pID;
        this.type = ty;
        this.hourlyPay = pay;
    }

}

