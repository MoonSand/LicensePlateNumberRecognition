package com.example.administrator.licenseplatenunberrecognition;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "/LPNR/";
        File destDir = new File(path);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        destDir=new File(path+"picture/");
        if (!destDir.exists()) {
            destDir.mkdirs();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnClick_start(View view) {
        Intent intent =new Intent(MainActivity.this,ScanActivity.class);
        startActivity(intent);


    }

    public void btnClick_history(View view) {

        Intent intent =new Intent(MainActivity.this,HistoryActivity.class);
        startActivity(intent);
    }
}
