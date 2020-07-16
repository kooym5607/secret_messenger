package edu.project.secret_messenger.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.project.secret_messenger.R;

public class chatlistFragment extends Fragment {
    private String myID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        myID = getArguments().getString("myID");
        return inflater.inflate(R.layout.fragment_chatlist, container, false);
    }

    public chatlistFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}