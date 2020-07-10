package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LobbyActivity extends AppCompatActivity {
    private FirebaseAdapter testDB;
    private EditText msgEdit;
    private Button msgSendBtn;
    private TextView msgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        testDB = new FirebaseAdapter(database, myRef);

        msgEdit = (EditText)findViewById(R.id.chat_editText);
        msgSendBtn = (Button)findViewById(R.id.textSendBtn);
        msgTextView = (TextView)findViewById(R.id.message_TextView);

        msgSendBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String str = msgEdit.getText().toString();
                testDB.inputValue(str);
                msgTextView.setText(str);
            }
        });

    }
}
