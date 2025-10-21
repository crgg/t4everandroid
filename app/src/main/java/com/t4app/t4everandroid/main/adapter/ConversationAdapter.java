package com.t4app.t4everandroid.main.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.ConversationTest;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ConversationTest> items;
    private Context context;
    private static ListenersUtils.OnConversationActionsListener listener;

    public ConversationAdapter(Context context, List<ConversationTest> items,
                               ListenersUtils.OnConversationActionsListener listener) {
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override public int getItemCount() {
        return items.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_conversation, parent, false);
            return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConversationTest item = items.get(position);
        ((ConversationViewHolder) holder).bind(item);
    }

    public Context getContext() {
        return context;
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView txtConversation, txtAnswer, txtNoAnswer;
        AppCompatImageButton btnDelete;
        ConversationViewHolder(View v) {
            super(v);
            txtConversation = v.findViewById(R.id.txtConversation);
            txtAnswer = v.findViewById(R.id.textConversation);
            txtNoAnswer = v.findViewById(R.id.text_no_answer);
            btnDelete = v.findViewById(R.id.btn_delete_conversation);
        }
        void bind(ConversationTest conversationTest) {
            txtConversation.setText(conversationTest.getType());
            if (conversationTest.getType().equalsIgnoreCase("text")) {
                txtAnswer.setText(conversationTest.getText());
            }
            btnDelete.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = getAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION){
                        listener.onDelete(conversationTest, currentPos);
                    }
                }
            });
        }

    }

    public void addItem(ConversationTest newItem) {
        items.add(newItem);
        notifyItemInserted(items.size() - 1);
    }

    public void updateList(List<ConversationTest> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

}
