package com.finalproject.dogplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.finalproject.dogplay.models.UserProfile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirstUserProfileActivity extends AppCompatActivity {

    private Intent intentToMain;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    DatabaseReference databaseUserProfiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        databaseUserProfiles = FirebaseDatabase.getInstance().getReference("UserProfiles");

        final EditText uNameET = findViewById(R.id.uName);
        final EditText dNameET = findViewById(R.id.dName);
        final RadioGroup dSizeRG = findViewById(R.id.size);
        final CheckBox friendlyCB = findViewById(R.id.isFriendly);
        final CheckBox playfulCB = findViewById(R.id.isPlayful);
        final CheckBox goodWithPeopleCB = findViewById(R.id.isGoodWithPeople);

        Button confirmButton = findViewById(R.id.filledUserData);

        intentToMain = new Intent(this,MainActivity.class);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String username = uNameET.getText().toString().trim();
                String dogname = dNameET.getText().toString().trim();

                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(getApplicationContext(), "Enter username!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(dogname)) {
                    Toast.makeText(getApplicationContext(), "Enter your dog's name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dSizeRG.getCheckedRadioButtonId() == -1)
                {
                    // no radio buttons are checked
                    Toast.makeText(getApplicationContext(), "Enter your dog's size!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //check if new user make new data
                UserProfile newUserProfile = makeNewUserData(username, dogname, dSizeRG
                        , friendlyCB, playfulCB, goodWithPeopleCB);
                addUserToFireBase(newUserProfile);

                //check if existing user and edit data
            }
        });
    }

    protected UserProfile makeNewUserData(String uName, String dName, RadioGroup dSizeRG
            , CheckBox friendlyCB, CheckBox playfulCB, CheckBox goodWithPeopleCB){

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        final String current_userID = user.getUid();
        String email = user.getEmail();

        UserProfile newUserProfile = new UserProfile(current_userID, email);

        newUserProfile.setuName(uName);
        newUserProfile.setdName(dName);

        ArrayList<String> dDescription = dogDescription(dSizeRG,
                friendlyCB, playfulCB, goodWithPeopleCB);
        newUserProfile.setdDescription(dDescription);

        return newUserProfile;

    }

    protected  ArrayList<String> dogDescription(RadioGroup dSizeRG,
                                                CheckBox friendlyCB, CheckBox playfulCB,
                                                CheckBox goodWithPeopleCB){
        ArrayList<String> dDescription = new ArrayList<>();
        //dog size
        dDescription.add(((RadioButton)findViewById(dSizeRG.getCheckedRadioButtonId()))
                .getText().toString().trim());
        if(friendlyCB.isChecked())
            dDescription.add(friendlyCB.getText().toString().trim());
        if(playfulCB.isChecked())
            dDescription.add(playfulCB.getText().toString().trim());
        if(goodWithPeopleCB.isChecked())
            dDescription.add(goodWithPeopleCB.getText().toString().trim());

        return  dDescription;
    }

    protected void addUserToFireBase(final UserProfile newUserProfile){
        databaseUserProfiles.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//                database.child("UserProfiles").push().setValue(newUserProfile);
                databaseUserProfiles.child(newUserProfile.getuID()).setValue(newUserProfile);
                Toast.makeText(getApplicationContext(),R.string.registration_complete, Toast.LENGTH_LONG).show();
                startActivity(intentToMain);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}