package com.example.roomdatabasewithrxjava;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class InstrumentedTest {
    private NameDatabase db;
    private NameDao dao;

    private Name name1 = new Name("Mina");
    private Name name2 = new Name("Shoaib");
    private Name name3 = new Name("Rahman");

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.example.roomdatabasewithrxjava", appContext.getPackageName());
    }

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, NameDatabase.class).build();
        dao = db.nameDao();

        dao.insert(name1, name2, name3).blockingAwait();
    }

    @After
    public void closeDb() {
        db.close();
    }


    @Test
    public void testDbOperations() {
        dao.getName(name1.getName()).test().assertValue(new ArrayList<>(Arrays.asList(name1.getName())));
        dao.getName(name2.getName()).test().assertValue(new ArrayList<>(Arrays.asList(name2.getName())));
        dao.getName(name3.getName()).test().assertValue(new ArrayList<>(Arrays.asList(name3.getName())));

        dao.deleteName(name1.getName()).test().assertComplete();
        dao.deleteName(name2.getName()).test().assertComplete();
        dao.deleteName(name3.getName()).test().assertComplete();

        dao.getName(name1.getName()).test().assertValue(new ArrayList<String>());
        dao.getName(name2.getName()).test().assertValue(new ArrayList<String>());
        dao.getName(name3.getName()).test().assertValue(new ArrayList<String>());
    }

    @Test
    public void testGetAllNames() {
        dao.getName(name1.getName()).test().assertValue(new ArrayList<>(Arrays.asList(name1.getName())));
        dao.getName(name2.getName()).test().assertValue(new ArrayList<>(Arrays.asList(name2.getName())));
        dao.getName(name3.getName()).test().assertValue(new ArrayList<>(Arrays.asList(name3.getName())));

        List<String> names = new ArrayList<>();
        names.add(name1.getName());
        names.add(name2.getName());
        names.add(name3.getName());

        dao.getAllName().test().assertValue(names);
    }
}