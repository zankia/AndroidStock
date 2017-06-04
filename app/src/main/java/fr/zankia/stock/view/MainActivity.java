package fr.zankia.stock.view;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockContract;
import fr.zankia.stock.dao.StockDbHelper;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void setListActivity(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    public void resetDB(View view) {
        StockDbHelper helper = new StockDbHelper(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        helper.delete(db);
        helper.onCreate(db);

        ContentValues values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Poissons");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Viandes");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fruits");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Légumes");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Plats");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Sauces");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);

        for(int i = 1; i < 11; ++i) {
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Poisson " + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Poissons");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Viande " + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Viandes");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Fruit " + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fruits");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Légume " + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Légumes");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Plat " + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Plats");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Sauce " + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Sauces");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
        }

        Log.d("DB", "DB remplie");

        Toast.makeText(getApplicationContext(), R.string.dbToast, Toast.LENGTH_SHORT).show();
    }
}
