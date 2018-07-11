package com.finalproject.dogplay;

import java.util.ArrayList;

public class UserData {

    //user name to be displayed
    private String uName;
    //dog name
    private String dName;
    // dog descriptions: small , big, lazy, etc
    private ArrayList<String> dDescription;
    //list of dog friends
    private ArrayList<String> dFriends;
    //list of dog our dog don't like
    private ArrayList<String>  dEnemy;

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

    public ArrayList<String> getdEnemy() {
        return dEnemy;
    }

    public void setdEnemy(ArrayList<String> dEnemy) {
        this.dEnemy = dEnemy;
    }

    public void addFriend(String friendUser){
        dFriends.add(friendUser);
    }

    public void removeFriend(String friendUser){
        dEnemy.remove(friendUser);
    }

    public void addEnemy(String enemyUser){
        dEnemy.add(enemyUser);
    }

    public void removeEnemy(String enemyUser){
        dEnemy.remove(enemyUser);
    }

    public UserData(){
        dFriends = new ArrayList<String>();
        dEnemy = new ArrayList<String>();
    }
}
