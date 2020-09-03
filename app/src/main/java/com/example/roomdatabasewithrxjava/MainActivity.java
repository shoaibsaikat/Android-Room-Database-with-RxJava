package com.example.roomdatabasewithrxjava;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.CompletableObserver;
import io.reactivex.FlowableSubscriber;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.example.roomdatabasewithrxjava.NameDatabase.DB_NAME;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG  = "CUSTOM_LOG";

    private RecyclerView recyclerView;
    private NameAdapter adapter;
    private int REQUEST_CODE = 1;
    private ExecutorService executorService;

    private List<String> names;

    private NameDatabase db;
    private NameDao dao;

    private DbUpdateObserver updateObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        names = new ArrayList<>();
        executorService = new ThreadPoolExecutor(4, 5, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>());

        db = Room.databaseBuilder(this, NameDatabase.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .build();
        dao = db.nameDao();

        adapter = new NameAdapter();
        recyclerView.setAdapter(adapter);

        updateObserver = new DbUpdateObserver(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dao.getAllName()
                .subscribeOn(Schedulers.from(executorService))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(updateObserver);
    }

    public void onAddClicked(View view) {
        Intent intent = new Intent(this, NameActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                final String name = data.getStringExtra(NameActivity.KEY).trim();
                dao.insert(new Name(name))
                        .subscribeOn(Schedulers.from(executorService))
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new DbInsertCompleteObserver(this, name));

            } else {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DbInsertCompleteObserver implements CompletableObserver {

        private Context context;
        private String name;

        DbInsertCompleteObserver(Context context, String name) {
            this.context = context;
            this.name = name;
        }

        @Override
        public void onSubscribe(Disposable d) { }

        @Override
        public void onComplete() {
            names.add(name);
            adapter.setNames(names);
        }

        @Override
        public void onError(Throwable e) {
            Log.d(LOG_TAG, "onError");
            Toast.makeText(context, "Duplicate Name", Toast.LENGTH_SHORT).show();
        }
    }

    private class DbUpdateObserver implements FlowableSubscriber<List<String>> {

        private NameAdapter adapter;

        DbUpdateObserver(NameAdapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onSubscribe(Subscription s) {
            Log.d(LOG_TAG, "onSubscribe");
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(List<String> names) {
            Log.d(LOG_TAG, names.size() + "");
            adapter.setNames(names);
        }

        @Override
        public void onError(Throwable t) {
            Log.d(LOG_TAG, "onError");
        }

        @Override
        public void onComplete() {
            Log.d(LOG_TAG, "onComplete");
        }
    }
}