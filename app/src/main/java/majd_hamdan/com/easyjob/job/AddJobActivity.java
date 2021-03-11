package majd_hamdan.com.easyjob.job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.authentication.LoginActivity;
import majd_hamdan.com.easyjob.payment.CheckoutActivity;

public class AddJobActivity extends AppCompatActivity  {

    // instance variables for the constructor and method codes

    // references for database, geo location, and location requests
    private DatabaseReference database;
    private DatabaseReference geofire_db;
    private GeoFire geoFire;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;
    private Location job_location;
    private String job_address;
    private FusedLocationProviderClient fusedLocationClient;
    public static String payment;


    // references for edit text information for the job
    private EditText job_type;
    private EditText job_pay;
    private EditText job_description;
    private EditText address_field;
    private EditText city_field;
    private EditText zipcode_field;
    private EditText state_field;
    private EditText country_field;


    String TAG = "mh";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        // setup app bar
        setTitle("Create a Job");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#002171")));

        // used to suppress the keyboard from popping up when code runs
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //initialize firebase db and user id
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogIn();
        } else {
            userId = firebaseUser.getUid();
        }
        database = FirebaseDatabase.getInstance().getReference();
        geofire_db = FirebaseDatabase.getInstance().getReference().child("geofire");
        geoFire = new GeoFire(geofire_db);

        // Add items via the Button and EditText at the bottom of the view.
        // initialize the instance variables foe edit texts and find their values
        job_type = (EditText) findViewById(R.id.jobtypeField);
        job_pay = (EditText) findViewById(R.id.payField);
        job_description = (EditText) findViewById(R.id.descriptionField);
        address_field = (EditText) findViewById(R.id.addressField);
        city_field = (EditText) findViewById(R.id.cityField);
        zipcode_field = (EditText) findViewById(R.id.zipcodeField);
        country_field = (EditText) findViewById(R.id.countryField);
        state_field = (EditText) findViewById(R.id.stateField);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        job_location = new Location(LocationManager.GPS_PROVIDER);
        //add user current address to address ui
        autofill_address();


    }

    public void onCancelClicked(View view){
        finish();
    }


    public void onAddJobClicked(View view){
        // if user clicks and selects to add a job
        // find the information the user input in the text

        String type = job_type.getText().toString();
        String pay = String.valueOf(job_pay.getText());
        String description = job_description.getText().toString();
        String offer_id = database.push().getKey();
        Log.d(TAG, "onAddJobClicked: "+ offer_id);

        // find the address of the job to be added
        if(getLocationFromAddress()){
            if(!checkInfo()){

                // if address info is not selected yet, then validate address and save
                Job job = new Job(description, type, offer_id,  job_address, pay, userId);
                geoFire.setLocation(offer_id, new GeoLocation(job_location.getLatitude(), job_location.getLongitude()));
                database.child("offers").child(offer_id).setValue(job);
                database.child("users").child(userId).child("offers_created").child(offer_id).setValue(job);
                payment = pay;
                Intent intent = new Intent(this, CheckoutActivity.class);
                startActivity(intent);
                finish();
            }
            else{
                // if one of the address tabs is empty, check which ones are empty
                // and send the user a toast to fill in the info

                String address = address_field.getText().toString();
                String zip = zipcode_field.getText().toString();
                String state = state_field.getText().toString();
                String country = state_field.getText().toString();

                String[] info = new String[]{type, pay, description, address, zip, state, country};
                String[] tags = new String[]{"Job Type", "Pay", "Description", "Address", "City", "State", "Zip Code", "Country"};

                String toast = makeToast(info, tags);
                Toast toast_it = Toast.makeText(this, toast, Toast.LENGTH_SHORT);
                toast_it.show();
            }

        }else{
            //if address is not validated, then make sure to tell the user to enter address
            // in requested format

            CharSequence text = "Address can't be found, please enter address in requested format.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }

    }

    public boolean checkInfo(){
        // checking if any of the edit texts are left unfilled

        String type = job_type.getText().toString();
        String pay = String.valueOf(job_pay.getText());
        String description = job_description.getText().toString();
        String address = address_field.getText().toString();
        String zip = zipcode_field.getText().toString();
        String state = state_field.getText().toString();
        String country = state_field.getText().toString();
        return type.length() == 0 || pay.length() == 0 || description.length() == 0 || address.length() == 0 ||
                zip.length() == 0 || state.length() == 0 || country.length() == 0;
    }

    public String makeToast(String[] info, String[] tags){
        // creating the toast with the specific edit texts that are unfilled

        StringBuilder toast = new StringBuilder();

        for(int i = 0; i < info.length; i++){

            if( toast.toString().length() == 0 && info[i].length() == 0){
                // finding the edit texts with no texts

                toast.append(tags[i]);
            }
            else if(info[i].length() == 0){
                toast.append(", ").append(tags[i]);
            }
        }

        return "Fill in " + toast.toString() + " information.";
    }

    @SuppressLint("MissingPermission")
    public void autofill_address(){
        // used to find location to fill in address edit texts automatically

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            String[] add;
                            Geocoder geocoder = new Geocoder(AddJobActivity.this, Locale.getDefault());
                            try {
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                Address address = addresses.get(0);
                                job_address = address.getAddressLine(0);
                                add = job_address.split(",");
                                fill_address_fields(add[0], add[1], address.getAdminArea(), add[3], address.getPostalCode());
                            } catch (IOException e) {
                            }
                        }
                    }
                });
    }

    public void fill_address_fields(String address, String city, String state, String country, String zipcode){
        // method to fill in address given address information specified in parameter

        address_field.setText(address);
        city_field.setText(city);
        state_field.setText(state);
        country_field.setText(country);
        zipcode_field.setText(zipcode);
    }

    public void loadLogIn(){
        // function to start the activity that asks for authentication

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public boolean getLocationFromAddress(){
        // given location find address and validate address

        // take the address from the edit text views and validate
        String str_add = address_field.getText().toString();
        String city = city_field.getText().toString();
        String state = state_field.getText().toString();
        String zipcode = zipcode_field.getText().toString();
        String country = country_field.getText().toString();
        String address = str_add + ", " + city + ", " + state + " " + zipcode + ", " + country;



        try {
            Geocoder selected_place_geocoder = new Geocoder(this);
            List<Address> addresses;

            addresses = selected_place_geocoder.getFromLocationName(address, 1);

            // check if the address is a valid one from geo location and return boolean
            if (addresses == null) {
                return false;
            }
            else {
                Address location = addresses.get(0);
                job_location.setLatitude(location.getLatitude());
                job_location.setLongitude(location.getLongitude());
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }







}


