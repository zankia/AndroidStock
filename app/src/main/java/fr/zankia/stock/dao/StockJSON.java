package fr.zankia.stock.dao;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.zankia.stock.model.Category;

public class StockJSON implements ValueEventListener {
    private static StockJSON instance;
    private List<Category> categories;
    private DatabaseReference dbReference;

    private StockJSON() {
        categories = new ArrayList<>();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
    }

    public static StockJSON getInstance() {
        if (instance == null) {
            instance = new StockJSON();
        }
        return instance;
    }

    public void selectNode(FirebaseUser currentUser) {
        if (currentUser.getEmail() == null) {
            return;
        }
        String path = currentUser.getEmail().substring(0, currentUser.getEmail().indexOf('@'));
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        dbReference = firebaseDatabase.getReference(path);
        dbReference.addValueEventListener(this);
        dbReference.keepSynced(true);
    }

    public void save() {
        dbReference.push();
        dbReference.setValue(categories);
    }

    public Category getCategory(String name) {
        for (Category c : categories) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
    }
    
    public List<String> getCategoryNames() {
        List<String> list = new LinkedList<>();
        for (Category c : categories) {
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

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        categories = new ArrayList<>();
        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            Category category = postSnapshot.getValue(Category.class);
            categories.add(category);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) { }
}
