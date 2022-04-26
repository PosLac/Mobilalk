package com.example.forum;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    ImageView userImage;
    EditText userNameEditText;
    EditText userEmailEditText;
    EditText passwordEditText;
    EditText passwordConfirmEditText;

    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private static final int SECRET_KEY = 99;

    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    Button b;
    private CollectionReference Users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //Majd ezt hasznald
//        Bundle bundle = getIntent().getExtras();
//        bundle.getInt("SECRET_KEY");

        /*
        int secret_key = getIntent().getIntExtra("SECRET_KEY", 0);
        if(secret_key != 99){
            //visszater az elozo activityre
            finish();
        }
        */

        //mainben editTextUserName, itt userNameEditText
        userImage = findViewById(R.id.userImage);
        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordAgainEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        //bejelentkezesnel beirt cuccok regisztracional mar be legyenek irva
        String userName = preferences.getString("userName", "");
        String password = preferences.getString("password", "");

        userNameEditText.setText(userName);
        passwordEditText.setText(password);
        passwordConfirmEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG, "onCreate");
    }


    public void register(View view) {
        b = (Button) findViewById(R.id.registerButton);
        Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.bounce);
        b.startAnimation(animation);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //String image = userImage.;
        String userName = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();
        if (userName.equals("") || password.equals("")) {
            Toast.makeText(RegisterActivity.this, "Minden mező ki van töltve?", Toast.LENGTH_LONG).show();
        } else {
            if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "Nem egyeznek a jelszavak.", Toast.LENGTH_LONG).show();
                return;
            }
            Log.i(LOG_TAG, "Regisztrált: " + userName + ", email: " + email);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        User user = new User(userName, email);
                        db.collection("Users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Sikeres", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.d(LOG_TAG, "User created successfully");
                        startForum();
                    } else {
                        Log.d(LOG_TAG, "User wasn't created successfully:", task.getException());
                        Toast.makeText(RegisterActivity.this, "User wasn't created successfully: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void cancel(View view) {
        b = (Button) findViewById(R.id.cancel);
        Animation animation = AnimationUtils.loadAnimation(RegisterActivity.this, R.anim.bounce);
        b.startAnimation(animation);

        finish();
    }

    public void startForum() {
        Intent intent = new Intent(this, ForumActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}