package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAdapter testDB;
    private EditText idEdit;
    private EditText pwEdit;
    private Button loginBtn;
    private Button signupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        testDB = new FirebaseAdapter(database, myRef);

        idEdit = (EditText)findViewById(R.id.login_id);
        pwEdit = (EditText)findViewById(R.id.login_password);
        loginBtn = (Button)findViewById(R.id.login_button);
        signupBtn = (Button)findViewById(R.id.signup_button);


        loginBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                String id = idEdit.getText().toString();
                String pw = pwEdit.getText().toString();

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
