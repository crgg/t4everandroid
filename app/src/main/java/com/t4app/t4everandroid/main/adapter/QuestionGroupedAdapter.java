package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.ListItem;
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.Models.QuestionTest;

import java.util.List;

public class QuestionGroupedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ListItem> items;
    private Context context;
    private static ListenersUtils.OnQuestionActionsListener listener;

    public QuestionGroupedAdapter(Context context, List<ListItem> items, ListenersUtils.OnQuestionActionsListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
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

    public Context getContext() {
        return context;
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
        TextView txtQuestion, txtAnswer, txtNoAnswer;
        AppCompatImageButton btnDelete, btnEdit;
        QuestionViewHolder(View v) {
            super(v);
            txtQuestion = v.findViewById(R.id.txtQuestion);
            txtAnswer = v.findViewById(R.id.txtAnswer);
            txtNoAnswer = v.findViewById(R.id.text_no_answer);
            btnDelete = v.findViewById(R.id.btn_delete_question);
            btnEdit = v.findViewById(R.id.btn_edit_question);
        }
        void bind(Question question) {
            txtQuestion.setText(question.getQuestion());
            if (question.getAnsweredAt() == null){
                txtNoAnswer.setVisibility(View.VISIBLE);
            }else{
                txtNoAnswer.setVisibility(View.GONE);
            }
//            txtAnswer.setText(question.get());

            btnDelete.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = getAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION){
                        listener.onDelete(question, currentPos);
                    }
                }
            });

            btnEdit.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = getAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION){
                        listener.onEdit(question, currentPos);
                    }
                }
            });
        }

    }

    public void updateList(List<ListItem> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

    public void addQuestionToCategory(Question question) {
        String category = question.getDimension();
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

    public void removeItem(int position) {
        if (position < 0 || position >= items.size()) return;

        ListItem toRemove = items.get(position);

        if (toRemove.getType() == ListItem.TYPE_HEADER) return;

        String category = toRemove.getCategory();

        items.remove(position);
        notifyItemRemoved(position);

        boolean hasMoreInCategory = false;

        for (ListItem item : items) {
            if (item.getType() == ListItem.TYPE_ITEM &&
                    item.getCategory().equals(category)) {
                hasMoreInCategory = true;
                break;
            }
        }

        if (!hasMoreInCategory) {
            int headerPos = -1;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getType() == ListItem.TYPE_HEADER &&
                        items.get(i).getCategory().equals(category)) {
                    headerPos = i;
                    break;
                }
            }

            if (headerPos != -1) {
                items.remove(headerPos);
                notifyItemRemoved(headerPos);
            }
        }
    }

    public void updateItem(int position, Question updatedQuestion) {
        if (position < 0 || position >= items.size()) return;

        ListItem item = items.get(position);

        if (item.getType() == ListItem.TYPE_ITEM) {
            item.setQuestion(updatedQuestion);
            notifyItemChanged(position);
        }
    }



}
