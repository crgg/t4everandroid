package com.t4app.t4everandroid.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.t4app.t4everandroid.Login.models.User;
import com.t4app.t4everandroid.main.Models.LegacyProfile;
import com.t4app.t4everandroid.room.converters.LegacyProfileConverter;
import com.t4app.t4everandroid.room.daos.LegacyProfileDao;
import com.t4app.t4everandroid.room.daos.UserDao;

@Database(entities = {LegacyProfile.class, User.class}, version = 2, exportSchema = false)
@TypeConverters({LegacyProfileConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract LegacyProfileDao legacyProfileDao();
    public abstract UserDao userDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "t4ever_android")
                            .fallbackToDestructiveMigration(true)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
