package majd_hamdan.com.easyjob.job;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.authentication.LoginActivity;

public class AddJobActivity extends AppCompatActivity  {

    private DatabaseReference database;
    private DatabaseReference geofire_db;
    GeoFire geoFire;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;
    private Location job_location;
    private FusedLocationProviderClient fusedLocationClient;



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

    public void onAddJobClicked(View view){
        String type = job_type.getText().toString();
        String pay = String.valueOf(job_pay.getText());
        String description = job_description.getText().toString();
        String offer_id = database.push().getKey();
        if(getLocationFromAddress()){
            Job job = new Job(description, type, offer_id , pay, userId);
            geoFire.setLocation(offer_id, new GeoLocation(job_location.getLatitude(), job_location.getLongitude()));
            database.child("users").child(userId).child("offers_created").child(offer_id).setValue(job);
            fetch();
        }else{
            CharSequence text = "Address can't be found, please enter address in requested format.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }

    }

    @SuppressLint("MissingPermission")
    public void autofill_address(){

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
                                add = address.getAddressLine(0).split(",");
                                fill_address_fields(add[0], add[1], address.getAdminArea(), add[3], address.getPostalCode());
                            } catch (IOException e) {
                            }
                        }
                    }
                });
    }

    public void fill_address_fields(String address, String city, String state, String country, String zipcode){
        address_field.setText(address);
        city_field.setText(city);
        state_field.setText(state);
        country_field.setText(country);
        zipcode_field.setText(zipcode);
    }

    public void loadLogIn(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public boolean getLocationFromAddress(){
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

            if (addresses == null) {
                return false;
            } else {
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

    public void fetch(){
        Log.d(TAG, "fetch: ");

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(job_location.getLatitude(), job_location.getLongitude()), 3);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d(TAG, String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });

    }

    {
        "rules":{
        "geofire":{

            "rules":{
                "<your-geofire-node>":{
                    // Allow anyone to read the GeoFire index
                    ".read":true,

                            // Index each location's geohash for faster querying
                            ".indexOn": ["g"],

                    // Schema validation
                    "$key":{
                        // Allow any authentication user to add, update, or remove keys in the GeoFire index
                        ".write":"auth !== null",

                                // Key validation
                                ".validate":
                        "newData.hasChildren(['g', 'l']) && newData.getPriority().length <= 22 && newData.getPriority().length > 0",

                                // Geohash validation
                                "g":{
                            ".validate":"newData.val() == newData.parent().getPriority()"
                        },

                        // Location coordinates validation
                        "l":{
                            "0" :{
                                ".validate":
                                "newData.isNumber() && newData.val() >= -90 && newData.val() <= 90"
                            },
                            "1" :{
                                ".validate":
                                "newData.isNumber() && newData.val() >= -180 && newData.val() <= 180"
                            },
                            "$other":{
                                ".validate":false
                            }
                        },

                        // Don't allow any other keys to be written
                        "$other":{
                            ".validate":false
                        }
                    }
                }

            }
        },
        "users":{
            "$uid":{
                ".read":"auth != null && auth.uid == $uid",
                        ".write":"auth != null && auth.uid == $uid",
                        "name":{
                    ".validate":"newData.isString() && newData.val().length > 0"
                },
                "phone_number":{
                    ".validate":"newData.isNumber() && newData.val().length > 7"
                }


            }
        },
        "offers":{
            ".read":"auth != null",
                    ".write":"auth != null"
        }
    }
    }


}
