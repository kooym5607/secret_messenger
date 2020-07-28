package edu.project.secret_messenger.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import edu.project.secret_messenger.R;
import edu.project.secret_messenger.fragment.chatFragment;

public class decPopupFragment extends DialogFragment {
    private static final String TAG = "decPopupFragment";
    private TextView plainTextView;
    private Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decpopup,container,false);

        Bundle args = getArguments();
        String plain = args.getString("plain");
        Log.e(TAG,"팝업에서 plain = "+plain);
        plainTextView = (TextView) view.findViewById(R.id.dec_popup);
        plainTextView.setText(plain);
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
        if(fragment!=null){
            DialogFragment dialog = (DialogFragment) fragment;
            dialog.dismiss();
        }
        return view;
    }
}
