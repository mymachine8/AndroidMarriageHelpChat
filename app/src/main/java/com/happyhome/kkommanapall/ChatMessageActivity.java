package com.happyhome.kkommanapall;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.happyhome.kkommanapall.service.ChatMessageService;

public class ChatMessageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);
        Toolbar toolbar = (Toolbar) findViewById(R.id.chattoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*if (savedInstanceState == null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            Fragment fragment = new ChatMessageActivityFragment();
            Bundle b = getIntent().getExtras();
            fragment.setArguments(b);
            ft.add(R.id.chat_message_container, fragment);
            ft.commit();
        }*/
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("ACTIVITYCYCLE","ON STOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ACTIVITYCYCLE", "ON DESTROY");
    }
}
