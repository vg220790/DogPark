package com.finalproject.dogplay.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.finalproject.dogplay.MainActivity;
import com.finalproject.dogplay.Manifest;
import com.finalproject.dogplay.R;
import com.finalproject.dogplay.models.UserProfile;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class FirstUserProfileActivity extends AppCompatActivity {

    public static final int READ_EXTERNAL_STORAGE = 0;
    public static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;
    private Firebase firebase;
    private Uri image_uri = null;
    private DatabaseReference databaseUserProfiles;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private Intent intentToMain;

    private FirebaseUser user;
    private String current_userID;

    EditText uNameET, dNameET;
    RadioGroup dSizeRG;
    CheckBox friendlyCB, playfulCB, goodWithPeopleCB;
    ImageView photo;
    Button upload_image, save_image, confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_profile);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        databaseUserProfiles = FirebaseDatabase.getInstance().getReference("UserProfiles");
        progressDialog = new ProgressDialog(FirstUserProfileActivity.this);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://dogplay-2564a.appspot.com/");
        final FirebaseUser u = auth.getCurrentUser();


        uNameET = findViewById(R.id.uName);
        dNameET = findViewById(R.id.dName);
        dSizeRG = findViewById(R.id.size);
        friendlyCB = findViewById(R.id.isFriendly);
        playfulCB = findViewById(R.id.isPlayful);
        goodWithPeopleCB = findViewById(R.id.isGoodWithPeople);
        photo = findViewById(R.id.user_imageview);
        upload_image = findViewById(R.id.upload_image_button);
        save_image = findViewById(R.id.save_image_button);
        confirmButton = findViewById(R.id.filledUserData);


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

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check for runtime permission
                if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE);
                    }
                } else {
                    callGallery();
                }
            }
        });

        save_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }// end of on create

    private UserProfile makeNewUserData(String uName, String dName, RadioGroup dSizeRG
            , CheckBox friendlyCB, CheckBox playfulCB, CheckBox goodWithPeopleCB){

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        current_userID = Objects.requireNonNull(user).getUid();
        String email = user.getEmail();

        UserProfile newUserProfile = new UserProfile(current_userID, email);

        newUserProfile.setuName(uName);
        newUserProfile.setdName(dName);

        ArrayList<String> dDescription = dogDescription(dSizeRG,
                friendlyCB, playfulCB, goodWithPeopleCB);
        newUserProfile.setdDescription(dDescription);

        return newUserProfile;

    }

    private ArrayList<String> dogDescription(RadioGroup dSizeRG,
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

    private void addUserToFireBase(final UserProfile newUserProfile){
        databaseUserProfiles.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                databaseUserProfiles.child(newUserProfile.getuID()).setValue(newUserProfile);
                Toast.makeText(getApplicationContext(),R.string.registration_complete, Toast.LENGTH_LONG).show();
                startActivity(intentToMain);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void callGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_INTENT);
    }

    //check for runtime permissions for access storage
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callGallery();
                return;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {
            image_uri = data.getData();
            photo.setImageURI(image_uri);
            StorageReference filePath = storageReference.child("ProfilePictures").child(image_uri.getLastPathSegment());
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();

            filePath.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUri = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    
                    //Uri downloadUri = taskSnapshot.getDownloadUri();
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    databaseUserProfiles.child(user.getUid()).child("photo_url").setValue(downloadUri.toString());


                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.dog_play_icon)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(photo);

                    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            });
        }

    }
}