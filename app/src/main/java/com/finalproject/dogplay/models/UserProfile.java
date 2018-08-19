package com.finalproject.dogplay.models;


import java.util.ArrayList;
import java.util.HashMap;

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
    private HashMap<String, UserProfile> dFriends;
    //list of dog our dog don't like
    private HashMap<String, UserProfile> dEnemies;
    //list of users who tagged me as 'friend'
    private ArrayList<String> followers;

    public UserProfile() { }

    public UserProfile(String id, String email) {
        setuID(id);
        setuEMail(email);
        checkLists();
    }

    //getters
    public String getuID() {
        return this.uID;
    }
    public String getuEMail() {
        return this.uEMail;
    }

    public String getuPassword() {
        return this.uPassword;
    }

    public String getuName() {
        return this.uName;
    }

    public String getdName() {
        return this.dName;
    }

    public ArrayList<String> getdDescription() {
        return this.dDescription;
    }

    public HashMap<String, UserProfile> getdFriends() {
        return this.dFriends;
    }

    public HashMap<String, UserProfile> getdEnemies() {
        return this.dEnemies;
    }

    public ArrayList<String> getFollowers() {
        return this.followers;
    }

    //setters
    public void setuPassword(String password) {
        this.uPassword = password;
    }

    public void setuEMail(String eMail) {
        this.uEMail = eMail;
    }

    private void setuID(String id) {
        this.uID = id;
    }
    public void setuName(String uName) {
        this.uName = uName;
    }
    public void setdName(String dName) {
        this.dName = dName;
    }

    public void setdDescription(ArrayList<String> dDescription) {
        this.dDescription = dDescription;
    }

    public void setDogFriendsList(HashMap<String, UserProfile> dFriends) {
        this.dFriends = dFriends;
    }

    public void setDogEnemiesList(HashMap<String, UserProfile> dEnemy) {
        this.dEnemies = dEnemy;
    }

    public void setFollowersList(ArrayList<String> followers) {
        this.followers = followers;
    }

    public void setFriend(UserProfile friend) {
        checkLists();
        String friend_id = friend.getuID();
        if (this.dEnemies.containsKey(friend_id))
            this.dEnemies.remove(friend_id);
        if (!this.dFriends.containsKey(friend_id))
            this.dFriends.put(friend_id, friend);
    }

    public void setEnemy(UserProfile enemy) {
        checkLists();
        String enemy_id = enemy.getuID();
        if (this.dFriends.containsKey(enemy_id))
            this.dFriends.remove(enemy_id);
        if (!this.dEnemies.containsKey(enemy_id))
            this.dEnemies.put(enemy_id, enemy);
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
            this.dFriends = new HashMap<String, UserProfile>();

        if (this.dEnemies == null)
            this.dEnemies = new HashMap<String, UserProfile>();

        if (this.followers == null)
            this.followers = new ArrayList<String>();
    }
}
