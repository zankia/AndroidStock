package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import fr.zankia.stock.dao.StockJSON;
import fr.zankia.stock.model.Category;
import fr.zankia.stock.model.Product;

public class ManageActivity extends Activity {

    private ProductAdapter prodAdapter;
    private Category currentCategory;

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
        setContentView(R.layout.activity_manage);

        ListView categoryView = (ListView) findViewById(R.id.categoryView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_button);
        adapter.addAll(StockJSON.getInstance().getCategoryNames());

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
        currentCategory = StockJSON.getInstance().getCategory(categoryName);

        setTitle(getString(R.string.rightButton) + " - " + categoryName);

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
                    silentUpdateProductInView(v, Integer.parseInt(amount));
                }
            }
        };

        ListView itemsView = (ListView) findViewById(R.id.itemsView);
        this.prodAdapter = new ProductAdapter(R.layout.row_item, R.id.itemName, R.id
                .itemQuantity, listener);

        for(Product product : currentCategory.getProducts()) {
            prodAdapter.add(product.getName(), product.getQuantity());
        }

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
        updateProductInView(view, --value);
    }

    public void addOne(View view) {
        EditText count = (EditText) ((LinearLayout)view.getParent()).getChildAt(2);
        String text = count.getText().toString();
        if(text.isEmpty()) {
            text = "0";
        }
        int value = Integer.parseInt(text);
        updateProductInView(view, ++value);
    }

    private void updateProductInView(View view, int value) {
        silentUpdateProductInView(view, value);
        this.prodAdapter.notifyDataSetChanged();
    }

    private void silentUpdateProductInView(View view, int value) {
        String label = ((TextView) ((LinearLayout) view.getParent()).getChildAt(0))
                .getText().toString();
        currentCategory.getProduct(label).setQuantity(value);
        StockJSON.getInstance().save();

        this.prodAdapter.add(label, value);
    }

    public void addCategory(final View view) {
        final EditText editText = new EditText(this);
        final int type = (int) view.getTag(R.string.type);

        int message = 0;
        if(type == R.string.Cat) {
            message = R.string.newCat;
        } else if(type == R.string.Prod) {
            message = R.string.newProd;
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = editText.getText().toString();

                        if(type == R.string.Cat) {
                            StockJSON.getInstance().addCategory(newValue);
                            finish();
                            startActivity(getIntent());

                        } else if(type == R.string.Prod) {
                            currentCategory.addProduct(newValue);
                            prodAdapter.add(newValue, 0);
                            prodAdapter.notifyDataSetChanged();

                        }

                        StockJSON.getInstance().save();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void updateCategory(final View view) {
        final EditText editText = new EditText(this);
        final int type = (int) view.getTag(R.string.type);

        final String oldValue = ((TextView) view).getText().toString();
        editText.setText(oldValue);

        int message = 0;
        if(type == R.string.Cat) {
            message = R.string.newCat;
        } else if(type == R.string.Prod) {
            message = R.string.newProd;
        }
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setView(editText)
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = editText.getText().toString();

                        if(type == R.string.Cat) {

                            if(newValue.equals("")) {
                                StockJSON.getInstance().removeCategory(oldValue);
                            } else {
                                currentCategory.setName(newValue);
                            }

                            finish();
                            startActivity(getIntent());

                        } else if(type == R.string.Prod) {

                            if(newValue.equals("")) {
                                currentCategory.removeProduct(oldValue);
                                prodAdapter.remove(oldValue);
                            } else {
                                currentCategory.getProduct(oldValue).setName(newValue);
                                prodAdapter.update(oldValue, newValue);
                            }
                            prodAdapter.notifyDataSetChanged();

                        }

                        StockJSON.getInstance().save();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
