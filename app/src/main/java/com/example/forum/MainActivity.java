 package com.example.forum;

 import android.Manifest;
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
 import android.view.animation.Animation;
 import android.view.animation.AnimationUtils;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.annotation.RequiresApi;
 import androidx.appcompat.app.AppCompatActivity;
 import androidx.core.app.ActivityCompat;
 import androidx.loader.app.LoaderManager;
 import androidx.loader.content.Loader;

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
 import com.google.firebase.auth.GoogleAuthProvider;

 import java.io.IOException;import android.content.pm.PackageManager;


 public class MainActivity extends AppCompatActivity
 implements LoaderManager.LoaderCallbacks<String> {
     private static final String LOG_TAG = MainActivity.class.getName();
     private static final String PREF_KEY = MainActivity.class.getPackage().toString();
     private static final int RC_SIGN_IN = 123;
     private static final int SECRET_KEY = 99;
     EditText userNameET;
     EditText passwordET;

     final private int REQUEST_CODE_ASK_PERMISSIONS = 420;

     boolean isRunning = false;
     private SharedPreferences preferences;
     private FirebaseAuth mAuth;
     private GoogleSignInClient mGoogleSignInClient;
     Button b;

     private SensorManager mSensorManager;
     private Sensor mAccelerometer;
     private SensorActivity mShakeDetector;

     MediaPlayer mediaPlayer;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);
         Toast.makeText(MainActivity.this, "Shake your phone!", Toast.LENGTH_LONG).show();

         userNameET = findViewById(R.id.editTextUserName);
         passwordET = findViewById(R.id.editTextPassword);

         preferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);
         mAuth = FirebaseAuth.getInstance();

         GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestIdToken(getString(R.string.default_web_client_id))
                 .requestEmail().build();
         mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
         getSupportLoaderManager().restartLoader(0, null, this);


         mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
         mAccelerometer = mSensorManager
                 .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
         mShakeDetector = new SensorActivity();
         mShakeDetector.setOnShakeListener(new SensorActivity.OnShakeListener() {
             public void onShake() {
                 if (Build.VERSION.SDK_INT >= 23 && isRunning == false) {
                     isRunning = true;
                     mediaPlayer = new MediaPlayer();
                     try {
                         mediaPlayer.setDataSource("https://fwesh.yonle.repl.co");
                         mediaPlayer.prepare();
                         mediaPlayer.start();
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }
         });
         Log.i(LOG_TAG, "onCreate");
     }

//     @RequiresApi(api = Build.VERSION_CODES.Q)
     public void onShake(){
         if (Build.VERSION.SDK_INT >= 23 && isRunning == false) {
//             if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
//                 requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_CODE_ASK_PERMISSIONS);
//             }
//             else {
                 isRunning = true;
                 mediaPlayer = new MediaPlayer();
                 try {
                     mediaPlayer.setDataSource("https://fwesh.yonle.repl.co");
                     mediaPlayer.prepare();
                     mediaPlayer.start();
                 } catch (IOException e) {
                     e.printStackTrace();
                 }
             }
//         }
     }
     /*
     @RequiresApi(api = Build.VERSION_CODES.Q)
     @Override
     public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
         switch (requestCode) {
             case REQUEST_CODE_ASK_PERMISSIONS:
                 if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     onShake();
                 } else {
                     Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                 }
                 break;
             default:
                 super.onRequestPermissionsResult(requestCode, permissions, grantResults);
         }
     }
     */

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
                             startForum();
                         } else {
                             Log.w(LOG_TAG, "signInWithCredential:failure", task.getException());
                         }
                     }
                 });
     }

     public void startForum() {
         Intent intent = new Intent(this, ForumActivity.class);
         startActivity(intent);
     }

     public void login(View view) {
         b = (Button) findViewById(R.id.loginButton);
         Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
         b.startAnimation(animation);

         String userName = userNameET.getText().toString();
         String password = passwordET.getText().toString();

         if (userName.equals("") || password.equals("")) {
             Toast.makeText(MainActivity.this, "Empty username or password.", Toast.LENGTH_LONG).show();
         } else {
             mAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if (task.isSuccessful()) {
                         Log.d(LOG_TAG, "Login was successfull.");
                         startForum();
                     } else {
                         Log.d(LOG_TAG, "User login fail:", task.getException());
                         Toast.makeText(MainActivity.this, "User login fail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                     }
                 }
             });
         }
     }

     public void loginAsGuest(View view) {
         b = (Button) findViewById(R.id.guestLoginButton);
         Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
         b.startAnimation(animation);

         mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if (task.isSuccessful()) {
                     Log.d(LOG_TAG, "Anonim login was successfull.");
                     startForum();
                 } else {
                     Log.d(LOG_TAG, "Anonim user login fail:", task.getException());
                     Toast.makeText(MainActivity.this, "Anonim user login fail: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                 }
             }
         });
     }

     public void loginWithGoogle(View view) {
         b = (Button) findViewById(R.id.googleSignInButton);
         Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
         b.startAnimation(animation);

         Intent signInIntent = mGoogleSignInClient.getSignInIntent();
         startActivityForResult(signInIntent, RC_SIGN_IN);
     }

     public void register(View view) {
         b = (Button) findViewById(R.id.registerButton);
         Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.bounce);
         b.startAnimation(animation);

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
         mediaPlayer.release();
         Log.i(LOG_TAG, "onStop");
     }

     @Override
     protected void onPause() {
         mSensorManager.unregisterListener(mShakeDetector);
         super.onPause();

         SharedPreferences.Editor editor = preferences.edit();
         editor.putString("userName", userNameET.getText().toString());
         editor.putString("password", passwordET.getText().toString());
         editor.apply();
         mediaPlayer.release();

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

     @NonNull
     @Override
     public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
         return new RandomAsyncLoader(this);
     }

     @Override
     public void onLoadFinished(@NonNull Loader<String> loader, String data) {
         Button button = findViewById(R.id.guestLoginButton);
         button.setText(data);
     }

     @Override
     public void onLoaderReset(@NonNull Loader<String> loader) {
     }
 }