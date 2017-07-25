package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockContract;
import fr.zankia.stock.dao.StockDbHelper;

public class ProductDisplayActivity extends Activity {

    private String categoryName;

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

        categoryName = this.getIntent().getStringExtra(this.getString(R.string.extraName));

        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(categoryName);
        setContentView(R.layout.activity_product_display);

        GridView productView = (GridView) findViewById(R.id.itemsView);
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

    public void empty(View view) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.emptyConfirm) + " " + categoryName  + " "
                        + getString(R.string.qMark))
                .setPositiveButton(R.string.emptyAction, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emptyDb();
                        finish();
                    }

                })
                .setNegativeButton(R.string.confirm, null)
                .show();
    }

    private void emptyDb() {
        StockDbHelper helper = new StockDbHelper(this.getBaseContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);


        db.update(StockContract.ProductEntry.TABLE_NAME,
                values,
                StockContract.ProductEntry.COLUMN_NAME_CAT + "= ?",
                new String[] {categoryName});

        db.close();
    }
}
