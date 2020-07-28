package edu.project.secret_messenger.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import edu.project.secret_messenger.R;

import static android.content.Context.CLIPBOARD_SERVICE;

public class decPopupFragment extends DialogFragment {
    private static final String TAG = "decPopupFragment";
    private TextView plainTextView;
    private Fragment fragment;
    private String plain;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_decpopup,container,false);

        Bundle args = getArguments();
        plain = args.getString("plain");
        Log.e(TAG,"팝업에서 plain = "+plain);
        plainTextView = (TextView) view.findViewById(R.id.dec_popup);
        plainTextView.setText(plain);
        plainTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){ // 복호화된 텍스트뷰 클릭했을 시
                    ClipboardManager cm = (ClipboardManager) inflater.getContext().getSystemService(CLIPBOARD_SERVICE);
                    ClipData cd = ClipData.newPlainText("plain",plain);
                    cm.setPrimaryClip(cd);
                    Toast.makeText(getContext(),"복사되었습니다.", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
        fragment = getActivity().getSupportFragmentManager().findFragmentByTag("tag");
//        if(fragment!=null){
//            DialogFragment dialog = (DialogFragment) fragment;
//            dialog.dismiss();
//        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
