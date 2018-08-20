package com.finalproject.dogplay.user;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
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
import com.finalproject.dogplay.R;
import com.finalproject.dogplay.models.UserProfile;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
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

public class UserProfileActivity extends AppCompatActivity {

    public static final int READ_EXTERNAL_STORAGE = 0;
    public static final int GALLERY_INTENT = 2;
    private ProgressDialog progressDialog;
    private Firebase firebase;
    private Uri image_uri = null;
    private DatabaseReference databaseUserProfiles;
    private StorageReference storageReference;
    private FirebaseAuth auth;
    private Uri downloadUri;
    private Intent intentToMain;

    private FirebaseUser user;
    private String current_userID;
    private UserProfile currentUserProfile;

    private String userName = "";
    private String dogName = "";
    private String photo_url = "";
    private ArrayList<String> dogAttributes;

    private EditText uNameET, dNameET;
    private RadioGroup dSizeRG;
    private CheckBox friendlyCB, playfulCB, goodWithPeopleCB;
    private ImageView photo;
    private Button confirmButton, upload_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_user_profile);

        //Get Firebase auth instance
        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        Firebase.setAndroidContext(this);
        databaseUserProfiles = FirebaseDatabase.getInstance().getReference("UserProfiles");
        progressDialog = new ProgressDialog(UserProfileActivity.this);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://dogplay-2564a.appspot.com/");

        //final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uNameET                 = findViewById(R.id.uName);
        dNameET                 = findViewById(R.id.dName);
        dSizeRG                 = findViewById(R.id.size);
        friendlyCB              = findViewById(R.id.isFriendly);
        playfulCB               = findViewById(R.id.isPlayful);
        goodWithPeopleCB        = findViewById(R.id.isGoodWithPeople);
        confirmButton = findViewById(R.id.filledUserData);
        photo = findViewById(R.id.user_imageview);
        upload_image = findViewById(R.id.upload_image_button);

        Bundle extras = getIntent().getExtras();
        userName = Objects.requireNonNull(extras).getString("EXTRA_USERNAME");
        dogName = extras.getString("EXTRA_DOGNAME");
        photo_url = extras.getString("EXTRA_URL");
        dogAttributes = extras.getStringArrayList("EXTRA_DOGATTRIBUTES");
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

    }

    private void updateUserData(final String username, final String dogname, final RadioGroup dSizeRG
            , final CheckBox friendlyCB, final CheckBox playfulCB, final CheckBox goodWithPeopleCB){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String current_userID = Objects.requireNonNull(user).getUid();

        databaseUserProfiles.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot profilesSnapshot : dataSnapshot.getChildren()) {
                    UserProfile userProfile = profilesSnapshot.getValue(UserProfile.class);

                    if (userProfile.getuID().equals(current_userID)) {
                        currentUserProfile = userProfile;
                        currentUserProfile.setuName(username);
                        currentUserProfile.setdName(dogname);
                        currentUserProfile.setPhoto_url(photo_url);
                        currentUserProfile.setdDescription(dogDescription(dSizeRG, friendlyCB, playfulCB, goodWithPeopleCB));
                        databaseUserProfiles.child(currentUserProfile.getuID()).setValue(currentUserProfile);
                        Toast.makeText(getApplicationContext(), R.string.registration_complete, Toast.LENGTH_LONG).show();
                        startActivity(intentToMain);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private ArrayList<String> dogDescription(RadioGroup dSizeRG,
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



    private void showCurrentUserData(){
        uNameET.setText(userName);
        dNameET.setText(dogName);

        //show profile photo
        Uri uri = null;
        if (photo_url != null) {
            uri = Uri.parse(photo_url);
        }

        photo.setImageURI(uri);
        Glide.with(getApplicationContext())
                .load(uri)
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.dog_play_icon)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(photo);

        if (dogAttributes.contains("small"))
            (findViewById(R.id.small)).setSelected(true);
        else if (dogAttributes.contains("medium"))
            (findViewById(R.id.medium)).setSelected(true);
        else if (dogAttributes.contains("big"))
            (findViewById(R.id.big)).setSelected(true);

        if (dogAttributes.contains("friendly"))
            friendlyCB.setChecked(true);
        if (dogAttributes.contains("playful"))
            playfulCB.setChecked(true);
        if (dogAttributes.contains("gWithPeople"))
            goodWithPeopleCB.setChecked(true);
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
            final StorageReference filePath = storageReference.child("ProfilePictures").child(image_uri.getLastPathSegment());
            progressDialog.setMessage("Uploading Image...");
            progressDialog.show();

            final Task uploadTask = filePath.putFile(image_uri);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    //we need this method to properly get selected image url
                    ///////////////////////////////////
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            // Continue with the task to get the download URL
                            return filePath.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadUri = task.getResult();
                                photo_url = downloadUri.toString();
                            } else {
                                // Handle failures
                                // ...
                            }
                        }
                    });
                    ///////////////////////////////////


                    //adding delay
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                        }
                    }, 2000);


                    Glide.with(getApplicationContext())
                            .load(downloadUri)
                            .apply(new RequestOptions()
                                    .placeholder(R.mipmap.dog_play_icon)
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                            .into(photo);

                    Toast.makeText(getApplicationContext(), "image uploaded", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            });
        }

    }
}