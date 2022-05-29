package com.example.task61d;

public class ChatBean {
    // Send a message
    public static final int SEND=1;
    // Receive messages
    public static final int RECEIVE=2;
    // Status of the message (whether it was received or sent)
    private int state;
    // Content of the message
    private String message;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

