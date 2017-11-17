package com.xcode126.chatplan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xcode26.chatplanlibrary.widght.ChatPlanView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements ChatPlanView.OnClickSendListener {

    private ListView listView;
    private ArrayList<Map<String, String>> list;
    private SimpleAdapter adapter;


    private ChatPlanView chatPlanView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //数据展示
        listView = findViewById(R.id.listview);
        list = new ArrayList<>();
        adapter = new SimpleAdapter(this, list, R.layout.list_item, new String[]{"key"}, new int[]{R.id.name});
        listView.setAdapter(adapter);

        //面板使用
        chatPlanView = findViewById(R.id.chat_bottom_palan);
        chatPlanView.setClickSendListener(this);
    }

    @Override
    public void OnClickSend(String messageContent, int flag) {
        //添加新数据
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("key", messageContent);
        list.add(hashMap);
        //刷新数据，并使列表展示最新数据
        adapter.notifyDataSetChanged();
        listView.setSelection(list.size() - 1);
    }
}
