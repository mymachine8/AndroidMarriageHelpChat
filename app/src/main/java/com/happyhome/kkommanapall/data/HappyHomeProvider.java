package com.happyhome.kkommanapall.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by kkommanapall on 10/19/2015.
 */
public class HappyHomeProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ChatDBHelper mOpenHelper;

    static final int CHATSERVICE = 100;
    private static final SQLiteQueryBuilder sChatServiceQueryBUilder = new SQLiteQueryBuilder();
    @Override
    public boolean onCreate() {
        mOpenHelper = new ChatDBHelper(getContext());
        return true;
    }
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        return "dummy";
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*"

            // "weather/*"
            // "weather"
            case CHATSERVICE: {
                retCursor = null;
                break;
            }
            // "location"

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Student: This is a lot like the delete function.  We return the number of rows impacted
        // by the update.
        return 0;
    }

    static UriMatcher buildUriMatcher() {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case CHATSERVICE: {
                long _id = db.insert(ChatContract.ChatServiceEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ChatContract.ChatServiceEntry.buildChatServiceUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database

        // Student: Use the uriMatcher to match the WEATHER and LOCATION URI's we are going to
        // handle.  If it doesn't match these, throw an UnsupportedOperationException.

        // Student: A null value deletes all rows.  In my implementation of this, I only notified
        // the uri listeners (using the content resolver) if the rowsDeleted != 0 or the selection
        // is null.
        // Oh, and you should notify the listeners here.

        // Student: return the actual rows deleted
        return 0;
    }

    private Cursor getChatservices(
            Uri uri, String[] projection, String sortOrder) {

        return sChatServiceQueryBUilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }
}
