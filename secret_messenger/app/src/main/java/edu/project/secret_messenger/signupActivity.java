package edu.project.secret_messenger;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.security.InvalidKeyException;

import static edu.project.secret_messenger.util.*;

public class signupActivity extends AppCompatActivity {
    private EditText idEdit;
    private EditText pwEdit;
    private EditText nameEdit;
    private Button signupFinishBtn;
    private Button sameidBtn;
    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private FirebaseAdapter DB;
    private DatabaseReference ref = database.getReference();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        idEdit = (EditText)findViewById(R.id.id_signup);
        pwEdit = (EditText)findViewById(R.id.pw_signup);
        nameEdit = (EditText)findViewById(R.id.name_signup);
        sameidBtn = (Button)findViewById(R.id.idsameBtn);
        sameidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    hasSameId(hashStr(idEdit.getText().toString()));
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });
        signupFinishBtn = (Button)findViewById(R.id.signupFinishBtn);
        signupFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isNull = false; // 입력 값 중 공백이 있으면 TRUE
                String id = idEdit.getText().toString();
                String pw = pwEdit.getText().toString();
                String name = nameEdit.getText().toString();


                if(id.equals("")||pw.equals("")||name.equals("")) { // 입력칸에 빈칸이 있는지 체크
                    isNull = true;
                }
                if(isNull==false) {
                    try {
                        id = hashStr(id);
                        pw = hashStr(pw);
                        user = new User(id,pw,name); // user 객체 생성
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    ref = database.getReference("user/").child(id);
                    DB = new FirebaseAdapter(database,ref);
                    DB.inputValue(user);

                    Toast.makeText(signupActivity.this.getApplicationContext(),"회원가입 성공\n" + name + " 회원님 반갑습니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(signupActivity.this.getApplicationContext(),"회원가입 실패\n아이디,비밀번호,이름 중 기입하지 않은 것이 있습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hasSameId(String id){
        ref = database.getReference("user/");
        Query query = ref.orderByChild("id").equalTo(id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                    Toast.makeText(signupActivity.this.getApplicationContext(),"아이디가 존재합니다.",Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(signupActivity.this.getApplicationContext(),"입력하신 ID로 가능합니다.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
