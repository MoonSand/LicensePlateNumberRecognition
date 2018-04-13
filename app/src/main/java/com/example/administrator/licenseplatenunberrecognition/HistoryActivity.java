package com.example.administrator.licenseplatenunberrecognition;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity{

    private List<Map<String,Object>> lists;
    private SimpleAdapter adapter;
    private ListView listView;
    public String[] content ={"渝C 12345","京B 12345"};
    public String[] time={"30/3/2018","29/3/2018"};
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ActionBar mActionBar = getSupportActionBar();

        mActionBar.setDisplayShowHomeEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);
        listView = (ListView) findViewById(R.id.list_view);
        lists = new ArrayList<>();
        for(int i = 0;i < content.length;i++){
            Map<String,Object> map =new HashMap<>();
            map.put("content",content[i]);
            map.put("time",time[i]);
            lists.add(map);
        }
        adapter = new SimpleAdapter(HistoryActivity.this,lists,R.layout.list_item
                ,new String[]{"content","time"}
                ,new int[]{R.id.text1,R.id.text2});
        listView.setAdapter(adapter);


    }



}
