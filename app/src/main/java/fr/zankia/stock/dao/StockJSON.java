package fr.zankia.stock.dao;

import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.zankia.stock.model.Category;
import fr.zankia.stock.model.Product;

public class StockJSON {
    public static final String PRODUCTS = "products";
    private static final String NAME = "name";
    public static final String QUANTITY = "quantity";
    public static final String DATA = "data";

    private List<Category> categories;
    private static StockJSON instance;
    private SharedPreferences preferences;

    private StockJSON() {
        categories = new ArrayList<>();
    }

    public static StockJSON getInstance() {
        if(instance == null) {
            instance = new StockJSON();
        }
        return instance;
    }

    public void load(String json) {
        Log.d("Load JSON", "start");
        categories = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(json);
            for(int i = 0; i < data.length(); ++i) {

                JSONObject jsonCategory = data.getJSONObject(i);
                Category category = new Category(jsonCategory.getString(NAME));
                JSONArray jsonProducts = jsonCategory.getJSONArray(PRODUCTS);

                for(int j = 0; j < jsonProducts.length(); ++j) {
                    JSONObject jsonProduct = jsonProducts.getJSONObject(j);
                    category.addProduct(new Product(
                            jsonProduct.getString(NAME),
                            jsonProduct.getInt(QUANTITY)
                    ));
                }

                categories.add(category);
                Log.d("Load JSON", "loaded " + jsonProducts.length() + " products in " +
                        category.getName());
            }
            Log.d("Load JSON", "loaded " + data.length() + " categories");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Load JSON", "done");
    }

    public void load(SharedPreferences preferences) {
        this.preferences = preferences;
        load(preferences.getString(DATA, "[]"));
    }

    public void save() {
        Log.d("Save JSON", "start");
        SharedPreferences.Editor editor = preferences.edit();
        try {
            JSONArray data = new JSONArray();
            for(Category category : categories) {
                JSONObject jsonCategory = new JSONObject();
                jsonCategory.put(NAME, category.getName());
                JSONArray jsonProducts = new JSONArray();
                for(Product product : category.getProducts()) {
                    JSONObject jsonProduct = new JSONObject();
                    jsonProduct.put(NAME, product.getName());
                    jsonProduct.put(QUANTITY, product.getQuantity());
                    jsonProducts.put(jsonProduct);
                }
                jsonCategory.put(PRODUCTS, jsonProducts);
                data.put(jsonCategory);
            }
            editor.putString(DATA, data.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();
        Log.d("Save JSON", "done");
    }

    public Category getCategory(String name) {
        for(Category c : categories) {
            if(c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    
    public List<String> getCategoryNames() {
        List<String> list = new LinkedList<>();
        for(Category c : categories) {
            list.add(c.getName());
        }
        return list;
    }

    public void addCategory(String name) {
        categories.add(new Category(name));
    }

    public void removeCategory(String name) {
        categories.remove(getCategory(name));
    }

}
