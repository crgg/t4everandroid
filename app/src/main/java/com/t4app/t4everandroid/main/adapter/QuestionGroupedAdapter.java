package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.Models.ListItem;
import com.t4app.t4everandroid.main.Models.QuestionTest;

import java.util.List;

public class QuestionGroupedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> items;
    private Context context;

    public QuestionGroupedAdapter(Context context, List<ListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ListItem.TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_category_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_question, parent, false);
            return new QuestionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ListItem item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(item.getCategory());
        } else if (holder instanceof QuestionViewHolder) {
            ((QuestionViewHolder) holder).bind(item.getQuestion());
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView txtCategoryTitle;
        HeaderViewHolder(View v) {
            super(v);
            txtCategoryTitle = v.findViewById(R.id.txtCategoryTitle);
        }
        void bind(String category) {
            txtCategoryTitle.setText(category);
        }
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView txtQuestion, txtAnswer;
        QuestionViewHolder(View v) {
            super(v);
            txtQuestion = v.findViewById(R.id.txtQuestion);
            txtAnswer = v.findViewById(R.id.txtAnswer);
        }
        void bind(QuestionTest question) {
            txtQuestion.setText(question.getQuestion());
            txtAnswer.setText(question.getAnswer());
        }
    }

    public void updateList(List<ListItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    public void addQuestionToCategory(QuestionTest question) {
        String category = question.getCategory();
        int insertPos = -1;

        for (int i = 0; i < items.size(); i++) {
            ListItem item = items.get(i);
            if (item.getType() == ListItem.TYPE_HEADER && item.getCategory().equals(category)) {
                insertPos = i + 1;
                while (insertPos < items.size() &&
                        items.get(insertPos).getType() == ListItem.TYPE_ITEM &&
                        items.get(insertPos).getCategory().equals(category)) {
                    insertPos++;
                }
                break;
            }
        }
        if (insertPos == -1) {
            items.add(new ListItem(ListItem.TYPE_HEADER, category, null));
            items.add(new ListItem(ListItem.TYPE_ITEM, category, question));
            notifyItemRangeInserted(items.size() - 2, 2);
        } else {
            items.add(insertPos, new ListItem(ListItem.TYPE_ITEM, category, question));
            notifyItemInserted(insertPos);
        }
    }

}
