package com.finalproject.dogplay;

import java.util.ArrayList;

public class Playground {

    private ArrayList<UserProfile> users;
    private String location;

    public Playground(String nLocation){
        setLocation(nLocation);
    }

    public ArrayList<UserProfile> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserProfile> users) {
        this.users = users;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addUser(UserProfile nUser){
        users.add(nUser);
    }
}
