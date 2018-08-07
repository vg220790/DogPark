package com.finalproject.dogplay.models;

import java.util.ArrayList;

public class UserProfile {

    private String UserID;
    private String UserEMail;
    private String UserPassword;

    //user name to be displayed
    private String UserName;

    //dog name
    private String DogName;

    // dog descriptions: small , big, lazy, etc
    private ArrayList<String> DogDescription;

    //list of dog friends
    private ArrayList<String> DogFriends;

    //list of dog our dog don't like
    private ArrayList<String> DogEnemy;

    public UserProfile() { }

    public UserProfile(String id, String email) {
        setUserID(id);
        setUserEMail(email);
    }

    public String getUserID() {
        return UserID;
    }
    public void setUserID(String id) {
        this.UserID = id;
    }
    public String getUserEMail() {
        return UserEMail;
    }
    public void setUserEMail(String eMail) {
        this.UserEMail = eMail;
    }
    public String getUserPassword() {
        return UserPassword;
    }
    public void setUserPassword(String password) {
        this.UserPassword = password;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String uName) {
        this.UserName = uName;
    }
    public String getDogName() {
        return DogName;
    }
    public void setDogName(String dName) {
        this.DogName = dName;
    }
    public ArrayList<String> getDogDescription() {
        return DogDescription;
    }
    public void setDogDescription(ArrayList<String> dDescription) {
        this.DogDescription = dDescription;
    }
    public ArrayList<String> getDogFriends() {
        return DogFriends;
    }
    public void setDogFriends(ArrayList<String> dFriends) {
        this.DogFriends = dFriends;
    }
    public ArrayList<String> getDogEnemy() {
        return DogEnemy;
    }
    public void setDogEnemy(ArrayList<String> dEnemey) {
        this.DogEnemy = dEnemey;
    }

    @Override
    public String toString() {
        String s = getUserName() +  " " + getDogName() + " ";
        for (String attribute : DogDescription){
            s += attribute + " ";
        }
        return s.substring(0, s.length() - 1); // return a substring to remove only the last space (" ") of the string
    }
}
