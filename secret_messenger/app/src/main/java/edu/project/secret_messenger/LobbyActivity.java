package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;

import edu.project.secret_messenger.fragment.chatFragment;
import edu.project.secret_messenger.fragment.userlistFragment;
import edu.project.secret_messenger.util.SaveSharedPreference;

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
        chatFragment chatFragment = new chatFragment();
        chatFragment.setArguments(bundle);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        lobbyViewAdapter = new LobbyViewAdapter(getSupportFragmentManager(),userlistFragment,chatFragment);
        viewPager.setAdapter(lobbyViewAdapter);


        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout){
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 1:
                        refresh();
                        break;
                    case 2:
                        refresh();
                        break;
                }

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                refresh();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.usermenu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.logout:
                SaveSharedPreference.logOut(getApplicationContext());
                Intent intent = new Intent(LobbyActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                LobbyActivity.this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void refresh() {
        lobbyViewAdapter.notifyDataSetChanged();
    }
}
