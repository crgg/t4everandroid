package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.main.Models.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messages;
    private Context context;

    private static final int VIEW_TYPE_SENT_TEXT = 1;
    private static final int VIEW_TYPE_RECEIVED_TEXT = 2;

    public ChatAdapter(Context context, List<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if (message.isSentByUser()) {
            if (message.getType() == Message.TYPE_TEXT) return VIEW_TYPE_SENT_TEXT;
        } else {
            if (message.getType() == Message.TYPE_TEXT) return VIEW_TYPE_RECEIVED_TEXT;
        }
        return VIEW_TYPE_RECEIVED_TEXT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_SENT_TEXT) {
            View view = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentTextHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedTextHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        if (holder instanceof SentTextHolder) {
            ((SentTextHolder) holder).bind(message);
        } else if (holder instanceof ReceivedTextHolder) {
            ((ReceivedTextHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SentTextHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textTime;

        public SentTextHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
        }

        void bind(Message message) {
            textMessage.setText(message.getContent());
            textTime.setText(message.getTime());
        }
    }

    public static class ReceivedTextHolder extends RecyclerView.ViewHolder {
        TextView textMessage, textTime;

        public ReceivedTextHolder(@NonNull View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
        }

        void bind(Message message) {
            textMessage.setText(message.getContent());
            textTime.setText(message.getTime());
        }
    }

    public void updateMessages(List<Message> newMessages) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MessageDiffCallback(this.messages, newMessages));
        this.messages.clear();
        this.messages.addAll(newMessages);
        diffResult.dispatchUpdatesTo(this);
    }

    public static class MessageDiffCallback extends DiffUtil.Callback {
        private final List<Message> oldList;
        private final List<Message> newList;

        public MessageDiffCallback(List<Message> oldList, List<Message> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() { return oldList.size(); }
        @Override
        public int getNewListSize() { return newList.size(); }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
