package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

import edu.project.secret_messenger.fragment.chatlistFragment;
import edu.project.secret_messenger.fragment.userlistFragment;

public class LobbyActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LobbyViewAdapter lobbyViewAdapter;
    private TabLayout tabLayout;
    private Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Intent intent = getIntent();
        String value = intent.getStringExtra("myID");
        bundle.putString("myID",value);
        userlistFragment userlistFragment = new userlistFragment();
        userlistFragment.setArguments(bundle);
        chatlistFragment chatlistFragment = new chatlistFragment();
        chatlistFragment.setArguments(bundle);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        lobbyViewAdapter = new LobbyViewAdapter(getSupportFragmentManager(),userlistFragment,chatlistFragment);
        viewPager.setAdapter(lobbyViewAdapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
