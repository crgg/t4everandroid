package com.t4app.t4everandroid;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class SelectImageUtils {
    private static final String TAG = "SELECT_IMAGE_UTILS";

    private final Activity activity;
    public SelectImageUtils(Activity activity) {
        this.activity = activity;
    }

    public String getRealPathFromUri(Uri uri) {
        String result = null;

        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }

        if (result == null) {
            String path = uri.getPath();
            int cut = path.lastIndexOf('/');
            if (cut != -1) {
                result = path.substring(cut + 1);
            }
        }

        return result;
    }

    public File getFileFromUri(Uri uri){
        Bitmap bitmap = getBitmapFromUri(uri, activity);
        return bitmapToFile(compressBitmap(bitmap, 1024, 1024),activity);
    }

    public Bitmap getBitmapFromUri(Uri uri, Context context) {
        Bitmap bitmap = null;
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromUri: ", e);
        }
        return bitmap;
    }

    private File bitmapToFile(Bitmap bitmap, Context context) {
        File file = new File(context.getCacheDir(), "file_" + System.currentTimeMillis() + ".png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 80, out);
            out.flush();
        } catch (IOException e) {
            Log.e("FileConversion", "Error al convertir Bitmap a archivo", e);
        }
        return file;
    }

    public Bitmap compressBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float aspectRatio = (float) width / height;

        if (width > maxWidth || height > maxHeight) {
            if (width > height) {
                width = maxWidth;
                height = (int) (maxWidth / aspectRatio);
            } else {
                height = maxHeight;
                width = (int) (maxHeight * aspectRatio);
            }
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, false);
    }

}
