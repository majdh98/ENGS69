package majd_hamdan.com.easyjob.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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

import majd_hamdan.com.easyjob.ContentActivity;
import majd_hamdan.com.easyjob.R;

public class SignUpActivity extends AppCompatActivity {

    protected EditText emailEditText;
    protected EditText passwordEditText;
    protected Button singupButton;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);

        // Initialize FirebaseAuth
        firebaseAuth = FirebaseAuth.getInstance();

        //initialize views
        emailEditText = (EditText) findViewById(R.id.emailField);
        passwordEditText = (EditText) findViewById(R.id.passwordField);
        singupButton = (Button) findViewById(R.id.singupButton);

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
}
