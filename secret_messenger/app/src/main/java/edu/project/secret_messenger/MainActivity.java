package edu.project.secret_messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import static edu.project.secret_messenger.util.*;
import static edu.project.secret_messenger.ARIA_CBC.ARIA.*;
import static edu.project.secret_messenger.ARIA_CBC.Aria_CBC.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EditText msgEdit = findViewById(R.id.chat_editText);
        Button msgSendBtn = findViewById(R.id.textSendBtn);

        setContentView(R.layout.activity_main);
    }
}
