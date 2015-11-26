package com.happyhome.kkommanapall.model;

import com.happyhome.kkommanapall.Constants;

/**
 * Created by kkommanapall on 10/21/2015.
 */
public class MessageObject {
    public int id;
    public int senderId;
    public int receiverId;
    public int serviceId;
    public String content;
    public static final int OWN_MESSAGE = 1;
    public static final int RECEIVED_MESSAGE = 2;

    public boolean isMe(){
        return this.senderId == Constants.Userid;
    }

    public String getMessage(){
        return content;
    }
}
