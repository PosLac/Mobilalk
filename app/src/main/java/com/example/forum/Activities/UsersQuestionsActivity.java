package com.example.forum.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forum.Models.Question;
import com.example.forum.Models.User;
import com.example.forum.Others.QuestionAdapter;
import com.example.forum.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UsersQuestionsActivity extends AppCompatActivity {

    private static final String LOG_TAG = ForumActivity.class.getName();
    private ArrayList<Question> mQuestionsData;
    private FirebaseFirestore mFirestore;
    private CollectionReference mQuestions;
    private ArrayAdapter adapter;
    private List<String> questions;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String currentuid = user.getUid();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_questions);
        mQuestionsData = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        mQuestions = mFirestore.collection("Questions");

        questions = new ArrayList<>();
        queryData();

        adapter = new ArrayAdapter<String>(UsersQuestionsActivity.this, R.layout.question_list, questions);
        ListView listView = (ListView) findViewById(R.id.questions);
        listView.setAdapter(adapter);


//
//        adapter = new ArrayAdapter<String>(this, R.layout.question_list, questions);
//        ListView listView = (ListView) findViewById(R.id.questions);
//        listView.setAdapter(adapter);

//        Log.i(LOG_TAG, questions.get(0));

    }

    private void queryData(){

        mQuestions.whereEqualTo("userEmail", user.getEmail()).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                Question question = document.toObject(Question.class);
                mQuestionsData.add(question);

                questions.add(question.getTitle());
                Log.i(LOG_TAG, question.getTitle());
            }

            if(mQuestionsData.size() == 0){
//                initializeData();
                queryData();
            }
            adapter.notifyDataSetChanged();
        });
    }
}
