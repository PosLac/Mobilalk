package com.example.forum.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forum.Models.Question;
import com.example.forum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class AddQuestionActivity extends AppCompatActivity {

    private DocumentReference reference;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String currentuid = user.getUid();
    private static final String LOG_TAG = AddQuestionActivity.class.getName();
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_question);
    }

    public void submit(View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        EditText title = findViewById(R.id.question_title);
        EditText description = findViewById(R.id.question_description);

        String mTitle = title.getText().toString();
        String mDescription = description.getText().toString();

        if (mTitle.equals("")) {
            Toast.makeText(this, "Kérdést kötelező megadni!", Toast.LENGTH_LONG).show();
        } else {

            reference = firestore.collection("Users").document(currentuid);

            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {

                                String nameResult = task.getResult().getString("userName");
                                String imageResult = task.getResult().getString("userImage");
                                Date date = null;
                                List<String> emptyArray = new ArrayList<>();
                                
                                final String randomKey = UUID.randomUUID().toString();
                                for (UserInfo user: FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
                                    if (user.getProviderId().equals("google.com")) {
                                        Log.i(LOG_TAG, "User is signed in with Google");
                                        nameResult = user.getEmail().split("@")[0];
                                    }
                                }

                                Question question = new Question(randomKey, mTitle, mDescription, user.getEmail(), imageResult, emptyArray, nameResult,  date);
                                db.collection("Questions").document(randomKey).set(question).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(AddQuestionActivity.this, "Kérdésed kiírásra került", Toast.LENGTH_LONG).show();
//                                            Intent intent = new Intent(QuestionActivity.this, ForumActivity.class);
//                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(AddQuestionActivity.this, "Hiba a kirás során", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(AddQuestionActivity.this, "Hiba a kirás során", Toast.LENGTH_SHORT).show();
                                Log.i(LOG_TAG, "baj" + task.getException().getMessage());
                                finish();
                            }
                        }
                    });
        }
    }
}