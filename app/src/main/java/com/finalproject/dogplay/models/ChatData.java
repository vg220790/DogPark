package com.finalproject.dogplay.models;

import java.util.Date;

/**
 * Class responsible to hold the name and the message to the user
 * to send to firebase
 */
public class ChatData {

    private String mName;

    private String mMessage;

    private long time;

    public ChatData() {
        // empty constructor
    }

    public void setTime(){
        time = new Date().getTime();
    }
    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getTime() {
        return time;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }


}
