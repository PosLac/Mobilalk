package com.example.forum.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.forum.Models.User;
import com.example.forum.Others.Animation;
import com.example.forum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {
    private ImageView userImage;
    private EditText userNameEditText;
    private EditText userEmailEditText;
    private EditText passwordEditText;
    private EditText passwordConfirmEditText;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private static final String LOG_TAG = RegisterActivity.class.getName();
    private static final String PREF_KEY = RegisterActivity.class.getPackage().toString();
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private Button b;
    private String userName;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db = FirebaseFirestore.getInstance();
        documentReference = db.collection("Users").document("profile");

        userImage = findViewById(R.id.userImage);
        userNameEditText = findViewById(R.id.userNameEditText);
        userEmailEditText = findViewById(R.id.userEmailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordAgainEditText);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        String email = preferences.getString("email", "");
        String password = preferences.getString("password", "");

        userEmailEditText.setText(email);
        passwordEditText.setText(password);
        passwordConfirmEditText.setText(password);

        mAuth = FirebaseAuth.getInstance();
        Log.i(LOG_TAG, "onCreate");
    }


    public void register(View view) {

        Animation.animation(this, R.id.registerButton, R.anim.bounce);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        userName = userNameEditText.getText().toString();
        email = userEmailEditText.getText().toString();
        String userImage = "default_profile_picture.png";
        // TODO: 2022. 07. 03.  
        String password = passwordEditText.getText().toString();
        String passwordConfirm = passwordConfirmEditText.getText().toString();

        if (userName.equals("") || password.equals("")) {
            Toast.makeText(RegisterActivity.this, "Minden mez?? ki van t??ltve?", Toast.LENGTH_LONG).show();
        } else {
            if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "Nem egyeznek a jelszavak.", Toast.LENGTH_LONG).show();
                return;
            }
            Log.i(LOG_TAG, "Regisztr??lt: " + userName + ", email: " + email);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        User user = new User(userName, email, userImage);
                        db.collection("Users").document(uid).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Sikeres regisztr??ci??", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Hiba", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        Log.d(LOG_TAG, "User created successfully");
                        startForum();
                    } else {
                        Log.d(LOG_TAG, "User wasn't created successfully:", task.getException());
                        Toast.makeText(RegisterActivity.this, "Sikertelen regisztr??ci??: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void cancel(View view) {
        Animation.animation(this, R.id.cancel, R.anim.bounce);
        finish();
    }

    public void startForum() {
        Intent intent = new Intent(this, ForumActivity.class);
        intent.putExtra("currentUserName", userName);

        startActivity(intent);
    }
}