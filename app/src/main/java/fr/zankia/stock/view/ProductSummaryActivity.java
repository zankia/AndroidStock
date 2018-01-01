package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;

import fr.zankia.stock.R;
import fr.zankia.stock.dao.StockJSON;
import fr.zankia.stock.model.Category;
import fr.zankia.stock.model.Product;

public class ProductSummaryActivity extends Activity {

    private Category category;

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

        category = StockJSON.getInstance().getCategory(
                this.getIntent().getStringExtra(this.getString(R.string.name))
        );

        this.getActionBar().setDisplayHomeAsUpEnabled(true);
        this.setTitle(category.getName());
        setContentView(R.layout.activity_product_summary);

        GridView productView = (GridView) findViewById(R.id.itemsView);
        ProductAdapter adapter = new ProductAdapter(R.layout.row_simple_item, R.id.itemName,
                R.id.itemQuantity);

        for(Product product : category.getProducts()) {
            if(product.getQuantity() != 0) {
                adapter.add(product.getName(), product.getQuantity());
            }
        }

        productView.setAdapter(adapter);
    }

    public void empty(View view) {
        new AlertDialog.Builder(this)
                .setMessage(getString(R.string.emptyConfirm) + " " + category.getName()  + " "
                        + getString(R.string.qMark))
                .setPositiveButton(R.string.emptyAction, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emptyDb();
                        finish();
                    }

                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void emptyDb() {
        for(Product product : category.getProducts()) {
            product.setQuantity(0);
        }
        StockJSON.getInstance().save();
    }
}
