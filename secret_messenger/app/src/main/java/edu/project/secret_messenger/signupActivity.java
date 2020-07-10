package edu.project.secret_messenger;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
                String id = idEdit.getText().toString();
                String pw = pwEdit.getText().toString();
                String name = nameEdit.getText().toString();
                ref = database.getReference("user").child(id);

                DB = new FirebaseAdapter(database,ref);
                user = new User(id,pw,name);
                DB.inputValue(user);
            }
        });
    }
}
