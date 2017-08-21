package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

        categoryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("longClick", ((TextView) view).getText().toString());
                view.setTag(R.string.type, R.string.Cat);
                updateCategory(view);
                return true;
            }
        });

        View footer = getLayoutInflater().inflate(R.layout.row_button_add, null);
        footer.setTag(R.string.type, R.string.Cat);
        categoryView.addFooterView(footer);

        categoryView.setAdapter(adapter);
    }


    public void showCategory(View view) {
        String categoryName = ((TextView) view).getText().toString();

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
                new String[]{categoryName},
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


        itemsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("longClick", ((TextView) view).getText().toString());
                view.setTag(R.string.type, R.string.Prod);
                updateCategory(view);
                return true;
            }
        });

        View footer = getLayoutInflater().inflate(R.layout.row_button_add, null);
        footer.setTag(R.string.Prod);
        footer.setTag(R.string.type, R.string.Prod);
        footer.setTag(R.string.name, categoryName);
        if (itemsView.getFooterViewsCount() > 0) {
            itemsView.removeFooterView(itemsView.findViewWithTag(R.string.Prod));
        }
        itemsView.addFooterView(footer);

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

    public void addCategory(final View view) {
        final EditText editText = new EditText(this);
        final StockDbHelper helper = new StockDbHelper(view.getContext());

        int message = 0;
        if((int) view.getTag(R.string.type) == R.string.Cat) {
            message = R.string.newCat;
        } else if((int) view.getTag(R.string.type) == R.string.Prod) {
            message = R.string.newProd;
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getReadableDatabase();
                        ContentValues values = new ContentValues();
                        String newValue = editText.getText().toString();

                        if((int) view.getTag(R.string.type) == R.string.Cat) {

                            values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, newValue);
                            db.insert(StockContract.CategoryEntry.TABLE_NAME, null, values);
                            db.close();
                            finish();
                            startActivity(getIntent());

                        } else if((int) view.getTag(R.string.type) == R.string.Prod) {

                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, newValue);
                            values.put(StockContract.ProductEntry.COLUMN_NAME_CAT,
                                    (String) view.getTag(R.string.name));
                            values.put(StockContract.ProductEntry.COLUMN_NAME_QUANTITY, 0);
                            db.insert(StockContract.ProductEntry.TABLE_NAME, null, values);
                            db.close();
                            prodAdapter.add(newValue, 0);
                            prodAdapter.notifyDataSetChanged();

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void updateCategory(final View view) {
        final EditText editText = new EditText(this);
        final StockDbHelper helper = new StockDbHelper(view.getContext());

        final String oldValue = ((TextView) view).getText().toString();
        editText.setText(oldValue);

        int message = 0;
        if((int) view.getTag(R.string.type) == R.string.Cat) {
            message = R.string.newCat;
        } else if((int) view.getTag(R.string.type) == R.string.Prod) {
            message = R.string.newProd;
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase db = helper.getReadableDatabase();
                        ContentValues values = new ContentValues();
                        String newValue = editText.getText().toString();

                        if((int) view.getTag(R.string.type) == R.string.Cat) {

                            values.put(StockContract.CategoryEntry.COLUMN_NAME_NAME, newValue);

                            if(!newValue.equals("")) {
                                ContentValues prodValues = new ContentValues();
                                prodValues.put(StockContract.ProductEntry.COLUMN_NAME_CAT, newValue);
                                db.update(StockContract.ProductEntry.TABLE_NAME, prodValues,
                                        StockContract.ProductEntry.COLUMN_NAME_CAT + " = ?",
                                        new String[] {oldValue});
                                db.update(StockContract.CategoryEntry.TABLE_NAME, values,
                                        StockContract.CategoryEntry.COLUMN_NAME_NAME + " = ?",
                                        new String[] {oldValue});
                            } else {
                                db.delete(StockContract.ProductEntry.TABLE_NAME,
                                        StockContract.ProductEntry.COLUMN_NAME_CAT + " = ?",
                                        new String[] {oldValue});
                                db.delete(StockContract.CategoryEntry.TABLE_NAME,
                                        StockContract.CategoryEntry.COLUMN_NAME_NAME + " = ?",
                                        new String[] {oldValue});
                            }

                            db.close();
                            finish();
                            startActivity(getIntent());

                        } else if((int) view.getTag(R.string.type) == R.string.Prod) {

                            values.put(StockContract.ProductEntry.COLUMN_NAME_NAME, newValue);

                            if(!newValue.equals("")) {
                                db.update(StockContract.ProductEntry.TABLE_NAME, values,
                                        StockContract.ProductEntry.COLUMN_NAME_NAME + " = ?",
                                        new String[] {oldValue});
                                prodAdapter.update(oldValue, newValue);
                            } else {
                                db.delete(StockContract.ProductEntry.TABLE_NAME,
                                        StockContract.ProductEntry.COLUMN_NAME_NAME + " = ?",
                                        new String[] {oldValue});
                                prodAdapter.remove(oldValue);
                            }

                            db.close();
                            prodAdapter.notifyDataSetChanged();

                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
