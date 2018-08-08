package com.finalproject.dogplay.models;

import java.util.ArrayList;

public class Playground {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private ArrayList<UserProfile> users = new ArrayList<>();
    private String address;

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    private double longitude;
    private double latitude;
    ChatData playgroundChat;


    public void newChat(){
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
        String toReturn = this.getAddress() + "-" + this.latitude + "-" + this.longitude;
        String usersString =  "";
        if (!users.isEmpty()){
            StringBuilder s = new StringBuilder();
            s.append("-[");
            for (UserProfile userProfile: users){
                s.append( userProfile.toString()).append(",");
            }
            usersString = s.substring(0, s.length() - 1) + "]"; //remove last "," from users string array
        }
        return toReturn + usersString;
    }
}
