package com.example.wearsample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mitak_000 on 2014/08/28.
 */
public class ListActivity extends Activity {

    private WatchViewStub stub;
    ListView list;

    static List<String> dataList = new ArrayList<String>();
    static ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                list = (ListView) stub.findViewById(R.id.list);
                setAdapters();
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent,
                                            View view, int pos, long id) {
                        Intent intent = new Intent(ListActivity.this, lineActivity.class);
                        intent.setAction(Intent.ACTION_VIEW);
                        startActivity(intent);
                    }
                });
            }
        });

        for(int i=1 ; i<=10 ; i++){
            dataList.add("list" + i);
        }

    }

    protected void setAdapters(){
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                dataList);
        list.setAdapter(adapter);
    }


}
