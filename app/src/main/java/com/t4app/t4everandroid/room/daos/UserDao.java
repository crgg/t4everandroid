package com.t4app.t4everandroid.room.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.t4app.t4everandroid.Login.models.User;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    User getUserById(int id);

    @Query("UPDATE user SET avatarUrl = :avatarUrl WHERE id = :id")
    void setAvatarUser(int id, String avatarUrl);

    @Query("SELECT * FROM user WHERE id = :id LIMIT 1")
    LiveData<User> getUserLiveById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertStrict(User user);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertIgnore(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);
}
