package fr.zankia.stock.view;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockContract;
import fr.zankia.stock.dao.StockDbHelper;

public class ListActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
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

        categoryView.setAdapter(adapter);
    }


    public void showCategory(View view) {
        CharSequence name = ((Button) view).getText();
        ListView itemsView = (ListView) findViewById(R.id.itemsView);
        ProductAdapter adapter = new ProductAdapter(R.layout.row_item, R.id.itemName,
                R.id.itemQuantity);

        StockDbHelper helper = new StockDbHelper(itemsView.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(StockContract.ProductEntry.TABLE_NAME,
                new String[]{StockContract.ProductEntry.COLUMN_NAME_NAME,
                        StockContract.ProductEntry.COLUMN_NAME_QUANTITY},
                StockContract.ProductEntry.COLUMN_NAME_CAT + " = ?",
                new String[]{String.valueOf(name)},
                null, null, null);

        while(cursor.moveToNext()) {
            int nameIndex = cursor.getColumnIndexOrThrow(StockContract.ProductEntry
                    .COLUMN_NAME_NAME);
            String prodName = cursor.getString(nameIndex);
            int quantIndex = cursor.getColumnIndexOrThrow(StockContract.ProductEntry
                    .COLUMN_NAME_QUANTITY);
            int quantity = cursor.getInt(quantIndex);
            adapter.add(prodName, quantity);
        }

        cursor.close();

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

        dbUpdate(view, value);
    }

    public void addOne(View view) {
        EditText count = (EditText) ((LinearLayout)view.getParent()).getChildAt(2);
        String text = count.getText().toString();
        if(text.isEmpty()) {
            text = "0";
        }
        int value = Integer.parseInt(text);
        count.setText(String.valueOf(++value));

        dbUpdate(view, value);
    }

    private void dbUpdate(View view, int value) {
        StockDbHelper helper = new StockDbHelper(view.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, value);

        db.update(StockContract.ProductEntry.TABLE_NAME,
                values,
                StockContract.ProductEntry.COLUMN_NAME_NAME + "= ?",
                new String[] {(String) ((TextView)((LinearLayout)view.getParent()).getChildAt(0))
                        .getText()});
    }
}
