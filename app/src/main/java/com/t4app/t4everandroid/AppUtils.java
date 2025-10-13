package com.t4app.t4everandroid;

import android.util.Patterns;

public class AppUtils {

    public static boolean isValidEmail(String email){
        return Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }
}
