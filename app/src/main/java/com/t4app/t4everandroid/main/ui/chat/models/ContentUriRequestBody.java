package com.t4app.t4everandroid.main.ui.chat.models;

import android.net.Uri;

import okhttp3.RequestBody;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import okio.Okio;
import okio.Source;

public class ContentUriRequestBody extends RequestBody {
    private final Context context;
    private final Uri uri;
    private final MediaType mediaType;
    private final long contentLength;

    public ContentUriRequestBody(Context context, Uri uri, @Nullable String mimeType, long contentLength) {
        this.context = context.getApplicationContext();
        this.uri = uri;
        this.mediaType = mimeType != null ? MediaType.parse(mimeType) : null;
        this.contentLength = contentLength;
    }

    @Nullable @Override
    public MediaType contentType() {
        return mediaType;
    }

    @Override
    public long contentLength() {
        return contentLength > 0 ? contentLength : -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        ContentResolver cr = context.getContentResolver();
        try (InputStream in = cr.openInputStream(uri)) {
            if (in == null) throw new IOException("Cannot open InputStream for uri: " + uri);
            Source source = Okio.source(in);
            sink.writeAll(source); // streaming
        }
    }
}

