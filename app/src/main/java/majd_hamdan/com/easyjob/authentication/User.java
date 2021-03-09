package majd_hamdan.com.easyjob.authentication;

public class User {
    public String id;
    public String firstName;
    public String lastName;
    public String phoneNumber;
    public String email;
    public Double rating;


    //required for db
    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String id, String firstName, String lastName, String phoneNumber, String email){

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        //default
        rating = 5.0;

    }
}
