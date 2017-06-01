package fr.zankia.stock;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class ListActivity extends Activity {

    private static final HashMap<String, ArrayList<String>> map;
    static {
        map = new HashMap<>();
        ArrayList<String> arr = new ArrayList<>();
        for(int i = 1; i < 10; ++i) {
            arr.add("Fruit " + i);
        }
        map.put("Fruits", arr);
        arr = new ArrayList<>();
        for(int i = 1; i < 15; ++i) {
            arr.add("Légume " + i);
        }
        map.put("Légumes", arr);
        arr = new ArrayList<>();
        for(int i = 1; i < 6; ++i) {
            arr.add("Viande " + i);
        }
        map.put("Viandes", arr);
        arr = new ArrayList<>();
        for(int i = 1; i < 3; ++i) {
            arr.add("Condiment " + i);
        }
        map.put("Condiments", arr);
        arr = new ArrayList<>();
        for(int i = 1; i < 4; ++i) {
            arr.add("Poisson " + i);
        }
        map.put("Poissons", arr);
        arr = new ArrayList<>();
        for(int i = 1; i < 2; ++i) {
            arr.add("Plat " + i);
        }
        map.put("Plats", arr);
        arr = new ArrayList<>();
        for(int i = 1; i < 5; ++i) {
            arr.add("Sauce " + i);
        }
        map.put("Sauces", arr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_list);

        ListView categoryView = (ListView) findViewById(R.id.categoryView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_button);
        adapter.addAll(map.keySet());
        categoryView.setAdapter(adapter);
    }


    public void showCategory(View view) {
        CharSequence name = ((Button) view).getText();
        ListView itemsView = (ListView) findViewById(R.id.itemsView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_item, R.id.itemName);
        adapter.addAll(map.get(name));
        itemsView.setAdapter(adapter);
    }

    public void removeOne(View view) {
        EditText count = (EditText) ((LinearLayout)view.getParent()).getChildAt(2);
        String text = count.getText().toString();
        if(text.isEmpty()) {
            text = "1";
        }
        int value = Integer.parseInt(text);
        if(value == 0 ) {
            return;
        }
        count.setText(String.valueOf(--value));
    }

    public void addOne(View view) {
        EditText count = (EditText) ((LinearLayout)view.getParent()).getChildAt(2);
        String text = count.getText().toString();
        if(text.isEmpty()) {
            text = "0";
        }
        int value = Integer.parseInt(text);
        count.setText(String.valueOf(++value));
    }
}
