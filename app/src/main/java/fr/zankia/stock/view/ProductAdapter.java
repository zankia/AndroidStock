package fr.zankia.stock.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


public class ProductAdapter extends BaseAdapter {


    private final int resource;
    private final int nameId;
    private final int quantityId;

    private ArrayList<String> itemList;
    private HashMap<String, Integer> items;

    public ProductAdapter(int resource, int nameId, int quantityId) {
        this.resource = resource;
        this.nameId = nameId;
        this.quantityId = quantityId;
        itemList = new ArrayList<>();
        items = new HashMap<>();
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(this.resource, null);
        ((TextView)convertView.findViewById(nameId)).setText((CharSequence) getItem(position));
        ((TextView)convertView.findViewById(quantityId)).setText(String.valueOf(items.get((String)
                getItem(position))));
        return convertView;
    }

    public void add(String name, int quantity) {
        itemList.add(itemList.size(), name);
        items.put(name, quantity);
    }
}
