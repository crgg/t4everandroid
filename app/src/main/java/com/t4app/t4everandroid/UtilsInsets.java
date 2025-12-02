package com.t4app.t4everandroid;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class UtilsInsets {

    public static void applySafeArea(@NonNull View root) {
        final int baseLeft   = root.getPaddingLeft();
        final int baseTop    = root.getPaddingTop();
        final int baseRight  = root.getPaddingRight();
        final int baseBottom = root.getPaddingBottom();

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            int mask = WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout();

            Insets safe = insets.getInsets(mask);

            int left   = baseLeft   + safe.left;
            int top    = baseTop    + safe.top;
            int right  = baseRight  + safe.right;
            int bottom = baseBottom + safe.bottom;

            if (safe.bottom > 0) {
                bottom += dpToPx(v.getContext(), 6);
            }

            v.setPadding(left, top, right, bottom);

            return insets;
        });
    }


    private static int dpToPx(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }
}
