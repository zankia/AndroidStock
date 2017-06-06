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
    public void setDisplayActivity(View view) {
        startActivity(new Intent(this, DisplayActivity.class));
    }

    public void resetDB(View view) {
        StockDbHelper helper = new StockDbHelper(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        helper.delete(db);
        helper.onCreate(db);

        ContentValues values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 1");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 2");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 3");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 4");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 5");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
        values = new ContentValues();
        values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 6");
        db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);

        for(int i = 1; i < 11; ++i) {
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 1-" + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 1");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 2-" + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 2");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 3-" + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 3");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 4-" + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 4");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 5-" + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 5");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
            values = new ContentValues();
            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 6-" + i);
            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 6");
            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
        }

        Log.d("DB", "DB remplie");

        Toast.makeText(getApplicationContext(), R.string.dbToast, Toast.LENGTH_SHORT).show();
    }
}
