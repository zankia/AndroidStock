package fr.zankia.stock.view;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockContract;
import fr.zankia.stock.dao.StockDbHelper;

public class ListActivity extends Activity {

    private ProductAdapter prodAdapter;

    @Override
     public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
             case android.R.id.home:
                 this.finish();
                 return true;
             default:
                 return super.onOptionsItemSelected(item);
         }
     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(R.string.rightButton);
        setContentView(R.layout.activity_list);

        ListView categoryView = (ListView) findViewById(R.id.categoryView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_button);

        StockDbHelper helper = new StockDbHelper(categoryView.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(StockContract.CategoryEntry.TABLE_NAME,
                new String[]{StockContract.CategoryEntry.COLUMN_NAME_NAME},
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(StockContract.CategoryEntry.COLUMN_NAME_NAME);
            String val = cursor.getString(index);
            adapter.add(val);
        }

        cursor.close();
        db.close();

        categoryView.setAdapter(adapter);
    }


    public void showCategory(View view) {

        for(int i = 0; i < ((ListView) view.getParent()).getChildCount(); ++i) {
            ((ListView) view.getParent()).getChildAt(i).getBackground().setColorFilter
                    (0xFFD8D8D8, PorterDuff.Mode.MULTIPLY);
        }
        view.getBackground().setColorFilter(getResources().getColor(android.R.color
                .holo_blue_dark), PorterDuff.Mode.MULTIPLY);

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d("View", ((EditText) v).getText().toString());
                Log.d("Focus", String.valueOf(hasFocus));
                if(!hasFocus) {
                    String amount = ((EditText) v).getText().toString();
                    if(amount.equals("")) {
                       amount = "0";
                    }
                    silentDbUpdate(v, Integer.parseInt(amount));
                }
            }
        };

        ListView itemsView = (ListView) findViewById(R.id.itemsView);
        this.prodAdapter = new ProductAdapter(R.layout.row_item, R.id.itemName, R.id
                .itemQuantity, listener);

        StockDbHelper helper = new StockDbHelper(itemsView.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(StockContract.ProductEntry.TABLE_NAME,
                new String[]{StockContract.ProductEntry.COLUMN_NAME_NAME,
                        StockContract.ProductEntry.COLUMN_NAME_QUANTITY},
                StockContract.ProductEntry.COLUMN_NAME_CAT + " = ?",
                new String[]{String.valueOf(((TextView) view).getText())},
                null, null, null);

        while(cursor.moveToNext()) {
            int nameIndex = cursor.getColumnIndexOrThrow(StockContract.ProductEntry
                    .COLUMN_NAME_NAME);
            String prodName = cursor.getString(nameIndex);
            int quantIndex = cursor.getColumnIndexOrThrow(StockContract.ProductEntry
                    .COLUMN_NAME_QUANTITY);
            int quantity = cursor.getInt(quantIndex);
            this.prodAdapter.add(prodName, quantity);
        }

        cursor.close();
        db.close();

        itemsView.setAdapter(this.prodAdapter);
    }

    public void removeOne(View view) {
        EditText count = (EditText) ((LinearLayout)view.getParent()).getChildAt(2);
        String text = count.getText().toString();
        if(text.isEmpty()) {
            text = "1";
        }
        int value = Integer.parseInt(text);
        if(value == 0) {
            return;
        }
        dbUpdate(view, --value);
    }

    public void addOne(View view) {
        EditText count = (EditText) ((LinearLayout)view.getParent()).getChildAt(2);
        String text = count.getText().toString();
        if(text.isEmpty()) {
            text = "0";
        }
        int value = Integer.parseInt(text);
        dbUpdate(view, ++value);
    }

    private void dbUpdate(View view, int value) {
        silentDbUpdate(view, value);
        this.prodAdapter.notifyDataSetChanged();
    }

    private void silentDbUpdate(View view, int value) {
        String label = ((TextView) ((LinearLayout) view.getParent()).getChildAt(0))
                .getText().toString();
        StockDbHelper helper = new StockDbHelper(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, value);

        db.update(StockContract.ProductEntry.TABLE_NAME,
                values,
                StockContract.ProductEntry.COLUMN_NAME_NAME + "= ?",
                new String[] {label});

        db.close();

        this.prodAdapter.add(label, value);
    }
}
