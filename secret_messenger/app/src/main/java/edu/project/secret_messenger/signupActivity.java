package edu.project.secret_messenger;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signupActivity extends AppCompatActivity {
    private EditText idEdit;
    private EditText pwEdit;
    private EditText nameEdit;
    private Button signupFinishBtn;
    private FirebaseDatabase database= FirebaseDatabase.getInstance();
    private FirebaseAdapter DB;
    private DatabaseReference ref;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        idEdit = (EditText)findViewById(R.id.id_signup);
        pwEdit = (EditText)findViewById(R.id.pw_signup);
        nameEdit = (EditText)findViewById(R.id.name_signup);
        signupFinishBtn = (Button)findViewById(R.id.signupFinishBtn);
        signupFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSingUp = true;

                String id = idEdit.getText().toString();
                String pw = pwEdit.getText().toString();
                String name = nameEdit.getText().toString();

                ref = database.getReference("user").child(id);
                if(id.equals("")||pw.equals("")||name.equals(""))
                    isSingUp=false;
                if(isSingUp==true) {
                    DB = new FirebaseAdapter(database,ref);
                    user = new User(id,pw,name);
                    DB.inputValue(user);
                    finish();
                }
                else
                    Toast.makeText(signupActivity.this.getApplicationContext(),"회원가입 실패\n아이디,비밀번호,이름 중 기입하지 않은 것이 있습니다.",Toast.LENGTH_SHORT).show();
            }
        });

    }
}
