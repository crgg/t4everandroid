package com.t4app.t4everandroid.main.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.t4app.t4everandroid.ListenersUtils;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.ui.chat.models.InlineData;
import com.t4app.t4everandroid.main.ui.chat.models.Messages;
import com.t4app.t4everandroid.main.ui.chat.models.Part;
import com.t4app.t4everandroid.main.ui.media.AudioPlayerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "CHAT_ADAPTER";

    private static final int VT_SENT = 1;      // user
    private static final int VT_RECEIVED = 2;  // model

    private final List<Messages> messages;
    private final Context context;
    private static LayoutInflater inflater;
    private static ListenersUtils.OnMessageActionsListener listener;

    public ChatAdapter(Context context, List<Messages> messages, LayoutInflater inflater,
                       ListenersUtils.OnMessageActionsListener listener) {
        this.context = context;
        this.messages = messages;
        this.listener = listener;
        this.inflater = inflater;
    }

    @Override
    public int getItemViewType(int position) {
        Messages m = messages.get(position);
        if (m != null && m.role != null && m.role.equalsIgnoreCase("user")) return VT_SENT;
        return VT_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VT_SENT) {
            View v = inflater.inflate(R.layout.item_message_sent, parent, false);
            return new SentVH(v);
        } else {
            View v = inflater.inflate(R.layout.item_message_received, parent, false);
            return new ReceivedVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Messages message = messages.get(position);
        boolean isLast = position == getItemCount() - 1;

        if (holder instanceof SentVH) {
            SentVH vh = (SentVH) holder;

            vh.deleteInteraction.setVisibility(isLast ? View.VISIBLE : View.GONE);
            vh.deleteInteraction.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View v) {
                    int currentPos = vh.getAbsoluteAdapterPosition();
                    if (currentPos != RecyclerView.NO_POSITION) {
                        listener.onDelete(message, currentPos);
                    }
                }
            });

            vh.bind(message);

        } else if (holder instanceof ReceivedVH) {
            ReceivedVH vh = (ReceivedVH) holder;
            vh.bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class SentVH extends RecyclerView.ViewHolder {
        LinearLayout sendMessageContainer;
        LinearLayout containerFiles;
        TextView textMessage;
        TextView textTime;
        AppCompatImageButton deleteInteraction;
        AudioPlayerView audioPlayerView;

        public SentVH(@NonNull View itemView) {
            super(itemView);
            sendMessageContainer = itemView.findViewById(R.id.send_message_container);
            containerFiles = itemView.findViewById(R.id.filesInMessageContainer);
            textMessage = itemView.findViewById(R.id.textMessage);
            textTime = itemView.findViewById(R.id.textTime);
            audioPlayerView = itemView.findViewById(R.id.item_audio);
            deleteInteraction = itemView.findViewById(R.id.delete_message);
        }

        void bind(Messages message) {
            containerFiles.removeAllViews();
            containerFiles.setVisibility(View.GONE);

            sendMessageContainer.setVisibility(View.GONE);
            audioPlayerView.setVisibility(View.GONE);
            textMessage.setText(null);
            textTime.setText(null);

            if (message == null || message.getParts() == null || message.getParts().isEmpty()) return;

            for (int i = 0; i < message.getParts().size(); i++) {
                Part part = message.getParts().get(i);

                String txt = (part != null) ? part.getText() : null;
                boolean hasText = txt != null && !txt.isEmpty();
                boolean hasInline = part != null && part.getInlineData() != null;

                if (hasText && !hasInline) {
                    textMessage.setText(txt);
                    textTime.setText(message.getCreatedAt());
                    sendMessageContainer.setVisibility(View.VISIBLE);

                } else if (hasInline) {
                    containerFiles.setVisibility(View.VISIBLE);
                    addInlinePreviewToContainer(containerFiles, part.getInlineData());
                }
            }
        }

        public void addInlinePreviewToContainer(LinearLayout containerFiles, InlineData inlineData) {
            if (inlineData == null || inlineData.getFileUrl() == null) return;

            View item = inflater.inflate(R.layout.item_inline_preview, containerFiles, false);

            ImageView ivPreview = item.findViewById(R.id.ivPreview);
            View vDim = item.findViewById(R.id.vDim);
            ImageView ivPlay = item.findViewById(R.id.ivPlay);

            Glide.with(ivPreview.getContext()).clear(ivPreview);
            ivPreview.setImageDrawable(null);

            String mime = inlineData.getMimeType() != null ? inlineData.getMimeType() : "";
            String url  = inlineData.getFileUrl();

            boolean isImage = mime.startsWith("image/");
            boolean isVideo = mime.startsWith("video/");
            boolean isAudio = mime.startsWith("audio/");

            vDim.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
            ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);



            if (isImage) {
                Glide.with(ivPreview.getContext())
                        .load(url)
                        .centerCrop()
                        .into(ivPreview);

            } else if (isVideo) {
                vDim.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.VISIBLE);

                Glide.with(ivPreview.getContext())
                        .load(url)
                        .centerCrop()
                        .into(ivPreview);

            } else if (isAudio) {
                vDim.setVisibility(View.VISIBLE);
                ivPlay.setVisibility(View.VISIBLE);

                ivPreview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                ivPreview.setImageResource(R.drawable.ic_headset);

            } else {
                ivPreview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                ivPreview.setImageResource(R.drawable.ic_doc);
            }

            item.setOnClickListener(v -> {
                listener.onViewFile(inlineData);
            });

            containerFiles.addView(item);
        }

        public void setupAudioPlayer(Uri audioUri) {
            if (audioPlayerView != null) {
                audioPlayerView.setVisibility(View.VISIBLE);
                audioPlayerView.setAudioUri(audioUri);
            }
        }
    }

    public static class ReceivedVH extends RecyclerView.ViewHolder {
        LinearLayout responseMessageContainer;
        TextView textMessageAssistant;
        TextView textTimeAssistant;

        public ReceivedVH(@NonNull View itemView) {
            super(itemView);
            responseMessageContainer = itemView.findViewById(R.id.response_message_container);
            textMessageAssistant = itemView.findViewById(R.id.textMessageAssistant);
            textTimeAssistant = itemView.findViewById(R.id.textTimeAssistant);
        }

        void bind(Messages message) {
            if (message == null) {
                Log.w(TAG, "ReceivedVH: message == null");
                return;
            }

            Log.d(TAG, "ReceivedVH render id=" + message.getId()
                    + " role=" + message.role
                    + " createdAt=" + message.getCreatedAt()
                    + " parts=" + (message.getParts() != null ? message.getParts().size() : 0));

            if (message.getParts() == null || message.getParts().isEmpty()) {
                Log.w(TAG, "ReceivedVH: no parts");
                return;
            }

            for (int i = 0; i < message.getParts().size(); i++) {
                Part part = message.getParts().get(i);

                String txt = (part != null) ? part.getText() : null;
                boolean hasText = txt != null && !txt.isEmpty();
                boolean hasInline = part != null && part.getInlineData() != null;

                Log.d(TAG, "ReceivedVH part[" + i + "] hasText=" + hasText + " hasInline=" + hasInline);

                if (hasText && !hasInline) {
                    responseMessageContainer.setVisibility(View.VISIBLE);
                    textMessageAssistant.setText(txt);
                    textTimeAssistant.setText(message.getCreatedAt());
                } else if (hasInline) {
                    String mime = part.getInlineData().mimeType;
                    Log.w(TAG, "ReceivedVH: inline not handled. mime=" + mime);
                }
            }
        }
    }

    public void updateMessages(List<Messages> newMessages) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MessageDiffCallback(this.messages, newMessages));
        this.messages.clear();
        this.messages.addAll(newMessages);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addMessage(List<Messages> newMessages) {
        if (newMessages == null || newMessages.isEmpty()) return;

        int startPos = messages.size();
        messages.addAll(newMessages);
        notifyItemRangeInserted(startPos, newMessages.size());
        int newLastIndex = messages.size() - 1;
        int prevLastIndex = newLastIndex - newMessages.size();
        if (prevLastIndex >= 0) {
            notifyItemChanged(prevLastIndex);
        }
    }


    public static class MessageDiffCallback extends DiffUtil.Callback {
        private final List<Messages> oldList;
        private final List<Messages> newList;

        public MessageDiffCallback(List<Messages> oldList, List<Messages> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override public int getOldListSize() { return oldList.size(); }
        @Override public int getNewListSize() { return newList.size(); }

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
        if (position >= 0 && position < messages.size()) {
            messages.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, messages.size());
            notifyItemChanged(getItemCount() - 1);
        }
    }

    public List<Messages> getMessages() {
        return messages;
    }
}

