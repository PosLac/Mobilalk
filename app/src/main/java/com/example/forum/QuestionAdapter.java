package com.example.forum;

import android.content.Context;
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

import com.bumptech.glide.Glide;

import java.io.FilterReader;
import java.util.ArrayList;
import java.util.Locale;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> implements Filterable {

    private ArrayList<Question> mQuestionData = new ArrayList<>();;
    private ArrayList<Question> mQuestionDataAll = new ArrayList<>();;
    private Context mContext;
    private int lastPosition = -1;

    QuestionAdapter(Context context, ArrayList<Question> itemsData){
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

            itemView.findViewById(R.id.open).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    Log.d("Activity", "Kérdés megnyitása.");
                }
            });
        }

        public void bindTo(Question currentItem) {

            mUserNameText.setText(currentItem.getUserName());
            mTitleText.setText(currentItem.getTitle());
            mDescriptionText.setText(currentItem.getDescription());
            Glide.with(mContext).load(currentItem.getImageResource()).into(mUserImage);
        }
    }
}
