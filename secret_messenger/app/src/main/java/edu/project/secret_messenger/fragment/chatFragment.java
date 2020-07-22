package edu.project.secret_messenger.fragment;

import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.project.secret_messenger.ARIA_CBC.Aria_CBC;
import edu.project.secret_messenger.R;
import edu.project.secret_messenger.object.ChatDTO;

public class chatFragment extends Fragment {
    private static final String TAG = "chatFragment";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private Query query;
    private ListView listView;
    private ChatDTO chatDTO;
    private ArrayList<ChatDTO> chatDTOs = new ArrayList<ChatDTO>();
    private ChatListAdapter chatListAdapter;
    private String myID;
    private String mUserName;
    private EditText chatEdit;
    private EditText encKeyEdit;
    private String message;
    private Button sendButton;
    private Date msg_Time;
    private CheckBox enc_CheckBox;
    private String encKey;
    private boolean is_Enc;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_chatlist, container, false);
        listView = (ListView)layout.findViewById(R.id.chat_listView);
        chatListAdapter = new ChatListAdapter(getContext(),R.layout.chat_list_row,chatDTOs);
        listView.setAdapter(chatListAdapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e(TAG,"listview LongClick");

                String msgUid =chatDTOs.get(position).getMsgUID();
                chatDTOs.remove(position);
                chatListAdapter.notifyDataSetChanged();

                ref = database.getReference("messages").child(msgUid);
                ref.removeValue();

                listView.clearChoices();
                return true;
            }

        });

        enc_CheckBox = (CheckBox)layout.findViewById(R.id.enc_check);


        myID = getArguments().getString("myID");
        ref = database.getReference();
        query= ref.child("user").child(myID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserName = snapshot.child("name").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        encKeyEdit = (EditText)layout.findViewById(R.id.encKey);
        chatEdit = (EditText)layout.findViewById(R.id.chat_edit);
        sendButton = (Button)layout.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(enc_CheckBox.isChecked())
                    is_Enc = true;
                else
                    is_Enc = false;
                ref = database.getReference("messages");
                String msgUid = ref.push().getKey();
                message = chatEdit.getText().toString();
                if(is_Enc==true){
                    encKey = encKeyEdit.getText().toString();
                    /** Todo
                     * ARIA_CBC를 이용하여 message 암호화.
                     */
                }

                msg_Time = new Date();

                Log.e(TAG,"시간은 : "+msg_Time);
                chatDTO = new ChatDTO(msgUid,myID,message,mUserName,msg_Time,is_Enc);

                Map<String,Object> chatValues = chatDTO.toMap();
                Map<String,Object> childUpdates = new HashMap<>();
                childUpdates.put(msgUid,chatValues);
                ref.updateChildren(childUpdates);

                chatEdit.setText("");
                enc_CheckBox.setChecked(false);
            }
        });
        ref = database.getReference("messages");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(chatListAdapter!=null)
                    chatListAdapter.clear();
                for(DataSnapshot datas: snapshot.getChildren()){
                    chatListAdapter.add(datas.getValue(ChatDTO.class));
                }
                chatListAdapter.registerDataSetObserver(new DataSetObserver() {
                    @Override
                    public void onChanged() {
                        super.onChanged();
                        listView.setSelection(chatListAdapter.getCount()-1);
                    }
                });
                listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                chatListAdapter.add(snapshot.getValue(ChatDTO.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                chatListAdapter.remove(snapshot.getValue(ChatDTO.class));
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
            Log.e(TAG,"getView");
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
                    if(chatDTO.getIs_Enc()==false){

                        chatMsg.setText(chatDTO.getMessage());
                    }
                    else {
                        Log.e(TAG,"비밀메시지");
                        chatMsg.setText("비밀메시지입니다.");
                    }
                }
                if(userName != null){
                    userName.setText(chatDTO.getUserName());
                }
                if(chatTime!=null){
                    SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat time = new SimpleDateFormat("hh:mm:ss a");
                    String this_Time = date.format(chatDTO.getMsg_Time())+" " +time.format(chatDTO.getMsg_Time());
                    chatTime.setText(this_Time);
                }
            }

            return view;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}
