package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static edu.project.secret_messenger.FirebaseAdapter.*;
import static edu.project.secret_messenger.util.*;
import static edu.project.secret_messenger.ARIA_CBC.ARIA.*;
import static edu.project.secret_messenger.ARIA_CBC.Aria_CBC.*;

public class MainActivity extends AppCompatActivity {
    private FirebaseAdapter firebaseAdapter;
    private EditText msgEdit;
    private Button msgSendBtn;
    private TextView msgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        firebaseAdapter = new FirebaseAdapter(database, myRef);

        msgEdit = (EditText)findViewById(R.id.chat_editText);
        msgSendBtn = (Button)findViewById(R.id.textSendBtn);
        msgTextView = (TextView)findViewById(R.id.message_TextView);

        msgSendBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String str = msgEdit.getText().toString();
                firebaseAdapter.inputValue(str);
                msgTextView.setText(str);
            }
        });

    }
}
