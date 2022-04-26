package com.example.forum;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;


public class Profile extends AppCompatActivity {

    EditText userNameEditText;
    EditText userEmailEditText;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static final String LOG_TAG = Profile.class.getName();
    private static final int SECRET_KEY = 99;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentuid = user.getUid();
    DocumentReference reference;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    public void updateProfile(View view) {
        String name = userNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();

        final DocumentReference sDoc = db.collection("Users").document(currentuid);
        Task<Void> voidTask = db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(sDoc, "userName", name);
                transaction.update(sDoc, "email", email);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Profile.this, "Updated", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Failed", Toast.LENGTH_SHORT).show();
                        Log.i(LOG_TAG, e.getMessage());
                    }
                });
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        userNameEditText = findViewById(R.id.userName);
        userEmailEditText = findViewById(R.id.userEmail);
    }

    @Override
    protected void onStart() {
        super.onStart();


        reference = firestore.collection("Users").document(currentuid);
//
//        //userImage = findViewById(R.id.userImage);
//
//
        Log.i(LOG_TAG, "ide " + currentuid);


        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String nameResult = task.getResult().getString("userName");
                    String emailResult = task.getResult().getString("email");
                    //String url = task.getResult().getString("url");

                    //Picasso.get().load(url).into(userImage);
                    userNameEditText.setText(nameResult);
                    userEmailEditText.setText(emailResult);
                    Log.i(LOG_TAG, "baj " + nameResult);
                    Log.i(LOG_TAG, "baj " + emailResult);
                } else {
                    Toast.makeText(Profile.this, "Nono", Toast.LENGTH_SHORT).show();
                    Log.i(LOG_TAG, "baj" + task.getException().getMessage() );
                }
            }
        });


        Log.i(LOG_TAG, "onStart");
    }

    public void startForum() {
        Intent intent = new Intent(this, ForumActivity.class);
        startActivity(intent);
    }

    public void cancel(View view) {
        finish();
        startForum();
    }

    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop");
    }
    protected void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause");
    }

    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume");
    }

    protected void onRestart() {
        super.onRestart();
        Log.i(LOG_TAG, "onRestart");
    }
}