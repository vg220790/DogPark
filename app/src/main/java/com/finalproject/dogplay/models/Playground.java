package com.finalproject.dogplay.models;

import java.util.ArrayList;

public class Playground {

    private ArrayList<UserProfile> users = new ArrayList<>();
    private String name;
    private String address;
    private double longitude;
    private double latitude;
    ChatData playgroundChat;

    public Playground(){

    }

    public void newChat(){

    }

    public Playground(String address, double latitude, double longitude){
        setAddress(address);
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public ArrayList<UserProfile> getUsers() {
        return this.users;
    }

    public void setUsers(ArrayList<UserProfile> users) {
        this.users = users;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addUser(UserProfile nUser){
        users.add(nUser);
    }

    public String toString(){
        String s = this.getAddress() + "-" + this.latitude + "-" + this.longitude;
        if (!users.isEmpty()){
            s += "-[";
            for (UserProfile userProfile: users){
                s += userProfile.toString() + ",";
            }
            s = s.substring(0, s.length() - 1) + "]"; //remove last "," from users string array
        }
        return s;
    }
}
