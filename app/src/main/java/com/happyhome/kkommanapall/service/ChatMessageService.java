package com.happyhome.kkommanapall.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happyhome.kkommanapall.model.CustomerConnect;
import com.happyhome.kkommanapall.model.CustomerConnect;
import com.happyhome.kkommanapall.model.MessageObject;

import com.happyhome.kkommanapall.ChatMessageActivity;
import com.happyhome.kkommanapall.Constants;
import com.happyhome.kkommanapall.MainActivity;
import com.happyhome.kkommanapall.helper.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.Transport;

/**
 * Created by kkommanapall on 10/19/2015.
 */
public class ChatMessageService extends Service {
    private int mId;
    private LocalBroadcastManager mBroadcaster;
    private Socket mSocket;
    private int mServiceId;
    private int mExecutiveId;
    private boolean isConnected = false;
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    public class LocalBinder extends Binder {
       public ChatMessageService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ChatMessageService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mBroadcaster = LocalBroadcastManager.getInstance(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mServiceId = intent.getExtras().getInt("EXTRA_CARESERVICEID");
        initSocket();
        //transportCreation();
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void initSocket(){
    }

    public void transportCreation() {// Called upon transport creation.
        mSocket.io().on(Manager.EVENT_TRANSPORT,  new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Transport transport = (Transport) args[0];

                transport.on(Transport.EVENT_REQUEST_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                        // modify request headers
                        headers.put("Cookie", Arrays.asList("foo=1;"));
                    }
                });

                transport.on(Transport.EVENT_RESPONSE_HEADERS, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        @SuppressWarnings("unchecked")
                        Map<String, List<String>> headers = (Map<String, List<String>>) args[0];
                        // access response headers
                        String cookie = headers.get("Set-Cookie").get(0);
                    }
                });
            }
        });
    }

    public void sendNotification(String title, String contentText) {
        // NotificationCompatBuilder is a very convenient way to build backward-compatible
        // notifications.  Just throw in some data.
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(title)
                        .setContentText(contentText);

        // Make something interesting happen when the user clicks on the notification.
        // In this case, opening the app is sufficient.
        Intent resultIntent = new Intent(this, ChatMessageActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(mId, mBuilder.build());
    }

//    private Emitter.Listener onErrorConnection = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            Log.e("ErrorConnection",args[0].toString());
//           // CallActivity(Constants.NEW_MESSAGE, (JSONObject)args[0]);
//
//        }
//    };

//    private Emitter.Listener onConnectionTimeout = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            Log.e("ConnectionTimeout",args[0].toString());
//            // CallActivity(Constants.NEW_MESSAGE, (JSONObject)args[0]);
//
//        }
//    };
//
//    private Emitter.Listener onSuccessfulConnection = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//          //  Log.v("SocketConnected",args[0].toString());
//            emitCustomerConnect();
//            // CallActivity(Constants.NEW_MESSAGE, (JSONObject)args[0]);
//
//        }
//    };
//
//    private Emitter.Listener onNewMessage = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            CallActivity(Constants.NEW_MESSAGE, (JSONObject)args[0]);
//
//        }
//    };
//    private Emitter.Listener onExecutiveReady = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            try {
//                JSONObject obj = (JSONObject)args[0];
//                mExecutiveId = obj.getInt("executiveId");
//                CallActivity(Constants.EXECUTIVE_READY, (JSONObject)args[0]);
//            }catch(Exception ex){
//
//            }
//        }
//    };
//
//    private Emitter.Listener onTyping = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            CallActivity(Constants.TYPING, (JSONObject)args[0]);
//        }
//    };
//
//    private Emitter.Listener onStopTyping = new Emitter.Listener() {
//        @Override
//        public void call(final Object... args) {
//            CallActivity(Constants.STOP_TYPING, (JSONObject)args[0]);
//        }
//    };
//
//    private void CallActivity(String tag,JSONObject message){
//        Intent intent = new Intent(tag);
//        try {
//            intent.putExtra("EXTRA_MESSAGE", message.toString());
//            mBroadcaster.sendBroadcast(intent);
//        }catch (Exception ex){
//
//        }
//    }

//    public void emitTyping(){
//        MessageObject mo = new MessageObject();
//        mo.senderId = Constants.Userid;
//        mo.receiverId = mExecutiveId;
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        try {
//            JSONObject obj = new JSONObject(gson.toJson(mo));
//            mSocket.emit("typing",obj);
//        }catch(JSONException ex){
//            Log.d("JSON Exception", ex.toString());
//        }
//    }
//
//    public void emitStopTyping(){
//        MessageObject mo = new MessageObject();
//        mo.senderId = Constants.Userid;
//        mo.receiverId = mExecutiveId;
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        try {
//            JSONObject obj = new JSONObject(gson.toJson(mo));
//            mSocket.emit("stop typing", obj);
//        }catch(JSONException ex){
//            Log.d("JSON Exception", ex.toString());
//        }
//    }
//
//    public void emitNewMessage(String msg){
//            MessageObject mo = new MessageObject();
//            mo.senderId = Constants.Userid;
//            mo.receiverId = mExecutiveId;
//            mo.content = msg;
//            mo.serviceId = mServiceId;
//            GsonBuilder builder = new GsonBuilder();
//            Gson gson = builder.create();
//            try {
//                JSONObject obj = new JSONObject(gson.toJson(mo));
//                mSocket.emit("new message", obj);
//            }catch(JSONException ex){
//                Log.d("JSON Exception", ex.toString());
//            }
//    }
//
//    public void emitCustomerConnect(){
//        CustomerConnect customerConnect  = new CustomerConnect();
//        customerConnect.id = Constants.Userid;
//        customerConnect.name = Constants.Username;
//        customerConnect.serviceRequestId = mServiceId;
//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.create();
//        try {
//            JSONObject obj = new JSONObject(gson.toJson(customerConnect));
//            mSocket.emit("customer connect",obj);
//        }catch(JSONException ex) {
//            Log.d("JSON Exception", ex.toString());
//        }
//    }
}
