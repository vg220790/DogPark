package com.finalproject.dogplay;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.finalproject.dogplay.models.Playground;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        //one time method call to add dog parks into firebase
        //setDogParks();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });


    }//end of onCreate

    public void setDogParks(){

        final ArrayList<Playground> playgroundArrayList= new ArrayList<Playground>();

        playgroundArrayList.add(new Playground("אמיל בריג 6, תל אביב יפו",32.109001,34.822982));
        playgroundArrayList.add(new Playground( "גינת כלבים פיקוס",32.113836,34.818336));
        playgroundArrayList.add(new Playground("בני דן 12, תל אביב יפו",32.095335,34.785439));
        playgroundArrayList.add(new Playground("פייבל 8, תל אביב יפו",32.084164,34.793030));
        playgroundArrayList.add(new Playground("שדרות נחמה 8, רמת גן",32.081686,34.812097));
        playgroundArrayList.add(new Playground("עבודה 3, רמת גן",32.068424,34.824785));
        playgroundArrayList.add(new Playground("רוקח, רמת גן",32.096214,34.814479));
        playgroundArrayList.add(new Playground("אריה דיסנצ'יק 8, תל אביב יפו",32.116831,34.827169));
        playgroundArrayList.add(new Playground("הרב גליקסברג 17, תל אביב יפו",32.118582,34.823127));
        playgroundArrayList.add(new Playground("הירקון 228, תל אביב יפו",32.090311,34.772610));
        playgroundArrayList.add(new Playground("דובנוב 24, תל אביב יפו",32.078623,34.783350));
        playgroundArrayList.add(new Playground("המלך ג'ורג' 30, תל אביב יפו",32.072269,34.774104));
        playgroundArrayList.add(new Playground("עין הקורא 21, תל אביב יפו",32.057372,34.783016));




        final DatabaseReference databasePlaygrounds = FirebaseDatabase.getInstance().getReference("Playgrounds");
        databasePlaygrounds.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (Playground newPlayground: playgroundArrayList){
                    databasePlaygrounds.child(newPlayground.getAddress()).setValue(newPlayground);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void addUsersToPlaygrounds(){

    }

}//end of class
