package com.example.forum.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forum.Models.User;
import com.example.forum.Others.Animation;
import com.example.forum.Others.NotificationHandler;
import com.example.forum.R;
import com.example.forum.Others.ShakeSensor;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String PREF_KEY = MainActivity.class.getPackage().toString();
    private static final int RC_SIGN_IN = 123;
    private static final int SECRET_KEY = 99;
    private EditText emailET;
    private EditText passwordET;
    private NotificationHandler mNotificationHandler;
    boolean isRunning = false;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeSensor mShakeDetector;
    private MediaPlayer mediaPlayer;
    private FirebaseFirestore db;
    private FirebaseUser currentuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "Rázd meg a telefont!", Toast.LENGTH_LONG).show();

        mNotificationHandler = new NotificationHandler(MainActivity.this);

        db = FirebaseFirestore.getInstance();
        emailET = findViewById(R.id.editTextEmail);
        passwordET = findViewById(R.id.editTextPassword);

        preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeSensor();
        mShakeDetector.setOnShakeListener(new ShakeSensor.OnShakeListener() {
            public void onShake() {
                if (Build.VERSION.SDK_INT >= 23 && !isRunning) {
                    isRunning = true;
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource("https://fwesh.yonle.repl.co");
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                        Toast.makeText(MainActivity.this, "Thanks", Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(LOG_TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {
                Log.w(LOG_TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LOG_TAG, "signInWithCredential:success");

                            // TODO: 2022. 07. 13. Turn on
//                          mNotificationHandler.send("Jó újra látni!");

                            saveUser();
                            startForum();
                        } else {
                            Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                        }
                    }
                });
    }

    public void saveUser(){
        currentuser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentuser != null) {

//            Uri photoUrl = currentuser.getPhotoUrl();
//            StorageReference ref = storageReference.child("default_profile_picture.png");


//            ref.putFile(photoUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    Log.d(LOG_TAG, "Uploaded succesfully");
//                }
//            })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Log.d(LOG_TAG, "Error during upload");
//                        }
//                    });todo de amugy annyira nem fontos


            String email = currentuser.getEmail();
            String userName = email.split("@")[0];

            User user = new User(userName, email, "default_profile_picture.png");

            // TODO: 2022. 07. 13. Check duplicates
            db.collection("Users").document(currentuser.getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Sikeres", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Hiba", Toast.LENGTH_SHORT).show();
                    }
                    Log.d(LOG_TAG, "User created successfully");
                }
            });
            Log.i(LOG_TAG, email);
            Log.i(LOG_TAG, userName);
        }
    }

    public void loginWithGoogle(View view) {
        Animation.animation(this, R.id.googleSignInButton, R.anim.bounce);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void startForum() {
        Intent intent = new Intent(this, ForumActivity.class);
        startActivity(intent);
        finish();
    }

    public void login(View view) {
        Animation.animation(this, R.id.loginButton, R.anim.bounce);

        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (email.equals("") || password.equals("")) {
            Toast.makeText(MainActivity.this, "Minden mező ki van töltve?", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Log.d(LOG_TAG, "Login was successfull.");
                        // TODO: 2022. 07. 13. Turn on
//                        mNotificationHandler.send("Jó újra látni!");
                        startForum();
                    } else {
                        Log.d(LOG_TAG, "User login fail:", task.getException());
                        Toast.makeText(MainActivity.this, "Sikertelen bejelentkezés: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void loginAsGuest(View view) {
        Animation.animation(this, R.id.guestLoginButton, R.anim.bounce);

        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(LOG_TAG, "Anonim login was successfull.");

                    // TODO: 2022. 07. 13. Turn on
//                    mNotificationHandler.send("Jó újra látni!");

                    startForum();
                } else {
                    Log.d(LOG_TAG, "Anonim user login fail:", task.getException());
                    Toast.makeText(MainActivity.this, "Vendégbejelentkezés sikertelen: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    public void jumpToRegister(View view) {

        Animation.animation(this, R.id.registerButton, R.anim.bounce);

        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra("SECRET_KEY", SECRET_KEY);
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
        //mediaPlayer.release();
        Log.i(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();if(isRunning){
            mediaPlayer.release();
        }
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
        if(isRunning){
            mediaPlayer.release();
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("email", emailET.getText().toString());
        editor.putString("password", passwordET.getText().toString());
        editor.apply();
        Log.i(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        isRunning = false;
        Log.i(LOG_TAG, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}