package com.happyhome.kkommanapall.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kkommanapall on 10/19/2015.
 */
public class ChatContract {

    public static final String CONTENT_AUTHORITY = "com.happyhome.kkommanapall";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CHATSERVICE = "chatservice";
    public static final class ChatServiceEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_CHATSERVICE).build();

        public static final String TABLE_NAME = "chatservice";
        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_SERVICE = "servicename";
        public static final Uri buildChatServiceUri(long id){
            return new Uri.Builder().build();
        }

    }
}
