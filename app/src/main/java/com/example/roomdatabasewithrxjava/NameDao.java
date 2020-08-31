package com.example.roomdatabasewithrxjava;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface NameDao {
    @Insert
    Completable insert(Name... names);

    @Delete
    Single<Integer> delete(Name...names);

    @Query("DELETE FROM table_name WHERE name = :name")
    Single<Integer> deleteName(String name);

    @Query("SELECT name FROM table_name WHERE name = :name")
    Flowable<List<String>> getName(String name);

    @Query("SELECT name FROM table_name")
    Flowable<List<String>> getAllName();
}
