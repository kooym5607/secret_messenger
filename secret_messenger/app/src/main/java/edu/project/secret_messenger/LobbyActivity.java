package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.project.secret_messenger.fragment.chatFragment;
import edu.project.secret_messenger.fragment.userlistFragment;
import edu.project.secret_messenger.util.OnBackPressedListener;
import edu.project.secret_messenger.util.SaveSharedPreference;

public class LobbyActivity extends AppCompatActivity implements OnBackPressedListener {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private static final String TAG = "LobbyActivity";
    private ViewPager viewPager;
    private LobbyViewAdapter lobbyViewAdapter;
    private TabLayout tabLayout;
    private userlistFragment userlistFragment = new userlistFragment();
    private chatFragment chatFragment = new chatFragment();
    long backPressedTime = 0;
    private String myID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        Intent intent = getIntent();
        myID = intent.getStringExtra("myID");
        Bundle bundle = new Bundle();
        bundle.putString("myID",myID);
        chatFragment.setArguments(bundle);
        userlistFragment.setArguments(bundle);

        String noti = intent.getStringExtra("pendingIntent");

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        lobbyViewAdapter = new LobbyViewAdapter(getSupportFragmentManager(),userlistFragment,chatFragment);
        viewPager.setAdapter(lobbyViewAdapter);

        if(noti!=null)
            if(noti.equals("notiIntent")) {
                viewPager.setCurrentItem(1);
                lobbyViewAdapter.notifyDataSetChanged();
            }

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
                SaveSharedPreference.logOut(getApplicationContext(), myID);
                Toast.makeText(getApplicationContext(), "종료합니다",Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void refresh() {
        lobbyViewAdapter.notifyDataSetChanged();
    }


    @Override
    public void onResume(){
        super.onResume();

    }
    @Override
    public void onBackPressed(){
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && 2000 >= intervalTime)
        {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("user").child(myID).child("isLogin");
            ref.setValue(false);
            android.os.Process.killProcess(android.os.Process.myPid());
            super.onBackPressed();
        }
        else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }

    }
}
