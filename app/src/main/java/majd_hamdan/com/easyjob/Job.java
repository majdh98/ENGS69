package majd_hamdan.com.easyjob;

public class Job {

    String street;
    String townState;
    int photoID;
    String type;
    Double hourlyPay;

    public Job(String str, String twnSte, int pID, String ty, Double pay){
        this.street = str;
        this.townState = twnSte;
        this.photoID = pID;
        this.type = ty;
        this.hourlyPay = pay;
    }

}

