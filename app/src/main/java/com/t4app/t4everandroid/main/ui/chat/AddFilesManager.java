package com.t4app.t4everandroid.main.ui.chat;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.t4app.t4everandroid.R;
import com.t4app.t4everandroid.SafeClickListener;
import com.t4app.t4everandroid.main.ui.media.ViewerDocumentBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class AddFilesManager {

    private final LinearLayout llAttachments;
    private final LayoutInflater inflater;
    private final FragmentManager fm;

    private final List<Uri> selectedUris = new ArrayList<>();

    public AddFilesManager(LayoutInflater inflater, FragmentManager fm,LinearLayout llAttachments) {
        this.inflater = inflater;
        this.llAttachments = llAttachments;
        this.fm = fm;
    }

    public List<Uri> getSelectedUris() {
        return selectedUris;
    }

    public void addImage(Uri uri) {
        if (uri == null) return;

        View item = inflater.inflate(R.layout.item_attachment_image, llAttachments, false);
        ImageView iv = item.findViewById(R.id.ivPreview);
        ImageView ivTypeIcon = item.findViewById(R.id.ivTypeIcon);
        ImageButton btnRemove = item.findViewById(R.id.btnRemove);

        String mime = null;
        try { mime = iv.getContext().getContentResolver().getType(uri); } catch (Exception ignored) {}
        if (mime == null) mime = "";

        Glide.with(iv.getContext()).clear(iv);
        iv.setImageDrawable(null);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ivTypeIcon.setVisibility(View.GONE);


         String type;
        if (mime.startsWith("image/")) {
            Glide.with(iv.getContext())
                    .load(uri)
                    .centerCrop()
                    .into(iv);

            type = "image";
        } else if (mime.startsWith("audio/")) {
            iv.setBackgroundResource(R.drawable.bg_rounded);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setImageResource(R.drawable.ic_headset);
            ivTypeIcon.setImageResource(R.drawable.ic_headset);
            ivTypeIcon.setVisibility(View.VISIBLE);
            type = "audio";
        } else if (mime.startsWith("video/")) {
            iv.setBackgroundResource(R.drawable.bg_rounded);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setImageResource(R.drawable.ic_play);
            ivTypeIcon.setImageResource(R.drawable.ic_play);
            ivTypeIcon.setVisibility(View.VISIBLE);

            type = "video";

        } else {
            type = "";

            iv.setBackgroundResource(R.drawable.bg_rounded);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setImageResource(R.drawable.ic_doc);

            ivTypeIcon.setImageResource(R.drawable.ic_doc);
            ivTypeIcon.setVisibility(View.VISIBLE);
        }

        selectedUris.add(uri);

        btnRemove.setOnClickListener(v -> {
            llAttachments.removeView(item);
            selectedUris.remove(uri);
        });

        item.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                ViewerDocumentBottomSheet bottomSheet = ViewerDocumentBottomSheet.newInstance(type, true, uri);
                bottomSheet.show(fm, "view_media");
            }
        });

        llAttachments.addView(item);
    }


    public static String getDisplayName(Context ctx, Uri uri) {
        Cursor c = null;
        try {
            c = ctx.getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (idx >= 0) return c.getString(idx);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return "file_" + System.currentTimeMillis();
    }

    public static long getSize(Context ctx, Uri uri) {
        Cursor c = null;
        try {
            c = ctx.getContentResolver().query(uri, null, null, null, null);
            if (c != null && c.moveToFirst()) {
                int idx = c.getColumnIndex(OpenableColumns.SIZE);
                if (idx >= 0) return c.getLong(idx);
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null) c.close();
        }
        return -1;
    }

    public static String getMime(Context ctx, Uri uri) {
        return ctx.getContentResolver().getType(uri);
    }



    public void clearList(){
        selectedUris.clear();
    }
}
