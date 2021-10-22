package com.example.weatherforecast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.weatherforecast.Model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class SignUp extends AppCompatActivity {

    MaterialEditText Phone,Name,Password,email;
    Button SignUp;
    FirebaseAuth firebaseAuth;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Name = (MaterialEditText)findViewById(R.id.Name);
        Password = (MaterialEditText)findViewById(R.id.Password);
        Phone = (MaterialEditText)findViewById(R.id.Phone);
        email = (MaterialEditText)findViewById(R.id.email);

        SignUp = (Button) findViewById(R.id.SignUp);

        firebaseAuth = FirebaseAuth.getInstance();

        //Init firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage(("Please waiting ....."));
                mDialog.show();
                //send to authentication
                firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(),Password.getText().toString());

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //check if already user phone
                        if (snapshot.child(Phone.getText().toString()).exists() || snapshot.child(email.getText().toString()).exists()){
                            mDialog.dismiss();
                            Toast.makeText(SignUp.this, " Already registered!!!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mDialog.dismiss();
                            User user = new User(Name.getText().toString(),Password.getText().toString(),email.getText().toString());
                            table_user.child(Phone.getText().toString()).setValue(user);
                            Toast.makeText(SignUp.this, "Sign up successfully !", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
}