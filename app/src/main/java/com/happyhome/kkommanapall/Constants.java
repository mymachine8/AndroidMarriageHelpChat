package com.happyhome.kkommanapall;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kkommanapall on 10/19/2015.
 */
public class Constants {
    public static final String CHAT_SERVER_URL = "http://kkommanapalli.herokuapp.com:80";
    public static final String LOCALHOST = "10.0.2.2";
    public static final String LOCALHOSTMOBILE = "192.168.1.102";
    public static final String BASE_URL = "http://kkommanapalli.herokuapp.com";
    public final static String NEW_MESSAGE = "com.happyhome.kkommanapall.service.ChatMessageService.newmessage";
    public final static String TYPING = "com.happyhome.kkommanapall.service.ChatMessageService.typing";
    public final static String EXECUTIVE_READY = "com.happyhome.kkommanapall.service.ChatMessageService.executiveready";
    public final static String STOP_TYPING = "com.happyhome.kkommanapall.service.ChatMessageService.stoptyping";
    public static final String USERDETAILS = "USERDETAILS";
    public static String Username;
    public static int Userid;
}
