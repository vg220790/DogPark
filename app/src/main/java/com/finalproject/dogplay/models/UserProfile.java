package com.finalproject.dogplay.models;


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
    private ArrayList<String> dEnemies;

    public UserProfile() { }

    public UserProfile(String id, String email) {
        setuID(id);
        setuEMail(email);
        checkLists();
    }

    public String getuID() {
        return uID;
    }
    private void setuID(String id) {
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

    public void setDogFriendsList(ArrayList<String> dFriends) {
        this.dFriends = dFriends;
    }

    public ArrayList<String> getdEnemies() {
        return dEnemies;
    }

    public void setDogEnemiesList(ArrayList<String> dEnemy) {
        this.dEnemies = dEnemy;
    }

    public void setFriend(String friend) {
        checkLists();
        if (this.dEnemies.contains(friend))
            this.dEnemies.remove(friend);
        if (!this.dFriends.contains(friend))
            this.dFriends.add(friend);
    }

    public void setEnemy(String enemy) {
        checkLists();
        if (this.getdFriends().contains(enemy))
            this.dFriends.remove(enemy);
        if (!this.dEnemies.contains(enemy))
            this.dEnemies.add(enemy);
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(getuName() +  " " + getdName() + " ");
        for (String attribute : dDescription){
            s.append(attribute).append(" ");
        }
        return s.substring(0, s.length() - 1); // return a substring to remove only the last space (" ") of the string
    }

    public void checkLists() {
        if (this.dFriends == null)
            dFriends = new ArrayList<String>();

        if (this.dEnemies == null)
            dEnemies = new ArrayList<String>();
    }
}
