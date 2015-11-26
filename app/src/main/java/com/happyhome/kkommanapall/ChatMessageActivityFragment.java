package com.happyhome.kkommanapall;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.happyhome.kkommanapall.helper.MessageAdapter;
import com.happyhome.kkommanapall.model.CustomerConnect;
import com.happyhome.kkommanapall.model.MessageObject;
import com.happyhome.kkommanapall.service.ChatMessageService;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * A placeholder fragment containing a simple view.
 */
public class ChatMessageActivityFragment extends Fragment {
    private static final int TYPING_TIMER_LENGTH = 800;

    private EditText messageInput;
    private ListView messagesContainer;
    private ImageButton sendBtn;
    private ChatAdapter adapter;
    private List<MessageObject> mMessages = new ArrayList<MessageObject>();
    private boolean mTyping = false;
    private Handler mTypingHandler = new Handler();
    private String mUsername;
    private BroadcastReceiver mBroadcastReceiver;
    private ChatMessageService messageService;
    private int mExecutiveId;
    private String mExecutiveName;
    private TextView titlebarText;
    private TextView titlebarSubtext;
    private Socket mSocket;
    private int mServiceId;

    public ChatMessageActivityFragment() {
        super();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapter = new ChatAdapter(context, mMessages);
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_message, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initControls(view);
        messageInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == R.id.send || id == EditorInfo.IME_NULL) {
                    attemptSend();
                    return true;
                }
                return false;
            }
        });
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!mTyping) {
                    mTyping = true;
                    emitTyping();
                }

                mTypingHandler.removeCallbacks(onTypingTimeout);
                mTypingHandler.postDelayed(onTypingTimeout, TYPING_TIMER_LENGTH);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        ImageButton sendButton = (ImageButton) view.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSend();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Activity.RESULT_OK != resultCode) {
            getActivity().finish();
            return;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServiceId = getActivity().getIntent().getExtras().getInt("EXTRA_CARESERVICEID");
        initSocket();
    }

    @Override
    public void onStart() {
        super.onStart();
        titlebarText = (TextView) getActivity().findViewById(R.id.toolbar_title);
        titlebarSubtext = (TextView) getActivity().findViewById(R.id.toolbar_subtitle);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mSocket.disconnect();
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("new message", onNewMessage);
        mSocket.off("typing", onTyping);
        mSocket.off("stop typing", onStopTyping);
    }

    @Override
    public void onStop(){
        mSocket.disconnect();
        mSocket.off("new message", onNewMessage);
        super.onStop();
    }

    public void initControls(View rootView){
        messagesContainer = (ListView) rootView.findViewById(R.id.messagesContainer);
        messageInput = (EditText) rootView.findViewById(R.id.message_input);
        sendBtn = (ImageButton) rootView.findViewById(R.id.send_button);
        messagesContainer.setAdapter(adapter);
    }

    public void initSocket(){
        try {
            mSocket = IO.socket(Constants.CHAT_SERVER_URL);
            mSocket.connect();
        } catch (URISyntaxException e) {
            Log.e("URI",e.toString());
        }catch(Exception e){
            Log.e("SocketConnection",e.toString());
        }

        mSocket.on(Socket.EVENT_CONNECT,onSuccessfulConnection);
        mSocket.on("new message", onNewMessage);
        mSocket.on("typing", onTyping);
        mSocket.on("stop typing", onStopTyping);
        mSocket.on("executive ready", onExecutiveReady);
        mSocket.on(Socket.EVENT_ERROR, onErrorConnection);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onErrorConnection);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectionTimeout);
        mSocket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("SocketDisconnet", "Socket Disconnected");
            }
        });
    }

    private Emitter.Listener onConnectionTimeout = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("ConnectionTimeout",args[0].toString());
            // CallActivity(Constants.NEW_MESSAGE, (JSONObject)args[0]);
        }
    };

    private Emitter.Listener onSuccessfulConnection = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            emitCustomerConnect();
            // CallActivity(Constants.NEW_MESSAGE, (JSONObject)args[0]);
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            CallActivity(Constants.NEW_MESSAGE, (JSONObject)args[0]);

        }
    };
    private Emitter.Listener onExecutiveReady = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            try {
                JSONObject obj = (JSONObject)args[0];
                mExecutiveId = obj.getInt("executiveId");
                CallActivity(Constants.EXECUTIVE_READY, (JSONObject)args[0]);
            }catch(Exception ex){

            }
        }
    };

    private Emitter.Listener onTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            CallActivity(Constants.TYPING, (JSONObject)args[0]);
        }
    };

    private Emitter.Listener onStopTyping = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            CallActivity(Constants.STOP_TYPING, (JSONObject)args[0]);
        }
    };

    private void CallActivity(String tag,JSONObject msg){
        String message = msg.toString();
        switch(tag){
            case Constants.NEW_MESSAGE: newMessage(message);
                break;
            case Constants.TYPING:  typing();
                break;
            case Constants.STOP_TYPING: stoptyping();
                break;
            case Constants.EXECUTIVE_READY: executiveReady(message);
                break;
        }
    }
    public void emitTyping(){
        MessageObject mo = new MessageObject();
        mo.senderId = Constants.Userid;
        mo.receiverId = mExecutiveId;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONObject obj = new JSONObject(gson.toJson(mo));
            mSocket.emit("typing",obj);
        }catch(JSONException ex){
            Log.d("JSON Exception", ex.toString());
        }
    }

    public void emitStopTyping(){
        MessageObject mo = new MessageObject();
        mo.senderId = Constants.Userid;
        mo.receiverId = mExecutiveId;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONObject obj = new JSONObject(gson.toJson(mo));
            mSocket.emit("stop typing", obj);
        }catch(JSONException ex){
            Log.d("JSON Exception", ex.toString());
        }
    }

    public void emitNewMessage(String msg){
        MessageObject mo = new MessageObject();
        mo.senderId = Constants.Userid;
        mo.receiverId = mExecutiveId;
        mo.content = msg;
        mo.serviceId = mServiceId;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONObject obj = new JSONObject(gson.toJson(mo));
            mSocket.emit("new message", obj);
        }catch(JSONException ex){
            Log.d("JSON Exception", ex.toString());
        }
    }

    public void emitCustomerConnect(){
        CustomerConnect customerConnect  = new CustomerConnect();
        customerConnect.id = Constants.Userid;
        customerConnect.name = Constants.Username;
        customerConnect.serviceRequestId = mServiceId;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        try {
            JSONObject obj = new JSONObject(gson.toJson(customerConnect));
            mSocket.emit("customer connect",obj);
        }catch(JSONException ex) {
            Log.d("JSON Exception", ex.toString());
        }
    }

    private Emitter.Listener onErrorConnection = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Log.e("ErrorConnection",args[0].toString());
        }
    };

    private void removeTyping() {
        titlebarSubtext.setText("");
    }

    private void addMessage(MessageObject message) {

        adapter.add(message);
        adapter.notifyDataSetChanged();
        scrollToBottom();
    }

    private void attemptSend() {
        mTyping = false;

        String message = messageInput.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            messageInput.requestFocus();
            return;
        }

        messageInput.setText("");
        MessageObject mo = new MessageObject();
        mo.content = message;
        mo.senderId = Constants.Userid;
        addMessage(mo);

        // perform the sending message attempt.
        emitNewMessage(message);
    }

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Connection Error", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void scrollToBottom() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void addTyping() {
        titlebarSubtext.setText("typing...");
    }

    private void executiveReady(final String messsage){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject data = new JSONObject(messsage);
                    mExecutiveId = data.getInt("executiveId");
                    mExecutiveName = data.getString("executiveName");
                    titlebarText.setText(mExecutiveName);
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        });
    }

    public void newMessage(final String msg) {
        if(msg == null)
                return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MessageObject mo = new MessageObject();
                try {
                    JSONObject data = new JSONObject(msg);

                    mo.senderId = data.getInt("senderId");
                    mo.content = data.getString("content");
                } catch (JSONException e) {
                    return;
                }
                removeTyping();
                addMessage(mo);
            }
        });
    }

    private Runnable onTypingTimeout = new Runnable() {
        @Override
        public void run() {
            if (!mTyping) return;

            mTyping = false;
            emitStopTyping();
        }
    };

    public void typing() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addTyping();
            }
        });
    }

//    private class TypingTask extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            addTyping();
//            return null;
//        }
//    }

    public void stoptyping() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeTyping();
            }
        });
    }


}
