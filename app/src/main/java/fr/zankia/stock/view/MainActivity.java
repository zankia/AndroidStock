package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
        final StockDbHelper helper = new StockDbHelper(view.getContext());
        final EditText editText = new EditText(this);

        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.dbWarning) + " " +
                        getString(R.string.validityCheck))
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(editText.getText().toString().equals(getString(R.string.validityCheck))) {
                            SQLiteDatabase db = helper.getReadableDatabase();
                            helper.delete(db);
                            helper.onCreate(db);
                            ContentValues values = new ContentValues();
                            values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 1");
                            db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
                            values = new ContentValues();
                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 1-1");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 1");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
                            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
                            values = new ContentValues();
                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 1-2");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 1");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
                            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
                            values = new ContentValues();
                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 1-3");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 1");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
                            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
                            values = new ContentValues();
                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 1-4");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 1");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
                            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
                            values = new ContentValues();
                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 1-5");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 1");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
                            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
                            values = new ContentValues();
                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, "Référence 1-6");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT, "Fournisseur 1");
                            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
                            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
                            values = new ContentValues();
                            values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, "Fournisseur 2");
                            db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
                            db.close();

                            Log.d("DB", "DB remplie");

                            Toast.makeText(getApplicationContext(), R.string.dbToast, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
