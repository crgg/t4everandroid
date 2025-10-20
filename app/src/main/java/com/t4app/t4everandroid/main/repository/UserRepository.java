package com.t4app.t4everandroid.main.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;

import com.t4app.t4everandroid.AppController;
import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.room.AppDatabase;
import com.t4app.t4everandroid.room.daos.UserDao;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserRepository {
    private static final String TAG = "USER_REPOSITORY";

    private final UserDao dao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public UserRepository(){
        AppDatabase db = AppController.getDatabase();
        dao = db.userDao();
    }

    public void updateUser(User user){
        executor.execute(() -> {
            if (user != null){
                User exist = dao.getUserById(user.id);

                if (exist == null){
                    Log.d(TAG, "NO EXIST ENTRY IN INSERT");
                    dao.insert(user);
                } else if (!exist.equals(user)) {
                    Log.d(TAG, "EXIST ENTRY IN UPDATE");
                    dao.update(user);
                }
            }
        });
    }

    public LiveData<User> getUser(int id){
        return dao.getUserLiveById(id);
    }

    public void updateImagePath(int id, String avatarUrl){
        executor.execute(() -> {
            dao.setAvatarUser(id, avatarUrl);
        });
    }
}
