package majd_hamdan.com.easyjob.authentication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import majd_hamdan.com.easyjob.ContentActivity;
import majd_hamdan.com.easyjob.R;

public class LoginActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button loginButton;
    protected Button singupButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hide the action bar at the top
        getSupportActionBar().hide();

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //initialize views
        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        loginButton = (Button) findViewById(R.id.loginButton);
        singupButton = (Button) findViewById(R.id.singupButton);

    }

    public void onLoginClicked(View view){

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
            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        //move to contentActivity if authentication is succelful otherwise diplay
                        //authentication error message.
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, ContentActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            } else {
                                AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                                        .setMessage(task.getException().getMessage())
                                        .setPositiveButton(android.R.string.ok, null)
                                        .show();
                            }
                        }
                    });
        }

    }

    public void onSignupClicked(View view){
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);

    }
}
