package edu.project.secret_messenger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.InvalidKeyException;

import static edu.project.secret_messenger.util.*;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private EditText idEdit;
    private EditText pwEdit;
    private Button loginBtn;
    private Button signupBtn;
    private String id;
    private String pw;
    private String loginID;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("user/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        idEdit = (EditText)findViewById(R.id.login_id);
        pwEdit = (EditText)findViewById(R.id.login_password);
        loginBtn = (Button)findViewById(R.id.login_button);
        signupBtn = (Button)findViewById(R.id.signup_button);

        loginBtn.setOnClickListener(new Button.OnClickListener(){ // 로그인 버튼 클릭리스너
            @Override
            public void onClick(View view){
                Log.i(TAG,"로그인 버튼 클릭");
                loginID = idEdit.getText().toString();

                try {
                    pw = hashStr(pwEdit.getText().toString()); // 입력받은 패스워드 해시로 저장
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
                Query query = myRef.orderByChild("id").equalTo(loginID);

                query.addListenerForSingleValueEvent(new ValueEventListener() { // user 내 로그인한 ID 찾기
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()==null) {
                            Toast.makeText(LoginActivity.this.getApplicationContext(), "등록된 ID가 없습니다.\n입력 ID : " + loginID, Toast.LENGTH_SHORT).show();
                            idEdit.setText(null);
                            pwEdit.setText(null);
                        }
                        else {
                            for (DataSnapshot datas : dataSnapshot.getChildren()) { // ID가 존재할 시 그 ID를 키 값으로 설정
                                id = datas.getKey();
                            }

                            myRef.child(id).child("pw").addValueEventListener(new ValueEventListener() { // 입력받은 pw가 id에 있는 pw와 일치한지 확인.
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    isPwEqual(dataSnapshot,pw);
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

        signupBtn.setOnClickListener(new Button.OnClickListener(){ //회원가입 버튼 클릭리스너
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LoginActivity.this, signupActivity.class);
                startActivity(intent);
            }
        });

    }

    private void isPwEqual(DataSnapshot snap, String pw){ // 입력받은 패스워드와 DB에 있는 패스워드를 비교
        if(snap.getValue().equals(pw)){
            Toast.makeText(LoginActivity.this.getApplicationContext(), "로그인 성공.\n ID : " + id, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginActivity.this,LobbyActivity.class);
            intent.putExtra("loginID",id);
            startActivity(intent);
            LoginActivity.this.finish();
        } else {
            Toast.makeText(LoginActivity.this.getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
            pwEdit.setText(null);
        }

    }
}
