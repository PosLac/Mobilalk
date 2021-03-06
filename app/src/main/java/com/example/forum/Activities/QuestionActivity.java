package com.example.forum.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.forum.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity {
    private TextView mTitleText;
    private ImageView mUserImage;
    private TextView mUserNameText;
    private TextView mDescriptionText;
    private String questionId;
    private FirebaseUser user;
    private FirebaseStorage storage;

    private List<String> answers;
    private ArrayAdapter adapter;
    private FirebaseFirestore db;
    private Intent intent;
    private Button delete_button;
    private static final String LOG_TAG = QuestionActivity.class.getName();
    private boolean guest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        db = FirebaseFirestore.getInstance();

        intent = getIntent();

        storage = FirebaseStorage.getInstance();
        questionId = intent.getStringExtra("QUESTION_ID");

        delete_button = (Button) findViewById(R.id.delete_button);
        delete_button.setVisibility(View.GONE);

        mTitleText = findViewById(R.id.title);
        mTitleText.setText(intent.getStringExtra("TITLE"));

        mDescriptionText = findViewById(R.id.description);
        mDescriptionText.setText(intent.getStringExtra("DESC"));

        mUserNameText = findViewById(R.id.userName);
        mUserNameText.setText(intent.getStringExtra("NAME"));

        mUserImage = findViewById(R.id.userImage);

        answers = intent.getStringArrayListExtra("ANSWERS");

        if (answers != null) {
            adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, answers);
            ListView listView = (ListView) findViewById(R.id.answers);
            listView.setAdapter(adapter);
        }

        user = FirebaseAuth.getInstance().getCurrentUser();
        guest = user.isAnonymous();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!guest) {
            if (user.getEmail().equals(intent.getStringExtra("EMAIL"))) {
                delete_button.setVisibility(View.VISIBLE);
            }
        }

        Log.i(LOG_TAG, "image: " + intent.getStringExtra("IMAGE"));
        try {
            storage.getReference().child("images/" + intent.getStringExtra("IMAGE")).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Glide.with(QuestionActivity.this).load(task.getResult()).into(mUserImage);
                    Log.i(LOG_TAG, "image: " + intent.getStringExtra("IMAGE"));
                }
            });
        }
        catch (Exception e){
            Log.i(LOG_TAG, e.getMessage());
        }
    }
    public void answer(View view) {

        if(guest){
            Toast.makeText(this, "Vend??gk??t nem el??rhet??.", Toast.LENGTH_SHORT).show();
        }

        else{
            EditText answer = findViewById(R.id.answer);
            answers.add(answer.getText().toString());

            Map<String, Object> answersMap = new HashMap<>();
            answersMap.put("answers", answers);

            // TODO: 2022. 07. 01.
            db.collection("Questions").document(questionId).set(answersMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(QuestionActivity.this, "V??laszod ki??r??sra ker??lt!", Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }
    }

    public void delete(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(QuestionActivity.this);
        builder.setTitle("T??rl??s");
        builder.setMessage("Biztosan t??rl??d?");
        builder.setPositiveButton("Igen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                final DocumentReference documentReference = db.collection("Questions").document(questionId);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(QuestionActivity.this, "K??rd??s t??r??lve", Toast.LENGTH_SHORT).show();
                        Log.i(LOG_TAG, "Siker");
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionActivity.this, "K??rd??s t??rl??se sikertelen.", Toast.LENGTH_SHORT).show();
                        Log.i(LOG_TAG, "g??z van " + e.getMessage());
                    }
                });
            }
        });

        builder.setNegativeButton("M??gse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}