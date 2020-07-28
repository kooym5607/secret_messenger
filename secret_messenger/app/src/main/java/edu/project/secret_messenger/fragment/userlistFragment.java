package edu.project.secret_messenger.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.project.secret_messenger.R;
import edu.project.secret_messenger.object.User;
import edu.project.secret_messenger.util.SaveSharedPreference;

public class userlistFragment extends Fragment {
    private static final String TAG = "userListFragment";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private ListView listView;
    private ArrayList<User> userArrayList = new ArrayList<User>();
    private String myID;
    private Query query;
    private int listIdx;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_userlist, container, false);

        if(userArrayList!=null)
            userArrayList.clear();
        listView = (ListView)layout.findViewById(R.id.user_listView);
        final UserListAdapter userListAdapter = new UserListAdapter(getActivity(),R.layout.user_list_row,userArrayList);
        listView.setAdapter(userListAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long l) {

            }
        });

        myID = SaveSharedPreference.getId(this.getContext());
        myID = getArguments().getString("myID");
        Log.w(TAG, "로그인한 사용자 : "+myID);
        query = ref.child("user/");

        query.addListenerForSingleValueEvent(new ValueEventListener() { // user 하위 리스트를 배열에 추가
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(userListAdapter!=null)
                    userListAdapter.clear();
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    User user = datas.getValue(User.class);
                    if(user.getId().equals(myID)) {
                        Log.e(TAG, "로그인한 사용자 : "+myID + " 제외");
                        continue;
                    }
                    else {
                        userArrayList.add(user);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG,"Firebase DB 값 불러오는데 실패.", databaseError.toException());
            }
        });

        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                userListAdapter.add(snapshot.getValue(User.class));
                Log.e(TAG, "onchildAdded");
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                final int index = getIndexOfList(snapshot.getKey());

                Log.e(TAG, "유저리스트에서 바뀐 child index : "+index);
                final User user = snapshot.getValue(User.class);
                for(DataSnapshot datas: snapshot.getChildren()){
                    String key = datas.getKey();
                    Log.e(TAG, "유저리스트에서 사용자 정보 키 : "+key);
                    switch(key){
                        case "isLogin": // 사용자 로그인 상태
                            boolean value = datas.getValue(boolean.class);
                            user.setIsLogin(value);
                            userArrayList.set(index,user);
                            break;
                    }
                    userListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return layout;
    }

    public class UserListAdapter extends ArrayAdapter<User> {
        private ArrayList<User> items;
        private User user;

        public UserListAdapter(Context context, int textViewResourceId, ArrayList<User> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.user_list_row,null);
            }
            user = items.get(position);
            if (user != null) {
                TextView name = (TextView) view.findViewById(R.id.toptext);
                ImageView loginStatus = (ImageView) view.findViewById(R.id.login_status);
                if (name != null) {
                    name.setText(user.getName());
                }
                if(user.isIsLogin())
                    loginStatus.setVisibility(View.VISIBLE);
                else
                    loginStatus.setVisibility(View.INVISIBLE);
            }
            return view;
        }
    }



    public userlistFragment() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    private int getIndexOfList(final String key){
        listIdx = 0;
        ref = database.getReference("user");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot datas: snapshot.getChildren()){
                    if(datas.getKey().equals(key))
                        break;
                    else
                        listIdx++;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return listIdx;
    }
}