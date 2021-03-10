package majd_hamdan.com.easyjob.ui;

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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import majd_hamdan.com.easyjob.ContentActivity;
import majd_hamdan.com.easyjob.R;
import majd_hamdan.com.easyjob.authentication.LoginActivity;
import majd_hamdan.com.easyjob.authentication.User;

public class UserInfoActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button singupButton;
    protected EditText confirmPassword;
    protected EditText firstName;
    protected EditText lastName;
    protected EditText phoneNumber;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference database;
    private FirebaseUser user;
    private String userId;



    String TAG = "mh";




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //initiate connection to db
        database = FirebaseDatabase.getInstance().getReference();

        //initiate authentication view
        emailEditText = (EditText) findViewById(R.id.authEmailField);
        passwordEditText = (EditText) findViewById(R.id.authPasswordField);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
    }

    public void onSaveButtonClicked(View view) {
        String fn = firstName.getText().toString();
        String ln = lastName.getText().toString();
        String phone_num = phoneNumber.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        user.updateEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User email address updated.");
                        }
                    }
                });



    }

    public void set_user_info_to_db_info(){

        //get user id

        userId = user.getUid();


        DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("users");
        Query userQuery = users_ref.child(userId);
        userQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user  = dataSnapshot.getValue(User.class);
                if(user != null){
                    firstName.setText(user.firstName);
                    lastName.setText(user.lastName);
                    emailEditText.setText(user.email);
                    phoneNumber.setText(user.phoneNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onAuthenticateClicked(View view) {

        //get email and password and trim them
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        email = email.trim();
        password = password.trim();

        //check if the correct email and password are given then authenticate
        if (email.isEmpty() || password.isEmpty()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(R.string.login_error_message)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
        } else {
            firebaseAuth.signOut();
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        //move to contentActivity if authentication is succelful otherwise diplay
                        //authentication error message.
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                findViewById(R.id.authenticateView).setVisibility(View.GONE);
                                findViewById(R.id.resetView).setVisibility(View.VISIBLE);
                                //initialize views
                                emailEditText = (EditText) findViewById(R.id.emailField);
                                passwordEditText = (EditText) findViewById(R.id.passwordField);
                                singupButton = (Button) findViewById(R.id.singupButton);
                                confirmPassword = (EditText) findViewById(R.id.passwordConfirmationField);
                                firstName = (EditText) findViewById(R.id.firstNameField);
                                lastName = (EditText) findViewById(R.id.lastNameField);
                                phoneNumber = (EditText) findViewById(R.id.phoneNumberField);
                                set_user_info_to_db_info();

                            } else {
                                AlertDialog dialog = new AlertDialog.Builder(UserInfoActivity.this)
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show();
                            }
                        }
                    });
        }


    }
}


//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//user.updateEmail("user@example.com")
//        .addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if (task.isSuccessful()) {
//        Log.d(TAG, "User email address updated.");
//        }
//        }
//        });


