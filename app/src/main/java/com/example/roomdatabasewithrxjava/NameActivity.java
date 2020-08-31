package com.example.roomdatabasewithrxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class NameActivity extends AppCompatActivity {

    private EditText etName;
    public static final String KEY = "com.example.roomdatabasewithrxjava.NAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name);

        etName = findViewById(R.id.etName);
    }

    public void onDoneClicked(View view) {
        Intent resultIntent = new Intent();

        if (etName.getText() != null && !etName.getText().toString().matches("")) {
            resultIntent.putExtra(KEY, etName.getText().toString());
            setResult(Activity.RESULT_OK, resultIntent);
        } else {
            setResult(Activity.RESULT_CANCELED, resultIntent);
        }
        finish();
    }
}