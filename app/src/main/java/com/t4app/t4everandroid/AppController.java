package com.t4app.t4everandroid;

import android.app.Application;

import com.t4app.t4everandroid.network.ApiServices;
import com.t4app.t4everandroid.network.RetrofitClient;
import com.t4app.t4everandroid.room.AppDatabase;

public class AppController extends Application {
    private static AppController instance;
    private static AppDatabase database;

    private static ApiServices apiServices = null;

    @Override
    public void onCreate() {
        super.onCreate();
//        FirebaseApp.initializeApp(this);
        instance = this;
        SessionManager.initialize(getApplicationContext());
        database = AppDatabase.getInstance(this);
    }

    public static ApiServices getApiServices(){
        if (apiServices == null){
            apiServices = RetrofitClient.getRetrofitClient().create(ApiServices.class);
        }
        return apiServices;
    }

    public static AppDatabase getDatabase() {
        if (database == null) {
            throw new IllegalStateException("The database is not yet initialized.");
        }
        return database;
    }

    public static AppController getInstance() {
        return instance;
    }
}