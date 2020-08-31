package com.example.roomdatabasewithrxjava;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Name.class}, version = 1, exportSchema = false)
public abstract class NameDatabase extends RoomDatabase {

    public static final String DB_NAME = "name_db";

    private static NameDatabase INSTANCE;
    public abstract NameDao nameDao();

    public NameDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (NameDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), NameDatabase.class, "name_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
