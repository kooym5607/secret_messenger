package edu.project.secret_messenger;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.ListFragment;

import android.renderscript.Sampler;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class userlistFragment extends Fragment {
    private static final String TAG = "userListFragment";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private FirebaseAdapter DB;
    private ListView listView;
    private ArrayList<User> userArrayList = new ArrayList<User>();;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_userlist, container, false);
        ref = database.getReference();
        DB = new FirebaseAdapter(database,ref);

        ref.child("user/").addListenerForSingleValueEvent(new ValueEventListener() { // user 하위 리스트를 배열에 추가
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    User user = datas.getValue(User.class);
                    userArrayList.add(user);
                }
                listView = (ListView)layout.findViewById(R.id.user_listView);
                UserListAdapter userListAdapter = new UserListAdapter(getActivity(),R.layout.user_list_row,userArrayList);
                listView.setAdapter(userListAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG,"Firebase DB 값 불러오는데 실패.", databaseError.toException());
            }
        });



        return layout;
    }

    private class UserListAdapter extends ArrayAdapter<User> {

        private ArrayList<User> items;

        public UserListAdapter(Context context, int textViewResourceId, ArrayList<User> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.user_list_row,null);
            }
            User p = items.get(position);
            if (p != null) {
                TextView name = (TextView) v.findViewById(R.id.toptext);
                TextView id = (TextView) v.findViewById(R.id.bottomtext);
                if (name != null){
                    Log.e(TAG, "리스트 setName");
                    name.setText(p.getName());
                }
                if(id != null){
                    Log.e(TAG, "리스트 setID");
                    id.setText("ID: "+ p.getId());
                }
            }
            return v;
        }
    }



    public userlistFragment() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}