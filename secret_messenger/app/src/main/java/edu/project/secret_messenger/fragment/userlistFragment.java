package edu.project.secret_messenger.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.project.secret_messenger.R;
import edu.project.secret_messenger.object.ChatRoom;
import edu.project.secret_messenger.object.User;

public class userlistFragment extends Fragment {
    private static final String TAG = "userListFragment";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private ListView listView;
    private ArrayList<User> userArrayList = new ArrayList<User>();
    private String myID;
    private Query query;
    private User mUser;
    private User chatUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_userlist, container, false);
        myID = getArguments().getString("myID");
        Log.w(TAG, "로그인한 사용자 : "+myID);
        query = database.getReference().child("user/");

        query.addListenerForSingleValueEvent(new ValueEventListener() { // user 하위 리스트를 배열에 추가
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    User user = datas.getValue(User.class);

                    if(user.getId().equals(myID)) {
                        Log.e(TAG, "로그인한 사용자 : "+myID + " 제외");
                        mUser = user;
                        continue;
                    }
                    else
                        userArrayList.add(user);
                }
                listView = (ListView)layout.findViewById(R.id.user_listView);
                UserListAdapter userListAdapter = new UserListAdapter(getActivity(),R.layout.user_list_row,userArrayList);
                listView.setAdapter(userListAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(final AdapterView<?> parent, View view, int position, long l) {
                        ListView listView = (ListView) parent;
                        chatUser = (User) listView.getItemAtPosition(position);
                        PopupMenu popup = new PopupMenu(parent.getContext(),listView);
                        MenuInflater inf = popup.getMenuInflater();
                        inf.inflate(R.menu.usermenu, popup.getMenu());

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                Toast.makeText(parent.getContext(), chatUser.getName()+"과 채팅하기", Toast.LENGTH_SHORT).show();
                                /**
                                 * 사용자 채팅 클릭이벤트
                                 */
                                Map<String, String> users = new HashMap<>();
                                users.put(mUser.getId(),mUser.getName());
                                users.put(chatUser.getId(),chatUser.getName());

                                ChatRoom chatRoom = new ChatRoom(users);

                                ref = database.getReference().child("chatroom");
                                String key = ref.push().getKey();
                                chatRoom.setRoomUid(key);
                                Map<String, Object> roomValues = chatRoom.toMap();
                                Map<String, Object> childUpdates = new HashMap<>();
                                childUpdates.put(key, roomValues);
                                ref.updateChildren(childUpdates);
                                Map<String, Object> chatroomUpdate = new HashMap<>();
                                chatroomUpdate.put(key,key);

                                ref = database.getReference("user").child(mUser.getId()).child("chatroom");
                                ref.updateChildren(chatroomUpdate);
                                ref = database.getReference("user").child(chatUser.getId()).child("chatroom");
                                ref.updateChildren(chatroomUpdate);
                                return false;
                            }
                        });
                        popup.show();
                    }
                });
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
                if (name != null) {
                    name.setText(user.getName());
                }
            }

            return view;
        }
    }



    public userlistFragment() { }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


}