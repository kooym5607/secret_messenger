package edu.project.secret_messenger.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.project.secret_messenger.LobbyActivity;
import edu.project.secret_messenger.LoginActivity;
import edu.project.secret_messenger.R;
import edu.project.secret_messenger.chatActivity;
import edu.project.secret_messenger.object.ChatRoom;
import edu.project.secret_messenger.object.User;

public class chatroomlistFragment extends Fragment {
    private static final String TAG = "chatRoomListFragment";
    private String myID;
    private User mUser;
    private String chatUserID;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference("user");
    private Query query;
    private ChatRoom chatRoom;
    private ArrayList<ChatRoom> chatRooms = new ArrayList<ChatRoom>();
    private ListView listView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "ChatRoomListFragment OnCreateView()");
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_chatlist, container, false);
        myID= getArguments().getString("myID");

        query = ref.orderByChild("id").equalTo(myID);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()) {
                    mUser = snap.getValue(User.class);
                }

                ref = ref.child(mUser.getId()).child("chatroom");

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snap) {
                        for(DataSnapshot datas: snap.getChildren()){
                            final String key = datas.getValue(String.class);

                            ref = database.getReference("chatroom/").child(key).child("users");
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    chatRoom = new ChatRoom(mUser);
                                    chatRoom.setRoomUid(key);
                                    for(DataSnapshot snap: snapshot.getChildren()){
                                        if(!snap.getKey().equals(mUser.getId())) {
                                            chatUserID = snap.getValue(String.class);
                                        }
                                    }
                                    chatRoom.setTitle(chatUserID);
                                    chatRooms.add(chatRoom);
                                    listView = (ListView)layout.findViewById(R.id.chat_listView);
                                    ChatRoomListAdapter chatRoomListAdapter = new ChatRoomListAdapter(getActivity(),R.layout.chatroom_list_row,chatRooms);
                                    listView.setAdapter(chatRoomListAdapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            Intent intent = new Intent(getContext(), chatActivity.class);
                                            intent.putExtra("roomID",chatRooms.get(i).getRoomUid());
                                            intent.putExtra("mUserID",myID);

                                            startActivity(intent);
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return layout;
    }


    private class ChatRoomListAdapter extends ArrayAdapter<ChatRoom> {
        private ArrayList<ChatRoom> items;
        private ChatRoom chatRoom;

        public ChatRoomListAdapter(Context context, int textViewResourceId, ArrayList<ChatRoom> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chatroom_list_row,null);
            }
            chatRoom = items.get(position);
            if (chatRoom != null) {
                TextView roomName = (TextView) view.findViewById(R.id.chatroom_name);
                TextView roomMsg = (TextView) view.findViewById(R.id.chatroom_message);
                TextView roomTime = (TextView) view.findViewById(R.id.chatroom_time);

                if (roomName != null) {
                    Log.e(TAG, "roomName: "+chatRoom.getTitle());
                    roomName.setText(chatRoom.getTitle()+"와 채팅");
                }
                if(roomMsg != null){
                    roomMsg.setText("메시지 내용");
                    Log.e(TAG, "roomMsg");
                }
                if(roomTime != null){
                    Date today = new Date();
                    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");

                    roomTime.setText(date.format(today)+" " +time.format(today));
                }
            }

            return view;
        }
    }
    public chatroomlistFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}