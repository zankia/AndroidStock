package fr.zankia.stock.view;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockContract;
import fr.zankia.stock.dao.StockDbHelper;

public class ProductDisplayActivity extends Activity {

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

        String categoryName = this.getIntent().getStringExtra(this.getString(R.string.extraName));

        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(categoryName);
        setContentView(R.layout.activity_product_display);

        ListView productView = (ListView) findViewById(R.id.itemsView);
        ProductAdapter adapter = new ProductAdapter(R.layout.row_simple_item, R.id.itemName,
                R.id.itemQuantity);


        StockDbHelper helper = new StockDbHelper(productView.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(StockContract.ProductEntry.TABLE_NAME,
                new String[]{StockContract.ProductEntry.COLUMN_NAME_NAME,
                        StockContract.ProductEntry.COLUMN_NAME_QUANTITY},
                StockContract.ProductEntry.COLUMN_NAME_QUANTITY + " != 0 AND "
                        + StockContract.ProductEntry.COLUMN_NAME_CAT + " = ?",
                new String[]{categoryName}, null, null, null);

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
        db.close();

        productView.setAdapter(adapter);
    }
}
