package com.example.forum.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forum.Models.Question;
import com.example.forum.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;


public class UsersQuestionsActivity extends AppCompatActivity {

    private static final String LOG_TAG = UsersQuestionsActivity.class.getName();
    private FirebaseFirestore mFirestore;
    private CollectionReference mQuestions;
    private ArrayAdapter adapter;
    private List<String> questionTitles;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private String currentuid = user.getUid();
    private ListView listView;
    private String data;
    public ArrayList<Question> mQuestionData;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_questions);
        mQuestionData = new ArrayList<>();
        mFirestore = FirebaseFirestore.getInstance();
        mQuestions = mFirestore.collection("Questions");

        questionTitles = new ArrayList<>();
        queryData();

        adapter = new ArrayAdapter<String>(UsersQuestionsActivity.this, R.layout.question_list, questionTitles);
        listView = (ListView) findViewById(R.id.questions);
        listView.setAdapter(adapter);




//        if (intent != null) {
//            data = intent.getExtras().getString("countryName");
//            questions.add(data);
//        } else {
//            data = null;
//        }


//
//        adapter = new ArrayAdapter<String>(this, R.layout.question_list, questions);
//        ListView listView = (ListView) findViewById(R.id.questions);
//        listView.setAdapter(adapter);

//        Log.i(LOG_TAG, questions.get(0));

        Log.i(LOG_TAG, "oncreate vege");
    }

    private void queryData() {

        mQuestions.whereEqualTo("userEmail", user.getEmail()).limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Question question = document.toObject(Question.class);
                mQuestionData.add(question);
                questionTitles.add(question.getTitle());

                Log.i(LOG_TAG, question.getTitle());
            }

            if (mQuestionData.size() == 0) {
//                initializeData();
                queryData();
            }
            adapter.notifyDataSetChanged();
        });
    }

    public void delete_question(View view) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onstart");
        Intent intent = new Intent(UsersQuestionsActivity.this, QuestionActivity.class);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Log.d(LOG_TAG, "Open question");
//                Log.d(LOG_TAG, mQuestionData.get(position).getId());
//                Log.d(LOG_TAG, mQuestionData.get(position).getTitle());
//                Log.d(LOG_TAG, mQuestionData.get(position).getDescription());
//                Log.d(LOG_TAG, mQuestionData.get(position).getImageResource());


                intent.putExtra("EMAIL", mQuestionData.get(position).getUserEmail());
                intent.putExtra("QUESTION_ID", mQuestionData.get(position).getId());
                intent.putExtra("TITLE", mQuestionData.get(position).getTitle());
                intent.putExtra("DESC", mQuestionData.get(position).getDescription());
                intent.putExtra("IMAGE", mQuestionData.get(position).getImageResource());
                intent.putExtra("NAME", mQuestionData.get(position).getUserName());
                intent.putStringArrayListExtra("ANSWERS", (ArrayList<String>) mQuestionData.get(position).getAnswers());

                startActivity(intent);
            }
        });
    }

}