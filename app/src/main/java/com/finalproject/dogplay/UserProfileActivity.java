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

public class UserProfileActivity extends AppCompatActivity {

    private Intent intentToMain;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    DatabaseReference databaseUserProfiles;
    UserProfile currentUserProfile;

    String uname = "";
    String dname = "";
    ArrayList<String> dAtbs;

    EditText uNameET, dNameET;
    RadioGroup dSizeRG;
    CheckBox friendlyCB, playfulCB, goodWithPeopleCB;
    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        databaseUserProfiles = FirebaseDatabase.getInstance().getReference("UserProfiles");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uNameET = findViewById(R.id.uName);
        dNameET = findViewById(R.id.dName);
        dSizeRG = findViewById(R.id.size);
        friendlyCB = findViewById(R.id.isFriendly);
        playfulCB = findViewById(R.id.isPlayful);
        goodWithPeopleCB = findViewById(R.id.isGoodWithPeople);
        confirmButton = findViewById(R.id.filledUserData);

        Bundle extras = getIntent().getExtras();
        uname = extras.getString("EXTRA_USERNAME");
        dname = extras.getString("EXTRA_DOGNAME");
        dAtbs = extras.getStringArrayList("EXTRA_DOGATTRIBUTES");
        showCurrentUserData();

        intentToMain = new Intent(this,MainActivity.class);

        //setCurrentUserProfile(user);

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
                updateUserData(username, dogname, dSizeRG, friendlyCB, playfulCB, goodWithPeopleCB);

            }
        });
    }

    public void updateUserData(final String username,final String  dogname,final RadioGroup dSizeRG
            ,final CheckBox friendlyCB,final CheckBox playfulCB,final CheckBox goodWithPeopleCB){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String current_userID = user.getUid();

        databaseUserProfiles.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot profilesSnapshot : dataSnapshot.getChildren()) {
                    UserProfile userProfile = profilesSnapshot.getValue(UserProfile.class);
                    String id = userProfile.getuID();
                    if (userProfile.getuID().equals(current_userID)) {
                        currentUserProfile = userProfile;
                    }

                    currentUserProfile.setuName(username);
                    currentUserProfile.setdName(dogname);
                    currentUserProfile.setdDescription(dogDescription(dSizeRG,friendlyCB,playfulCB, goodWithPeopleCB));


                    databaseUserProfiles.child(currentUserProfile.getuID()).setValue(currentUserProfile);
                    Toast.makeText(getApplicationContext(), R.string.registration_complete, Toast.LENGTH_LONG).show();
                    startActivity(intentToMain);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    protected  ArrayList<String> dogDescription(RadioGroup dSizeRG,
                                                CheckBox friendlyCB, CheckBox playfulCB,
                                                CheckBox goodWithPeopleCB){

        ArrayList<String> dDescription = currentUserProfile.getdDescription();
        dDescription.clear();
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


    protected void setCurrentUserProfile(final FirebaseUser user){
        final String current_userID = user.getUid();

        databaseUserProfiles.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot profilesSnapshot: dataSnapshot.getChildren()){
                    UserProfile userProfile = profilesSnapshot.getValue(UserProfile.class);
                    String id = userProfile.getuID();
                    if (userProfile.getuID().equals(current_userID)){
                        currentUserProfile = userProfile;
                    }

                }
                if (currentUserProfile == null){
                    //start UserProfile Activity
                    startActivity(new Intent(UserProfileActivity.this, LoginActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "on Canceled", Toast.LENGTH_SHORT).show();
            }
        });

        uNameET.setText(uname);
        dNameET.setText(dname);

        if (currentUserProfile.getdDescription().contains("small"))
            ((RadioButton)findViewById(R.id.small)).setSelected(true);
        else if (currentUserProfile.getdDescription().contains("medium"))
            ((RadioButton)findViewById(R.id.medium)).setSelected(true);
        else if (currentUserProfile.getdDescription().contains("big"))
            ((RadioButton)findViewById(R.id.big)).setSelected(true);

        if (currentUserProfile.getdDescription().contains("friendly"))
            friendlyCB.setChecked(true);
        if (currentUserProfile.getdDescription().contains("playful"))
            playfulCB.setChecked(true);
        if (currentUserProfile.getdDescription().contains("gWithPeople"))
            goodWithPeopleCB.setChecked(true);


    }

    public void showCurrentUserData(){
        uNameET.setText(uname);
        dNameET.setText(dname);

        if (dAtbs.contains("small"))
            ((RadioButton)findViewById(R.id.small)).setSelected(true);
        else if (dAtbs.contains("medium"))
            ((RadioButton)findViewById(R.id.medium)).setSelected(true);
        else if (dAtbs.contains("big"))
            ((RadioButton)findViewById(R.id.big)).setSelected(true);

        if (dAtbs.contains("friendly"))
            friendlyCB.setChecked(true);
        if (dAtbs.contains("playful"))
            playfulCB.setChecked(true);
        if (dAtbs.contains("gWithPeople"))
            goodWithPeopleCB.setChecked(true);
    }
}