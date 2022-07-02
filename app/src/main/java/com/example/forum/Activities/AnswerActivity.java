package com.example.forum.Activities;


import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerActivity extends AppCompatActivity {
    private TextView mTitleText;
    private ImageView mUserImage;
    private TextView mUserNameText;
    private TextView mDescriptionText;

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String currentuid = user.getUid();

    private List<String> answers;
    private ArrayAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Intent intent = getIntent();
    private Button delete_button;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);


        delete_button = (Button) findViewById(R.id.delete_button);
        delete_button.setVisibility(View.GONE);

        mTitleText  = findViewById(R.id.title);
        mTitleText.setText(intent.getStringExtra("TITLE"));

        mDescriptionText  = findViewById(R.id.description);
        mDescriptionText.setText(intent.getStringExtra("DESC"));

        mUserNameText  = findViewById(R.id.userName);
        mUserNameText.setText(intent.getStringExtra("NAME"));

        mUserImage  = findViewById(R.id.userImage);
        Glide.with(this).load(intent.getExtras().getInt("IMAGE")).into(mUserImage);

        answers = intent.getStringArrayListExtra("ANSWERS");

        if(answers != null){
            adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, answers);
            ListView listView = (ListView) findViewById(R.id.answers);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(user.getEmail().equals(intent.getStringExtra("currentUserEmail"))){
            delete_button.setVisibility(View.VISIBLE);
        }
    }

    public void answer(View view) {

        boolean guest = getIntent().getBooleanExtra("guest", false);
        if(guest){
            Toast.makeText(this, "Vendégkét nem elérhető.", Toast.LENGTH_SHORT).show();
        }

        else{
            EditText answer = findViewById(R.id.answer);
            answers.add(answer.getText().toString());

            Map<String, Object> answersMap = new HashMap<>();
            answersMap.put("answers", answers);
            // TODO: 2022. 07. 01.
            db.collection("Questions").document("rBET1TQPgRr4ntqIyOrU").set(answersMap, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
            Toast.makeText(this, "Válaszod kiírásra került!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    public void delete(View view) {
        Toast.makeText(this, "Gec", Toast.LENGTH_SHORT).show();
    }
}