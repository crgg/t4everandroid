package com.t4app.t4everandroid.main.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.MediaTest;

import java.util.List;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MediaTest> items;
    private Context context;
    private static ListenersUtils.OnConversationActionsListener listener;

    public MediaAdapter(Context context, List<MediaTest> items,
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
                    .inflate(R.layout.item_media, parent, false);
            return new ConversationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MediaTest item = items.get(position);
        ((ConversationViewHolder) holder).bind(item);
    }

    public Context getContext() {
        return context;
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {
        TextView txtConversation, txtAnswer, txtNoAnswer;
        AppCompatImageButton btnDelete;
        ImageView iconType;
        ConversationViewHolder(View v) {
            super(v);
            txtConversation = v.findViewById(R.id.txtConversation);
            txtAnswer = v.findViewById(R.id.textConversation);
            txtNoAnswer = v.findViewById(R.id.text_no_answer);
            btnDelete = v.findViewById(R.id.btn_delete_conversation);
            iconType = v.findViewById(R.id.icon_type);
        }
        void bind(MediaTest mediaTest) {
            txtConversation.setText(mediaTest.getType());

            switch (mediaTest.getType()){
                case "text":
                    iconType.setImageResource(R.drawable.ic_doc);
                    break;
                case "video":
                    iconType.setImageResource(R.drawable.ic_video);
                    break;
                case "audio":
                    iconType.setImageResource(R.drawable.ic_conversations);
                    break;

            }

            if (mediaTest.getType().equalsIgnoreCase("text")) {
                txtAnswer.setText(mediaTest.getText());
            }
            btnDelete.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = getAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION){
                        listener.onDelete(mediaTest, currentPos);
                    }
                }
            });
        }

    }

    public void addItem(MediaTest newItem) {
        items.add(newItem);
        notifyItemInserted(items.size() - 1);
    }

    public void updateList(List<MediaTest> newList) {
        this.items = newList;
        notifyDataSetChanged();
    }

}
