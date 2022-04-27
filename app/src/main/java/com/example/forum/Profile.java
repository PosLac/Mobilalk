package com.example.forum;

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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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

public class Profile extends AppCompatActivity {

    private EditText userNameEditText;
    private EditText userEmailEditText;
    private ImageView userProfileImage;
    private Uri imageUri;
    private static final String LOG_TAG = Profile.class.getName();
    final private int REQUEST_CODE_ASK_PERMISSIONS = 420;
    private DocumentReference reference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentuid = user.getUid();
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        userProfileImage = findViewById(R.id.userProfileImage);
        userNameEditText = findViewById(R.id.userName);
        userEmailEditText = findViewById(R.id.userEmail);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("profile images");

        userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserPermission();
            }
        });
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    void checkUserPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            if(ActivityCompat.checkSelfPermission(Profile.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        choosePicture();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    choosePicture();
                } else {
                    Toast.makeText(this, "Hozzáférés elutasítva.", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            userProfileImage.setImageURI(imageUri);
            Log.d(LOG_TAG, "okes");

            uploadPicture();
        }
    }

    private void uploadPicture() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Képfeltöltés folyamatban..");
        pd.show();

        final String randomKey = UUID.randomUUID().toString();
        StorageReference ref = storageReference.child("images/" + randomKey);

        ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                Snackbar.make(findViewById(R.id.userName), "Képfeltöltés sikeres", Snackbar.LENGTH_LONG).show();
                Log.d(LOG_TAG, "siker");
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(Profile.this, "Hiba a feltöltés során", Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "hiba");
            }
        });
    }

    private void ShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
        builder.setTitle("Törlés");
        builder.setMessage("Biztosan szeretnéd törölni a fiókod? A művelet később nem vonható vissza.");
        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final DocumentReference documentReference = db.collection("Users").document(currentuid);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Profile.this, "Fiók törölve", Toast.LENGTH_SHORT).show();

                        Log.i(LOG_TAG, "siker");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Fiók törlése sikertelen.", Toast.LENGTH_SHORT).show();
                        Log.i(LOG_TAG, "gáz van " + e.getMessage());
                    }
                });

                user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(Profile.this, "Fiók törölve", Toast.LENGTH_SHORT).show();

                        Log.i(LOG_TAG, "siker");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Profile.this, "Fiók törlése sikertelen.", Toast.LENGTH_SHORT).show();
                        Log.i(LOG_TAG, "gáz van " + e.getMessage());
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
//        String email = userEmailEditText.getText().toString();

//        final String randomKey = UUID.randomUUID().toString();
//        StorageReference ref = storageReference.child("images/" + randomKey);

//        uploadTask = ref.putFile(imageUri);
//        Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//            @Override
//            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                if(!task.isSuccessful()){
//                    throw task.getException();
//                }
//                return ref.getDownloadUrl();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//            @Override
//            public void onComplete(@NonNull Task<Uri> task) {
//                if(task.isSuccessful()){
//                    Uri downloadUri = task.getResult();

        final DocumentReference sDoc = db.collection("Users").document(currentuid);
        Task<Void> voidTask = db.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sDoc);

                transaction.update(sDoc, "userName", name);
//                            transaction.update(sDoc, "url", downloadUri.toString());
                //transaction.update(sDoc, "email", email);
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Profile.this, "Frissítve", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Profile.this, "Hiba", Toast.LENGTH_SHORT).show();
                Log.i(LOG_TAG, e.getMessage());
            }
        });
//                }
//            }
//        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        reference = firestore.collection("Users").document(currentuid);

        reference.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String nameResult = task.getResult().getString("userName");
                    String emailResult = task.getResult().getString("email");
//                    String url = task.getResult().getString("url");

//                    Picasso.get().load(url).into(userProfileImage);
                    userNameEditText.setText(nameResult);
                    userEmailEditText.setText(emailResult);
                } else {
                    Toast.makeText(Profile.this, "Hiba", Toast.LENGTH_SHORT).show();
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

    public void deleteUser(View view) {
        ShowDialog();
    }
}