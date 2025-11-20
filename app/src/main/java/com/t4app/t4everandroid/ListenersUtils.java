package com.t4app.t4everandroid;

import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.main.Models.Interactions;
import com.t4app.t4everandroid.main.Models.Media;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.main.Models.NotificationItem;
import com.t4app.t4everandroid.main.Models.Question;
import com.t4app.t4everandroid.main.Models.Session;

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

    public interface OnInteractionActionsListener{
        void onDelete(Interactions interactions, int position);
        void onDeleteAudio(Interactions interactions, int position);
    }

}
