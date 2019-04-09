package com.example.trial1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void next(View view) {
        Intent intent=new Intent(MainActivity.this,encode.class);
        startActivity(intent);
    }

    public void decode(View view) {
Intent intent1=new Intent(MainActivity.this,decode.class);
startActivity(intent1);
    }
}
