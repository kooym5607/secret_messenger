package edu.project.secret_messenger;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import edu.project.secret_messenger.fragment.chatroomlistFragment;
import edu.project.secret_messenger.object.ChatDTO;
import edu.project.secret_messenger.object.ChatRoom;
import edu.project.secret_messenger.object.User;

public class chatActivity extends AppCompatActivity {
    private static final String TAG = "chatActivity";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private Query query;
    private ListView listView;
    private ChatDTO chatDTO;
    private ArrayList<ChatDTO> chatDTOs = new ArrayList<ChatDTO>();
    private ChatListAdapter chatListAdapter;
    private String roomID;
    private String mUserID;
    private String mUserName;
    private EditText chatEdit;
    private String message;
    private Button sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatmessage);
        roomID = getIntent().getStringExtra("roomID");
        mUserID = getIntent().getStringExtra("mUserID");
        ref = database.getReference();

        query= ref.child("user").child(mUserID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserName = snapshot.child("name").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.e(TAG,"유저 이름 : "+mUserName);
        chatEdit = (EditText)findViewById(R.id.chat_edit);

        query = database.getReference("messages/").child(roomID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot datas:snapshot.getChildren()){
                    chatDTO = datas.getValue(ChatDTO.class);
                    chatDTOs.add(chatDTO);
                }
                listView = (ListView)findViewById(R.id.chatmessage_listView);
                chatListAdapter = new ChatListAdapter(getApplicationContext(),R.layout.chat_list_row,chatDTOs);
                listView.setAdapter(chatListAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendButton = (Button)findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msgUid = ref.child("messages").push().getKey();
                message = chatEdit.getText().toString();
                chatDTO = new ChatDTO(mUserID,message,mUserName);
                Map<String,Object> chatValues = chatDTO.toMap();
                Map<String,Object> childUpdates = new HashMap<>();
                childUpdates.put("/messages/"+roomID+"/"+msgUid+"/",chatValues);
                ref.updateChildren(childUpdates);
                chatEdit.setText("");
                chatListAdapter.notifyDataSetChanged();
                chatListAdapter.clear();
            }
        });






    }

    private class ChatListAdapter extends ArrayAdapter<ChatDTO> {
        private ArrayList<ChatDTO> items;
        private ChatDTO chatDTO;

        public ChatListAdapter(Context context, int textViewResourceId, ArrayList<ChatDTO> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }
        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.chat_list_row,null);
            }
            chatDTO = items.get(position);
            if (chatDTO != null) {
                TextView userName = (TextView) view.findViewById(R.id.chat_userName);
                TextView chatMsg = (TextView) view.findViewById(R.id.chat_message);
                TextView chatTime = (TextView) view.findViewById(R.id.chat_time);

                if(chatMsg != null){
                    chatMsg.setText(chatDTO.getMessage());
                }
                if(userName != null){
                    userName.setText(chatDTO.getUserName());
                }
                if(chatTime!=null){
                    Date today = new Date();
                    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                    chatTime.setText(date.format(today)+" " +time.format(today));
                }

            }

            return view;
        }
    }

}
