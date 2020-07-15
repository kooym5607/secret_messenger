package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LobbyActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private LobbyViewAdapter lobbyViewAdapter;
    private TabLayout tabLayout;
    private String value;
    private Bundle bundle = new Bundle();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Intent intent = getIntent();

        value = intent.getStringExtra("loginID");
        bundle.putString("loginID",value);
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
