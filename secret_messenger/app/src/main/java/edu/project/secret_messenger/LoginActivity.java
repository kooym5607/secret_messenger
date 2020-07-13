package edu.project.secret_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.InvalidKeyException;

import static edu.project.secret_messenger.util.*;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAdapter testDB;
    private EditText idEdit;
    private EditText pwEdit;
    private Button loginBtn;
    private Button signupBtn;
    private String id;
    private String pw;
    private String loginId;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("user/");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        testDB = new FirebaseAdapter(database, myRef);


        idEdit = (EditText)findViewById(R.id.login_id);
        pwEdit = (EditText)findViewById(R.id.login_password);
        loginBtn = (Button)findViewById(R.id.login_button);
        signupBtn = (Button)findViewById(R.id.signup_button);


        loginBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
               id = idEdit.getText().toString();

                try {
                    pw = hashStr(pwEdit.getText().toString());
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                myRef.orderByChild("id").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null)
                            Toast.makeText(LoginActivity.this.getApplicationContext(), "등록된 ID가 없습니다.\n", Toast.LENGTH_SHORT).show();
                        else {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) {
                                loginId = datas.getKey();
                                myRef = myRef.child(id);
                            }
                            myRef.child("pw").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue().equals(pw)) {
                                        Toast.makeText(LoginActivity.this.getApplicationContext(), "로그인 성공.\n ID : " + id, Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,LobbyActivity.class));
                                        LoginActivity.this.finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this.getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        signupBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, signupActivity.class);
                startActivity(intent);
            }
        });

    }
}
