package com.example.forum.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.forum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private EditText userNameEditText;
    private TextView userEmailTextView;
    private ImageView userProfileImage;
    private Uri imageUri;
    private static final String LOG_TAG = ProfileActivity.class.getName();
    final private int REQUEST_CODE_ASK_PERMISSIONS = 420;
    private FirebaseStorage storage;
    private DocumentReference reference;
    private StorageReference storageReference;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private String currentuid;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        currentuid  = user.getUid();

        userProfileImage = findViewById(R.id.userProfileImage);
        userNameEditText = findViewById(R.id.userName);
        userEmailTextView = findViewById(R.id.userEmail);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Profil betöltése..");
        pd.show();

        reference = db.collection("Users").document(currentuid);
        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            String nameResult = task.getResult().getString("userName");
                            String emailResult = task.getResult().getString("email");
                            String imageResult = task.getResult().getString("userImage");

                            Log.i(LOG_TAG, "image: " + imageResult);

                            userNameEditText.setText(nameResult);
                            userEmailTextView.setText(emailResult);

                            try {
                                storage.getReference().child("images/" + imageResult).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        Glide.with(ProfileActivity.this).load(task.getResult()).into(userProfileImage);
                                        pd.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Glide.with(ProfileActivity.this).load(R.drawable.default_profile_picture).into(userProfileImage);
                                        pd.dismiss();
                                    }
                                });

                            }catch (Exception e){
                                Log.i(LOG_TAG, e.getMessage());
                            }
                        } else {
                            pd.dismiss();
                            Toast.makeText(ProfileActivity.this, "Hiba", Toast.LENGTH_SHORT).show();
                            Log.i(LOG_TAG, "baj" + task.getException().getMessage() );
                        }
                    }
                });

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserPermission();
            }
        });
    }

    public void checkUserPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(ProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        choosePicture();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_PERMISSIONS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePicture();
            } else {
                Toast.makeText(this, "Hozzáférés elutasítva.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            userProfileImage.setImageURI(imageUri);
            uploadPicture();
        }
    }

    //Kiválasztott kép feltöltése Firebase Storage-ba
    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Képfeltöltés folyamatban..");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child(randomKey);

        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(R.id.userName), "Képfeltöltés sikeres", Snackbar.LENGTH_LONG).show();
                Log.d(LOG_TAG, "Uploaded successfully");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(ProfileActivity.this, "Hiba a feltöltés során", Toast.LENGTH_LONG).show();
                        Log.d(LOG_TAG, "An error occured during upload: " + e.getMessage());
                    }
                });

        db.collection("Users").document(currentuid).update("userImage", randomKey);
    }

    private void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Törlés");
        builder.setMessage("Biztosan szeretnéd törölni a fiókod? A művelet később nem vonható vissza.");
        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final DocumentReference documentReference = db.collection("Users").document(currentuid);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        setContentView(R.layout.activity_main);
                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ProfileActivity.this, "Fiók törölve", Toast.LENGTH_SHORT).show();
                                Log.i(LOG_TAG, "Profile deleted successfully");
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfileActivity.this, "Fiók törlése sikertelen.", Toast.LENGTH_SHORT).show();
                                Log.i(LOG_TAG, "Error during delete: " + e.getMessage());
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, "Fiók törlése sikertelen.", Toast.LENGTH_SHORT).show();
                        Log.i(LOG_TAG, "Error during delete: " + e.getMessage());
                    }
                });
            }
        });

        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void updateProfile(View view) {
        String name = userNameEditText.getText().toString();

        final DocumentReference sDoc = db.collection("Users").document(currentuid);
        Task<Void> voidTask = db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sDoc);

                transaction.update(sDoc, "userName", name);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ProfileActivity.this, "Frissítve", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Hiba", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, e.getMessage());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    public void deleteUser(View view) {
        ShowDialog();
    }
}