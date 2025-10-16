package com.t4app.t4everandroid;

import com.t4app.t4everandroid.main.Models.LegacyProfile;

public class ListenersUtils {

    public interface ConfirmationCallback {
        void onResult(boolean confirmed);
    }

    public interface OnProfileActionListener {
        void onSelect(LegacyProfile profile);
        void onChat(LegacyProfile profile);
        void onEdit(LegacyProfile profile, int position);
        void onDelete(LegacyProfile profile, int position);
    }

}
