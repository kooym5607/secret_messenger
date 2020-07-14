package edu.project.secret_messenger;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseAdapter {
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private DataSnapshot tempStr;

    private static final String TAG = "FirebaseAdapter";

    FirebaseAdapter(FirebaseDatabase db, DatabaseReference ref){
        this.db = db;
        this.ref = ref;
    }

    void inputValue(String str){
        this.ref.setValue(str);
    }
    void inputValue(Object object){
        this.ref.setValue(object);
    }

    DataSnapshot getValue(){
        this.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tempStr = dataSnapshot;

                Log.d(TAG,"Firebase DB를 통해 불러온 값 : " + tempStr);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG,"Firebase DB 값 불러오는데 실패.", databaseError.toException());
            }
        });
        return tempStr;
    }
    void setRef(DatabaseReference dbRef){
        this.ref = dbRef;
    }
    DatabaseReference getRef(){return this.ref;}

}
