package com.t4app.t4everandroid;

import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.main.Models.Media;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.NotificationItem;
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.Models.Session;
import com.t4app.t4everandroid.main.ui.chat.models.InlineData;
import com.t4app.t4everandroid.main.ui.chat.models.Messages;

public class ListenersUtils {

    public interface OnNotificationClickListener {
        void onItemChecked(NotificationItem item, boolean isChecked);
        void onMarkRead(NotificationItem item, int position);
        void onDeleteClicked(NotificationItem item, int position);
    }

    public interface OnSessionStartedOrEndCallback{
        void onSession(Session session);
    }

    public interface ConfirmationCallback {
        void onResult(boolean confirmed);
    }

    public interface OnActionPreviewImageListener {
        void onSaveImage();
        void onTakeAnother();
    }

    public interface OnUserUpdateListener{
        void onResult(User user);
    }

    public interface OnProfileActionListener {
        void onSelect(LegacyProfile profile);
        void onChat(LegacyProfile profile);
        void onQuestion(LegacyProfile profile);
        void onEdit(LegacyProfile profile, int position);
        void onDelete(LegacyProfile profile, int position);
    }

    public interface OnQuestionActionsListener{
        void onDelete(Question questionTest, int pos);
        void onEdit(Question questionTest, int pos);
    }

    public interface OnMediaActionsListener {
        void onDelete(Media mediaTest, int pos);
        void onDownload(Media mediaTest, int pos);
        void onView(Media mediaTest, int pos);
    }

    public interface OnMediaAddedListener {
        void onAddConversation(Media mediaTest);
    }

    public interface OnEmailChangeListener{
        void onEmailChanged(String newEmail);
    }

    public interface OnMessageActionsListener{
        void onDelete(Messages messages, int position);
        void onViewFile(InlineData inlineData);
        void onDeleteAudio(Messages messages, int position);
    }

    public interface CreateQuestionListener {
        void onQuestionUpdated(Question question, String answer,int pos);
    }

    public interface OnOptionImageListener {
        void onCameraSelected();
        void onGallerySelected();
    }

    public interface OnEmailSendListener {
        void onSend(String to, String subject, String message);
    }

    public interface OnActionSuccessListener{
        void onSuccess();
    }

}
