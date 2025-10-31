package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.Models.Interactions;
import com.t4app.t4everandroid.main.ui.media.AudioPlayerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.InteractionsVH> {

    private List<Interactions> interactions;
    private Context context;
    private ListenersUtils.OnInteractionActionsListener listener;
    public ChatAdapter(Context context, List<Interactions> interactions, ListenersUtils.OnInteractionActionsListener listener) {
        this.context = context;
        this.interactions = interactions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ChatAdapter.InteractionsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message_sent, parent, false);
        return new InteractionsVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.InteractionsVH holder, int position) {
        Interactions interaction = interactions.get(position);
        boolean isLast = position == getItemCount() - 1;

        holder.deleteInteraction.setVisibility(isLast ? View.VISIBLE : View.GONE);

        holder.deleteInteraction.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                int currentPos = holder.getAbsoluteAdapterPosition();
                if (currentPos != RecyclerView.NO_POSITION){
                    if (interaction.getTextFromUser() != null){
                        listener.onDelete(interaction, currentPos);
                    }else if (interaction.getUserAudioUrl() != null){
                        listener.onDeleteAudio(interaction, currentPos);
                    }
                }
            }
        });

        holder.bind(interaction);
    }

    @Override
    public int getItemCount() {
        return interactions.size();
    }

    public static class InteractionsVH extends RecyclerView.ViewHolder {
        LinearLayout sendMessageContainer;
        TextView textMessage;
        TextView textTime;
        AppCompatImageButton deleteInteraction;

        AudioPlayerView audioPlayerView;

        LinearLayout responseMessageContainer;
        TextView textMessageAssistant;
        TextView textTimeAssistant;
        public InteractionsVH(@NonNull View itemView) {
            super(itemView);
            sendMessageContainer = itemView.findViewById(R.id.send_message_container);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);

            responseMessageContainer = itemView.findViewById(R.id.response_message_container);
            textMessageAssistant = itemView.findViewById(R.id.textMessageAssistant);
            textTimeAssistant = itemView.findViewById(R.id.textTimeAssistant);
            audioPlayerView = itemView.findViewById(R.id.item_audio);
            deleteInteraction = itemView.findViewById(R.id.delete_interaction);
        }

        void bind(Interactions interaction) {
            if (interaction.getTextFromUser() != null){
                sendMessageContainer.setVisibility(View.VISIBLE);
                audioPlayerView.setVisibility(View.GONE);
            }else if (interaction.getUserAudioUrl() != null){
                sendMessageContainer.setVisibility(View.GONE);
                audioPlayerView.setVisibility(View.VISIBLE);
                setupAudioPlayer(Uri.parse(interaction.getUserAudioUrl()));
            }
            if (interaction.getAssistantTextResponse() != null){
                responseMessageContainer.setVisibility(View.VISIBLE);
                textMessageAssistant.setText(interaction.getAssistantTextResponse());
                textTimeAssistant.setText(interaction.getTimestamp());
            }else{
                responseMessageContainer.setVisibility(View.GONE);
                textMessage.setText(interaction.getTextFromUser());
                textTime.setText(interaction.getTimestamp());
            }
        }
        public void setupAudioPlayer(Uri audioUri) {
            if (audioPlayerView != null) {
                audioPlayerView.setVisibility(View.VISIBLE);
                audioPlayerView.setAudioUri(audioUri);
            }
        }
    }

    public void updateMessages(List<Interactions> newMessages) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MessageDiffCallback(this.interactions, newMessages));
        this.interactions.clear();
        this.interactions.addAll(newMessages);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addMessage(Interactions message) {
        if (message == null) return;
        interactions.add(message);
        notifyItemInserted(interactions.size() - 1);

        if (interactions.size() > 1) {
            notifyItemChanged(interactions.size() - 2);
        }
    }

    public static class MessageDiffCallback extends DiffUtil.Callback {
        private final List<Interactions> oldList;
        private final List<Interactions> newList;

        public MessageDiffCallback(List<Interactions> oldList, List<Interactions> newList) {
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

    public void deleteItem(int position) {
        if (position >= 0 && position < interactions.size()) {
            interactions.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, interactions.size());
            notifyItemChanged(getItemCount() - 1);
        }
    }

}
