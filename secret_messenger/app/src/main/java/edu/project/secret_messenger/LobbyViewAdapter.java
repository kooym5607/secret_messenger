package edu.project.secret_messenger;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

import edu.project.secret_messenger.fragment.chatroomlistFragment;
import edu.project.secret_messenger.fragment.userlistFragment;

public class LobbyViewAdapter extends FragmentPagerAdapter {
    private ArrayList<Fragment> items;

    public LobbyViewAdapter(FragmentManager fm, userlistFragment user, chatroomlistFragment chat){
        super(fm);
        items = new ArrayList<Fragment>();
        items.add(user);
        items.add(chat);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getCount() {
        return items.size();
    }
}
