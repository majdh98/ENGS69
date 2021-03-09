package majd_hamdan.com.easyjob.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import majd_hamdan.com.easyjob.ContentActivity;
import majd_hamdan.com.easyjob.R;

public class SignUpActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button singupButton;
    protected EditText confirmPassword;
    protected EditText firstName;
    protected EditText lastName;
    protected EditText phoneNumber;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;

    String TAG = "mh";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        // hide the action bar at the top
        getSupportActionBar().hide();

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //initialize views
        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        singupButton = (Button) findViewById(R.id.singupButton);
        confirmPassword = (EditText) findViewById(R.id.passwordConfirmationField);
        firstName = (EditText) findViewById(R.id.firstNameField);
        lastName = (EditText) findViewById(R.id.lastNameField);
        phoneNumber = (EditText) findViewById(R.id.phoneNumberField);

        //initiate connection to db
        database = FirebaseDatabase.getInstance().getReference();


    }

    public void onSignupClicked(View view){
        //get password and email and trim them
        String password = passwordEditText.getText().toString();
        String email = emailEditText.getText().toString();
        password = password.trim();
        email = email.trim();

        //check password and email then create use
        if (password.isEmpty() || email.isEmpty()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.signup_error_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                save_user_info();
                                Intent intent = new Intent(SignUpActivity.this, ContentActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog dialog = new AlertDialog.Builder(SignUpActivity.this)
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show();
                            }
                        }
                    });
        }

    }

    public void save_user_info(){
        String userId;
        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();
        String phonenum = phoneNumber.getText().toString();

        //get user id
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser.getUid();
        database.child("users").child(userId).child("offers_created").push().setValue(job);
        

    }
}
