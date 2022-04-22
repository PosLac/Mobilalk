package com.example.forum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ForumActivity extends AppCompatActivity {

    private static final String LOG_TAG = ForumActivity.class.getName();
    private FirebaseUser user;

    private RecyclerView mRecyclerView;
    private ArrayList<Question> mQuestionsData;
    private QuestionAdapter mAdapter;

    private FirebaseFirestore mFirestore;
    private CollectionReference mQuestions;

    private int gridNumber = 1; //oszlopszám
    private boolean viewRow = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            Log.d(LOG_TAG, "Authenticated user!");
        }else{
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

        queryData();
    }


    private void queryData(){
        mQuestionsData.clear();

        //mQuestions.whereEqualTo();
        mQuestions.orderBy("userName").limit(10).get().addOnSuccessListener(queryDocumentSnapshots -> {
            for(QueryDocumentSnapshot document : queryDocumentSnapshots){
                Question question = document.toObject(Question.class);
                mQuestionsData.add(question);
            }

            if(mQuestionsData.size() == 0){
                initializeData();
                queryData();
            }
            mAdapter.notifyDataSetChanged();
        });
    }

    private void initializeData() {
        String[] userNameList = getResources()
                .getStringArray(R.array.userNames);
        String[] titleList = getResources().
                getStringArray(R.array.titles);
        String[] descriptionList = getResources().
                getStringArray(R.array.descriptions);
        TypedArray userImageResources = getResources().
                obtainTypedArray(R.array.userImages);

        //mQuestionList.clear();

        for (int i = 0; i < userNameList.length; i++) {

            Log.d(LOG_TAG, userNameList[i] +
                    titleList[i] +
                    descriptionList[i] +
                    userImageResources.getResourceId(i, 0));

            mQuestions.add(new Question(
                    userNameList[i],
                    titleList[i],
                    descriptionList[i],
                    userImageResources.getResourceId(i, 0)));
        }
        userImageResources.recycle();
        //mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.question_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search_bar);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(LOG_TAG, s);
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
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
            case R.id.settings_button:
                Log.d(LOG_TAG, "Settings clicked!");
                FirebaseAuth.getInstance().signOut();
                finish();
                return true;
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
}