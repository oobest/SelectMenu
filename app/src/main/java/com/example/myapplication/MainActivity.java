package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.albert.selectmenu.SelectMenu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView selectBridge;

    private BridgeData currentBridge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        selectBridge = findViewById(R.id.selectBridge);
        selectBridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(view);
            }
        });
    }

    private void showMenu(View view) {
        List<String> nameList = Arrays.asList("桥梁一", "桥梁二", "桥梁三", "桥梁四", "桥梁五", "桥梁六", "桥梁七", "桥梁8", "桥梁9", "桥梁10");
        List<BridgeData> bridgeData = new ArrayList<>();
        int count = 0;
        for (String name : nameList) {
            bridgeData.add(new BridgeData(count, name));
            count++;
        }
//           List<String> dataList = Arrays.asList("桥梁一", "桥梁二", "桥梁三");

        SelectMenu.create(getSupportFragmentManager(), view, bridgeData)
                .setItemTextMapper(BridgeData::getName) // 下拉菜单显示项
                .setIsSameFunction((o1, o2) -> o1.getId() == o2.getId()) // 判断o1,o2是否为同一个对象
                .setSelectedItem(currentBridge) // 默认选中项
                .show(o -> {
                    BridgeData temp = o;
                    if (temp.equals(currentBridge)) {
                        // 点击了已选中项，取消选项
                        temp = null;
                    }
                    currentBridge = temp;
                    ((TextView) view).setText(currentBridge.getName());
                });
    }
}