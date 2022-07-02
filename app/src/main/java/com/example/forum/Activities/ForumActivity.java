package com.example.forum.Activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.forum.Models.Question;
import com.example.forum.Models.User;
import com.example.forum.Others.QuestionAdapter;
import com.example.forum.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class ForumActivity extends AppCompatActivity {

    private static final String LOG_TAG = ForumActivity.class.getName();
    private FirebaseUser user;
    private RecyclerView mRecyclerView;
    private ArrayList<Question> mQuestionsData;
    private ArrayList<User> mUsersData;
    private QuestionAdapter mAdapter;
    private FirebaseFirestore mFirestore;
    private CollectionReference mQuestions;
    private static final int SECRET_KEY = 99;
    private int gridNumber = 1; //oszlopszám
    private boolean viewRow = true;
    private boolean guest;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        } else {
            Log.d(LOG_TAG, "Unauthenticated user!");
            finish();
        }

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridNumber));
        mQuestionsData = new ArrayList<>();
        mAdapter = new QuestionAdapter(this, mQuestionsData);
        mRecyclerView.setAdapter(mAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mQuestions = mFirestore.collection("Questions");
        guest = getIntent().getBooleanExtra("guest", false);
        queryData();
    }

    private void queryData(){
        mQuestionsData.clear();

        mQuestions.limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                Question question = document.toObject(Question.class);
                mQuestionsData.add(question);
            }

            if(mQuestionsData.size() == 0){
//                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }
//
//    private void initializeData() {
//        String[] userNameList = getResources()
//                .getStringArray(R.array.userNames);
//        String[] titleList = getResources().
//                getStringArray(R.array.titles);
//        String[] descriptionList = getResources().
//                getStringArray(R.array.descriptions);
//        TypedArray userImageResources = getResources().
//                obtainTypedArray(R.array.userImages);
//
//        for (int i = 0; i < userNameList.length; i++) {
//
//            Log.d(LOG_TAG, userNameList[i] +
//                    titleList[i] +
//                    descriptionList[i] +
//                    userImageResources.getResourceId(i, 0));
//
//            mQuestions.add(new Question(
//                    userNameList[i],
//                    titleList[i],
//                    descriptionList[i],
//                    userImageResources.getResourceId(i, 0)));
//        }
//        userImageResources.recycle();
//        mAdapter.notifyDataSetChanged();
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.question_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out_button:
                    Log.d(LOG_TAG, "Logout clicked!");
                    FirebaseAuth.getInstance().signOut();
                    finish();
                    return true;
            case R.id.profile_button:
                Log.d(LOG_TAG, "Profile clicked!");

                if(guest){
                    Toast.makeText(this, "Vendégkét nem elérhető.", Toast.LENGTH_SHORT).show();
                    return true;
                }

                else{

                    Intent intent = new Intent(this, ProfileActivity.class);
                    intent.putExtra("SECRET_KEY", SECRET_KEY);

                    intent.putExtra("currentUserName", getIntent().getStringExtra("currentUserName"));
                    intent.putExtra("currentUserEmail", getIntent().getStringExtra("currentUserEmail"));
//                    intent.putExtra("currentUserImage", 2131230893);

                    startActivity(intent);
                    finish();
                    return true;
                }
            case R.id.questions_button:
                if(guest){
                    Toast.makeText(this, "Vendégkét nem elérhető.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    Intent intent = new Intent(this, UsersQuestionsActivity.class);
                    startActivity(intent);
                }

            case R.id.view_selector:
                Log.d(LOG_TAG, "Views clicked!");
                if (viewRow) {
                    changeSpanCount(item, R.drawable.ic_view_grid, 2);
                } else {
                    changeSpanCount(item, R.drawable.ic_view_row, 1);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeSpanCount(MenuItem item, int drawableId, int spanCount) {
        viewRow = !viewRow;
        item.setIcon(drawableId);
        GridLayoutManager layoutManager = (GridLayoutManager) mRecyclerView.getLayoutManager();
        layoutManager.setSpanCount(spanCount);
    }

    public void add_question(View view) {

        boolean guest = getIntent().getBooleanExtra("guest", false);
        if(guest){
            Toast.makeText(this, "Vendégkét nem elérhető.", Toast.LENGTH_SHORT).show();
        }

        else{
            Intent intent = new Intent(this, AddQuestionActivity.class);
            startActivity(intent);
        }
    }

//
//    public void answer(View view) {
//        Log.d(LOG_TAG, "Answer clicked!");
//        Intent intent = new Intent(this, AnswerActivity.class);
////        curent_question
////        intent.putExtra("CURRENT_QUESTION", );
//        startActivity(intent);
//        finish();
//    }


    @Override
    protected void onResume() {
        super.onResume();
        queryData();
    }
}

