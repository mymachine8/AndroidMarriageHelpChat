package com.happyhome.kkommanapall;

import android.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    private static final int TAB_CHAT = 0;
    private static final int TAB_ABOUT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("Chat"),TAB_CHAT);
        tabLayout.addTab(tabLayout.newTab().setText("About"),TAB_ABOUT);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == TAB_CHAT) {
                    createChatFragment();
                }
                if (tab.getPosition() == TAB_ABOUT) {

                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

        });
        createChatFragment();
    }

    private void createChatFragment(){
        FragmentTransaction ft;
        ChatFragment fragment = new ChatFragment();
        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_container,fragment);
        ft.commit();
    }

}
