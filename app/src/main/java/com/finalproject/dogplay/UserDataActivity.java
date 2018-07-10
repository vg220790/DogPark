package com.finalproject.dogplay;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserDataActivity extends AppCompatActivity {

    private Intent intentToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);

        final EditText uNameET = findViewById(R.id.uName);
        final EditText dNameET = findViewById(R.id.dName);
        final RadioGroup dSizeRG = findViewById(R.id.size);
        final CheckBox friendlyCB = findViewById(R.id.isFriendly);
        final CheckBox playfulCB = findViewById(R.id.isPlayful);
        final CheckBox goodWithPeopleCB = findViewById(R.id.isGoodWithPeople);

        Button confirmButton = findViewById(R.id.filledUserData);

        intentToMain = new Intent(this,LoginActivity.class);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserData newUserData = makeNewUserData(uNameET, dNameET, dSizeRG
                , friendlyCB, playfulCB, goodWithPeopleCB);
                addUserToFireBase(newUserData);
            }
        });
    }

    protected UserData makeNewUserData(EditText uNameET,  EditText dNameET, RadioGroup dSizeRG
            , CheckBox friendlyCB, CheckBox playfulCB, CheckBox goodWithPeopleCB){
        UserData newUserData = new UserData();
        newUserData.setuName(uNameET.getText().toString());
        newUserData.setdName(dNameET.getText().toString());
        ArrayList<String> dDescription = dogDescription(dSizeRG,
                                            friendlyCB, playfulCB, goodWithPeopleCB);
        newUserData.setdDescription(dDescription);
        return newUserData;

    }

    protected  ArrayList<String> dogDescription(RadioGroup dSizeRG,
                                                CheckBox friendlyCB, CheckBox playfulCB,
                                                CheckBox goodWithPeopleCB){
        ArrayList<String> dDescription = new ArrayList<>();
        //dog size
        dDescription.add(((RadioButton)findViewById(dSizeRG.getCheckedRadioButtonId()))
                .getText().toString());
        if(friendlyCB.isChecked())
            dDescription.add(friendlyCB.getText().toString());
        if(playfulCB.isChecked())
            dDescription.add(playfulCB.getText().toString());
        if(goodWithPeopleCB.isChecked())
            dDescription.add(goodWithPeopleCB.getText().toString());

        return  dDescription;
    }

    protected void addUserToFireBase(final UserData newUserData){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseReference database = FirebaseDatabase.getInstance().getReference();
                database.child("users").push().setValue(newUserData);
                Toast.makeText(getApplicationContext(),R.string.registration_complete, Toast.LENGTH_LONG).show();
                startActivity(intentToMain);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
