package com.finalproject.dogplay.models;

import java.util.ArrayList;

public class Playground {

    private String id;
    private ArrayList<UserProfile> users;
    private String address;

    private double longitude;
    private double latitude;
    private ChatData playgroundChat;


    public Playground(){}

    public Playground(String address, double latitude, double longitude){
        setAddress(address);
        setLatitude(latitude);
        setLongitude(longitude);
        newChat();
    }

    private void newChat(){
        playgroundChat = new ChatData();
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public ArrayList<UserProfile> getUsers() {
        return this.users;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void addUser(UserProfile nUser){
        users.add(nUser);
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public void setUsers(ArrayList<UserProfile> users) {
        this.users = users;
    }

    public boolean hasUsers(){ return !this.users.isEmpty();}


    public String toString(){
        String toReturn = this.getAddress() + "-" + this.latitude + "-" + this.longitude;
        String usersString =  "";
        if (!users.isEmpty()){
            StringBuilder s = new StringBuilder();
            s.append("-[");
            s.append(usersToString(false));
            usersString = s.substring(0, s.length() - 1) + "]"; //remove last "," from users string array
        }
        return toReturn + usersString;
    }

    public String usersToString(boolean detailed){
        StringBuilder userInPlaygroundString = new StringBuilder();
        for(UserProfile user : this.users) {
            if (detailed)
                userInPlaygroundString.append(user.toString()).append(", ");
            else {
                userInPlaygroundString.append(user.getuName()).append("& ").append(user.getdName()).append(", ");
            }
        }
        return userInPlaygroundString.toString();
    }


}
