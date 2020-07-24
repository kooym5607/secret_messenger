package edu.project.secret_messenger;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
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
import java.util.regex.Pattern;

import edu.project.secret_messenger.object.User;

import static edu.project.secret_messenger.util.*;

public class signupActivity extends AppCompatActivity {
    private EditText idEdit;
    private EditText pwEdit;
    private EditText nameEdit;
    private Button signupFinishBtn;
    private Button sameidBtn;
    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private DatabaseReference ref;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        idEdit = (EditText)findViewById(R.id.id_signup);
        idEdit.setFilters(new InputFilter[]{filterAlphaNum});
        pwEdit = (EditText)findViewById(R.id.pw_signup);
        pwEdit.setFilters(new InputFilter[]{filterAlphaNum});
        nameEdit = (EditText)findViewById(R.id.name_signup);
        nameEdit.setFilters(new InputFilter[]{filterKoEnNum2});

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
                    ref.setValue(user);

                    Toast.makeText(signupActivity.this.getApplicationContext(),"회원가입 성공\n" + name + " 회원님 반갑습니다.",Toast.LENGTH_SHORT).show();
                    finish();
                }
                else
                    Toast.makeText(signupActivity.this.getApplicationContext(),"회원가입 실패\n아이디,비밀번호,이름 중 기입하지 않은 것이 있습니다.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hasSameId(String id){
        Query query = database.getReference("user/").orderByChild("id").equalTo(id);

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
    protected InputFilter filterAlphaNum = new InputFilter() { //영어, 숫자 사용. 띄어쓰기 불가.
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");

            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };
    protected InputFilter filterKoEnNum2 = new InputFilter() { // 한글,영어,숫자 사용. 띄어쓰기 허용
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if(source.equals("")){ // for backspace
                return source;
            }
            if(source.toString().matches("[a-z0-9ㄱ-ㅎ가-힣 ]+")){
                return source;
            }

            return "";
        }
    };

}
