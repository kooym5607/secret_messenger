package edu.project.secret_messenger.fragment;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import edu.project.secret_messenger.ARIA_CBC.Aria_CBC;
import edu.project.secret_messenger.LobbyActivity;
import edu.project.secret_messenger.R;
import edu.project.secret_messenger.object.ChatDTO;
import edu.project.secret_messenger.util.SaveSharedPreference;

import static android.content.Context.NOTIFICATION_SERVICE;

public class chatFragment extends Fragment {
    private static final String TAG = "chatFragment";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref;

    private EditText chatEdit;
    private EditText encKeyEdit;
    private CheckBox enc_CheckBox;
    private Button sendButton;
    private ListView listView;
    private NotificationManager notificationManager;
    private Notification noti;
    private NotificationChannel notificationChannel;

    private ChatDTO chatDTO;
    private ArrayList<ChatDTO> chatDTOs = new ArrayList<ChatDTO>();
    private ChatListAdapter chatListAdapter;
    private String myID;
    private String mUserName;
    private String message;
    private String plainText;
    private Date msg_Time;
    private String encKey;
    private boolean is_Enc;
    private Aria_CBC aria;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.fragment_chatlist, container, false);
        listView = (ListView)layout.findViewById(R.id.chat_listView);
        if(chatDTOs!=null)
            chatDTOs.clear();
        chatListAdapter = new ChatListAdapter(getContext(),R.layout.chat_list_row,chatDTOs);
        listView.setAdapter(chatListAdapter);
        chatListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatListAdapter.getCount()-1);
            }
        });
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        enc_CheckBox = (CheckBox)layout.findViewById(R.id.enc_check);
        encKeyEdit = (EditText)layout.findViewById(R.id.encKey);
        encKeyEdit.setFilters(new InputFilter[]{new ByteLengthFilter(16,"EUC-KR")});
        chatEdit = (EditText)layout.findViewById(R.id.chat_edit);
        sendButton = (Button)layout.findViewById(R.id.sendButton);
        // 뷰 id 지정

        myID = SaveSharedPreference.getId(this.getContext());
        myID = getArguments().getString("myID");
        ref = database.getReference("user").child(myID);
        ref.addListenerForSingleValueEvent(new ValueEventListener() { // 사용자 ID로부터 이름을 받아옴
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUserName = snapshot.child("name").getValue(String.class);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        ref = database.getReference("messages");

        ref.addChildEventListener(new ChildEventListener() { // 채팅이 추가/삭제 될 때 마다 리스트어댑터에 추가/삭제
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() { // 채팅 길게 클릭했을 때 클릭 리스너
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.e(TAG,"listview LongClick");

                final int pos = position;
                final CharSequence[] items = {"삭제","비밀메시지 보기"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch(i){
                            case 0: // 삭제
                                if(chatDTOs.get(pos).getUserID().equals(myID)){ // 선택한 채팅이 사용자가 작성한 것인지 확인 후 삭제.
                                    String msgUid =chatDTOs.get(pos).getMsgUID();
                                    chatDTOs.remove(pos);
                                    chatListAdapter.notifyDataSetChanged();

                                    ref = database.getReference("messages").child(msgUid);
                                    ref.removeValue();

                                    listView.clearChoices();
                                }
                                else
                                    Toast.makeText(getContext(), "이 채팅의 사용자가 아니여서 삭제 불가", Toast.LENGTH_SHORT).show();
                                break;
                            case 1: // 비밀메시지 보기
                                if(chatDTOs.get(pos).getIs_Enc()==true){ // 비밀메시지인지 확인 후 복호화
                                    final EditText decKeytext = new EditText(getView().getContext());
                                    final AlertDialog.Builder decKeyDialog = new AlertDialog.Builder(getView().getContext(),android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                    final AlertDialog mDialog = decKeyDialog.create();

                                    decKeyDialog.setTitle("비밀번호");
                                    decKeyDialog.setView(decKeytext);

                                    decKeyDialog.setPositiveButton("입력",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    /**
                                                     * Todo 비밀키가 틀렸을 시에 그냥 암호문으로 나올지, 알려줄 지
                                                     */
                                                    encKey = decKeytext.getText().toString();
                                                    aria = new Aria_CBC(encKey);
                                                    String cipherMsg = chatDTOs.get(pos).getMessage();
                                                    plainText=aria.Decrypt(cipherMsg);
                                                    if(plainText!=null)
                                                        showDecChat(plainText);
                                                    mDialog.dismiss();
                                                }
                                            });
                                    decKeyDialog.show();

                                }
                                else
                                    Toast.makeText(getContext(), "비밀메시지가 아닙니다", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.create().show();



                return true;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() { // 전송 버튼 클릭 리스너
            @Override
            public void onClick(View view) {
                if(enc_CheckBox.isChecked())
                    is_Enc = true;
                else
                    is_Enc = false;

                ref = database.getReference("messages");
                String msgUid = ref.push().getKey();
                message = chatEdit.getText().toString();
                msg_Time = new Date();
                if(is_Enc==true){ // 비밀메시지 체크가 되어있으면 암호화하여 DB에 저장.
                    encKey = encKeyEdit.getText().toString();
                    aria = new Aria_CBC(encKey);
                    String cipher = aria.Encrypt(message);
                    chatDTO = new ChatDTO(msgUid,myID,cipher,mUserName,msg_Time,is_Enc);
                }
                else
                    chatDTO = new ChatDTO(msgUid,myID,message,mUserName,msg_Time,is_Enc);

                Map<String,Object> chatValues = chatDTO.toMap();
                Map<String,Object> childUpdates = new HashMap<>();
                childUpdates.put(msgUid,chatValues);
                ref.updateChildren(childUpdates);


                msgNoti(getContext(),chatDTO);
                if(!chatDTO.getUserID().equals(myID))
                    notifi(notificationManager,noti);
                chatEdit.setText("");
                encKeyEdit.setText("");
                enc_CheckBox.setChecked(false);
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
                        view.setBackgroundResource(R.drawable.white_edittext);
                    }
                    else {
                        chatMsg.setText("비밀메시지입니다.");
                        view.setBackgroundResource(R.drawable.red_edittext);
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


    /**
     * Todo 자신이 보낸 메세지는 알림 x
     */
    public void msgNoti(Context context, ChatDTO chatDTO){
        String notiUserName = chatDTO.getUserName();
        String notiMessage = chatDTO.getMessage();
        boolean noti_isEnc = chatDTO.getIs_Enc();
        if(noti_isEnc)
            notiMessage = "비밀 메시지";

        notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationChannel = new NotificationChannel("noti_channel","channel",NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.setDescription("알림 테스트");
        notificationManager.createNotificationChannel(notificationChannel);
        Intent notiIconClickIntent = new Intent(context,LobbyActivity.class);
        notiIconClickIntent.putExtra("pendingIntent","notiIntent");
        notiIconClickIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(LobbyActivity.class);
        stackBuilder.addNextIntent(notiIconClickIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        noti = new NotificationCompat.Builder(context,"noti_channel")
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.drawable.noti_icon)
                .setContentTitle(notiUserName)
                .setContentText(notiMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
    }

    private void notifi(NotificationManager ntm, Notification noti){
        ntm.notify(1234,noti);
    }
    private void showDecChat(String msg){
        Log.e(TAG, "showDecChat");
        Bundle bundle = new Bundle();
        bundle.putString("plain",msg);
        decPopupFragment dialog = new decPopupFragment();
        dialog.setArguments(bundle);
        dialog.show(this.getActivity().getSupportFragmentManager(),"tag");
    }
}
class ByteLengthFilter implements InputFilter {
    private String mCharset; // 인코딩 문자셋
    protected int mMaxByte; // 입력가능한 최대 바이트 길이
    public ByteLengthFilter(int maxbyte, String charset) {
        this.mMaxByte = maxbyte;
        this.mCharset = charset;
    }

    /**
     * 이 메소드는 입력/삭제 및 붙여넣기/잘라내기할 때마다 실행된다. - source : 새로 입력/붙여넣기 되는
     * 문자열(삭제/잘라내기 시에는 "") - dest : 변경 전 원래 문자열
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                               int dstart,int dend) {
        // 변경 후 예상되는 문자열
        String expected = new String();
        expected += dest.subSequence(0, dstart);
        expected += source.subSequence(start, end);
        expected += dest.subSequence(dend, dest.length());
        int keep = calculateMaxLength(expected) - (dest.length() - (dend - dstart));
        if(keep < 0) //keep -값이 나올경우를 대비한 방어코드
        {
            keep = 0;
        }
        int Rekeep = plusMaxLength(dest.toString(), source.toString(), start);

        if (keep <= 0 && Rekeep <= 0) {
            return ""; // source 입력 불가(원래 문자열 변경 없음)

        } else if (keep >= end - start) {
            return null; // keep original. source 그대로 허용
        } else {
            if( dest.length() == 0 && Rekeep <= 0 ) //기존의 내용이 없고, 붙여넣기 하는 문자바이트가 71바이트를 넘을경우
            {
                return source.subSequence(start, start + keep);
            }
            else if(Rekeep <= 0)  //엔터가 들어갈 경우 keep이 0이 되어버리는 경우가 있음
            {
                return source.subSequence(start, start + (source.length()-1));
            }
            else
            {
                return source.subSequence(start, start + Rekeep); // source중 일부만입력 허용
            }
        }
    }
    /**
     * 붙여넣기 시 최대입력가능한 길이 수 구하는 함수
     * 숫자와 문자가 있을경우 길이수의 오차가 있음. While문으로 오차범위를 줄여줌
     * @param expected  : 현재 입력되어 있는 문자
     * @param source    : 붙여넣기 할 문자
     * @param start
     * @return
     */
    protected int plusMaxLength( String expected, String source, int start ) {
        int keep = source.length();
        int maxByte = mMaxByte - getByteLength(expected.toString()); //입력가능한 byte

        while (getByteLength(source.subSequence(start, start + keep).toString()) > maxByte) {
            keep--;
        };
        return keep;
    }

    /**
     * 입력가능한 최대 문자 길이(최대 바이트 길이와 다름!).
     */

    protected int calculateMaxLength(String expected) {
        int expectedByte = getByteLength(expected);
        if (expectedByte == 0) {
            return 0;
        }
        return mMaxByte - (getByteLength(expected) - expected.length());
    }

    /**
     * 문자열의 바이트 길이. 인코딩 문자셋에 따라 바이트 길이 달라짐.
     *
     * @param str
     * @return
     */

    private int getByteLength(String str) {
        try {
            return str.getBytes(mCharset).length;

        } catch (UnsupportedEncodingException e) {

        }
        return 0;
    }
}




