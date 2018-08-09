package com.finalproject.dogplay.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Playground {

    private String id;
    private ArrayList<UserProfile> users = new ArrayList<>();
    private ArrayList<String> visitors = new ArrayList<>();
    private String address;
    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
    }
    private double longitude;
    private double latitude;
    private ChatData playgroundChat;
    private StringBuilder userString;


    private void newChat(){
        playgroundChat = new ChatData();
    }
    public Playground(){}

    public Playground(String address, double latitude, double longitude){
        setAddress(address);
        this.latitude = latitude;
        this.longitude = longitude;
        newChat();
    }

    public ArrayList<UserProfile> getUsers() {
        return this.users;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    private void setAddress(String address) {
        this.address = address;
    }

    public void addUser(UserProfile nUser){
        users.add(nUser);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUsers(ArrayList<UserProfile> users) {
        this.users = users;
    }

    public void setVisitors(ArrayList<String> visitors) {
        this.visitors = visitors;
    }

    public boolean hasVisitors(){ return !this.visitors.isEmpty();}


    public String toString(){
        String toReturn = this.getAddress() + "-" + this.latitude + "-" + this.longitude;
        String usersString =  "";
        if (!visitors.isEmpty()){
            StringBuilder s = new StringBuilder();
            s.append("-[");
            s.append(makeUserString(false));
            usersString = s.substring(0, s.length() - 1) + "]"; //remove last "," from users string array
        }
        return toReturn + usersString;
    }
    public String makeUserString(boolean detailed){
        userString = new StringBuilder();
        findUserByID(detailed);
        return userString.toString();
    }

    private void findUserByID(final boolean detailed){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("UserProfiles");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    if(visitors.contains(userSnapshot.child("uID").getValue())) {
                        UserProfile user = userSnapshot.getValue(UserProfile.class);
                        if(detailed)
                            userString.append(user.toString()).append(", ");
                        else{
                            userString.append(user.getuName()).append("& ").append(user.getdName()).append(", ");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
