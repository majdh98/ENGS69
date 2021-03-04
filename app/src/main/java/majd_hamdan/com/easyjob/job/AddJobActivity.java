package majd_hamdan.com.easyjob.job;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import majd_hamdan.com.easyjob.Job;
import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.authentication.LoginActivity;

public class AddJobActivity extends AppCompatActivity {

    private DatabaseReference database;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userId;
    private EditText job_type;
    private EditText job_pay;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_job);

        //initialize firebase db and user id
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance().getReference();

        // Add items via the Button and EditText at the bottom of the view.
        job_type = (EditText) findViewById(R.id.jobtypeField);
        job_pay = (EditText) findViewById(R.id.payField);

        if (firebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogIn();
        } else {
            userId = firebaseUser.getUid();

        }

    }

    public void onAddJobClicked(View view){
        String type = job_type.getText().toString();
        Double pay = Double.valueOf(String.valueOf(job_pay.getText()));
        Job job = new Job("", "", 0, type, pay);
        database.child("users").child(userId).child("offers_created").push().setValue(job);
    }

    public void loadLogIn(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
