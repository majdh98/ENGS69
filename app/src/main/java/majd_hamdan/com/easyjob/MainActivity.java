package majd_hamdan.com.easyjob;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.CallSuper;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import majd_hamdan.com.easyjob.authentication.LoginActivity;

public class MainActivity extends AppCompatActivity {
    String TAG = "mh";

    private FirebaseAuth firebaseAuth;      // instance variable for firebase authentication
    private FirebaseUser firebaseUser;      // instance variable for firebase user profile




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // remove the app bar
        getSupportActionBar().hide();           // hiding the action bar

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();  // getting the authentication instance
        firebaseUser = firebaseAuth.getCurrentUser();  // getting the current user
        if (firebaseUser == null) {
            // Not logged in, launch the Log In activity
            loadLogIn();            // if user is null, then call and display login information
        }else{
           loadContent();           // if user is not null, then load the contents
        }

    }


    public void loadLogIn(){
        Intent intent = new Intent(this, LoginActivity.class);  // create the login activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void loadContent(){
        Intent intent = new Intent(this, ContentActivity.class);        // create and start the content activity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


}