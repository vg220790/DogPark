package com.finalproject.dogplay;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserProfile {

    private String uID;
    private String uEMail;
    private String uPassword;

    //user name to be displayed
    private String uName;

    //dog name
    private String dName;

    // dog descriptions: small , big, lazy, etc
    private ArrayList<String> dDescription;

    //list of dog friends
    private ArrayList<String> dFriends;

    //list of dog our dog don't like
    private ArrayList<String> dEnemey;

    public UserProfile() { }

    public UserProfile(String id, String email) {
        setuID(id);
        setuEMail(email);
    }

    public String getuID() {
        return uID;
    }
    public void setuID(String id) {
        this.uID = id;
    }
    public String getuEMail() {
        return uEMail;
    }
    public void setuEMail(String eMail) {
        this.uEMail = eMail;
    }
    public String getuPassword() {
        return uPassword;
    }
    public void setuPassword(String password) {
        this.uPassword = password;
    }

    public String getuName() {
        return uName;
    }
    public void setuName(String uName) {
        this.uName = uName;
    }
    public String getdName() {
        return dName;
    }
    public void setdName(String dName) {
        this.dName = dName;
    }
    public ArrayList<String> getdDescription() {
        return dDescription;
    }
    public void setdDescription(ArrayList<String> dDescription) {
        this.dDescription = dDescription;
    }
    public ArrayList<String> getdFriends() {
        return dFriends;
    }
    public void setdFriends(ArrayList<String> dFriends) {
        this.dFriends = dFriends;
    }
    public ArrayList<String> getdEnemey() {
        return dEnemey;
    }
    public void setdEnemey(ArrayList<String> dEnemey) {
        this.dEnemey = dEnemey;
    }

    @Override
    public String toString() {
        String s = getuName() +  " " + getdName() + " ";
        for (String attribute : dDescription){
            s += attribute + " ";
        }
        return s.substring(0, s.length() - 1); // return a substring to remove only the last space (" ") of the string
    }
}
