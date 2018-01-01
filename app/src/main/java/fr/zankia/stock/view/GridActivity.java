package fr.zankia.stock.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockJSON;

public class GridActivity extends Activity {

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
        this.setTitle(R.string.leftButton);
        setContentView(R.layout.activity_grid);

        GridView categoryView = (GridView) findViewById(R.id.itemsView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_button);
        adapter.addAll(StockJSON.getInstance().getCategoryNames());
        categoryView.setAdapter(adapter);
    }

    public void showCategory(View view) {
        CharSequence name = ((Button) view).getText();
        Intent intent = new Intent(this, ProductSummaryActivity.class);
        intent.putExtra(this.getString(R.string.name), name);

        this.startActivity(intent);
    }
}
