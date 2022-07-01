package com.example.forum.Others;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forum.Activities.AnswerActivity;
import com.example.forum.Models.Question;
import com.example.forum.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> implements Filterable {

    public ArrayList<Question> mQuestionData = new ArrayList<>();;
    public ArrayList<Question> mQuestionDataAll = new ArrayList<>();;
    public Context mContext;
    public int lastPosition = -1;
    private StorageReference mStorageReference;

    public QuestionAdapter(Context context, ArrayList<Question> itemsData){
        this.mQuestionData = itemsData;
        this.mQuestionDataAll = itemsData;
        this.mContext = context;
    }

    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(QuestionAdapter.ViewHolder holder, int position) {
        Question currentItem = mQuestionData.get(position);

        holder.bindTo(currentItem);
        if(holder.getAdapterPosition() > lastPosition){
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mQuestionData.size();
    }

    @Override
    public Filter getFilter() {
        return questionFilter;
    }

    private Filter questionFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Question> filteredList = new ArrayList<>();
            FilterResults results = new FilterResults();

            if(charSequence == null || charSequence.length() == 0){
                results.count = mQuestionDataAll.size();
                results.values = mQuestionDataAll;
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for(Question item: mQuestionDataAll){
                    if(item.getUserName().toLowerCase().contains(filterPattern)){
                        filteredList.add(item);
                    }
                }
                results.count = filteredList.size();
                results.values = mQuestionDataAll;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mQuestionData = (ArrayList) filterResults.values;
            notifyDataSetChanged();
        }
    };

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView mUserImage;
        private TextView mUserNameText;
        private TextView mTitleText;
        private TextView mDescriptionText;

        public ViewHolder(View itemView) {
            super(itemView);

            mUserImage = itemView.findViewById(R.id.userImage);
            mUserNameText = itemView.findViewById(R.id.userName);
            mTitleText = itemView.findViewById(R.id.title);
            mDescriptionText = itemView.findViewById(R.id.description);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Open question");
                    Intent intent = new Intent(view.getContext(), AnswerActivity.class);
                    intent.putExtra("TITLE", mQuestionData.get(getAdapterPosition()).getTitle());
                    intent.putExtra("DESC", mQuestionData.get(getAdapterPosition()).getDescription());
                    intent.putExtra("IMAGE", mQuestionData.get(getAdapterPosition()).getImageResource());
                    intent.putExtra("NAME", mQuestionData.get(getAdapterPosition()).getUserName());
                    intent.putStringArrayListExtra("ANSWERS",(ArrayList<String>) mQuestionData.get(getAdapterPosition()).getAnswers());
                    view.getContext().startActivity(intent);
                }
            });
        }

        public void bindTo(Question currentItem) {
            final ProgressDialog pd = new ProgressDialog(mContext);
            pd.setTitle("Kis türelmet...");
            pd.show();

            mUserNameText.setText(currentItem.getUserName());
            mTitleText.setText(currentItem.getTitle());
            mDescriptionText.setText(currentItem.getDescription());

            mStorageReference = FirebaseStorage.getInstance().getReference().child("images/" + currentItem.getImageResource());

            try{
                final File localFile = File.createTempFile(currentItem.getImageResource(), "png");
                mStorageReference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                        ((ImageView) mUserImage.findViewById(R.id.userImage)).setImageBitmap(bitmap);
                        pd.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        pd.dismiss();
                    }
                });

            }catch (IOException e){
                e.printStackTrace();
            }

        }
    }
}