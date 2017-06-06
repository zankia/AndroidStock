package fr.zankia.stock.view;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockContract;
import fr.zankia.stock.dao.StockDbHelper;

public class DisplayActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        GridView categoryView = (GridView) findViewById(R.id.itemsView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.category_button);

        StockDbHelper helper = new StockDbHelper(categoryView.getContext());
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(StockContract.CategoryEntry.TABLE_NAME,
                new String[]{StockContract.CategoryEntry.COLUMN_NAME_NAME},
                null, null, null, null, null);

        while(cursor.moveToNext()) {
            int index = cursor.getColumnIndexOrThrow(StockContract.CategoryEntry.COLUMN_NAME_NAME);
            String name = cursor.getString(index);
            adapter.add(name);
        }

        cursor.close();

        categoryView.setAdapter(adapter);
    }

    public void showCategory(View view) {
        CharSequence name = ((Button) view).getText();
        Intent intent = new Intent(this, ProductDisplayActivity.class);
        intent.putExtra("name", name);

        this.startActivity(intent);
    }
}
