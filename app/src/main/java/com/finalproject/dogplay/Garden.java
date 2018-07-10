package com.finalproject.dogplay;

import java.util.ArrayList;

public class Garden {

    private ArrayList<UserData> users;
    private String location;

    public Garden(String nLocation){
        setLocation(nLocation);
    }

    public ArrayList<UserData> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<UserData> users) {
        this.users = users;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void addUser(UserData nUser){
        users.add(nUser);
    }


}
