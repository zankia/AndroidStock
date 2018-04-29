package fr.zankia.stock.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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

        ListView categoryView = findViewById(R.id.categoryView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.row_button);
        adapter.addAll(StockJSON.getInstance().getCategoryNames());

        categoryView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setTag(R.string.type, R.string.Cat);
                update(view);
                return true;
            }
        });

        View footer = getLayoutInflater().inflate(R.layout.row_button_add, null);
        footer.setTag(R.string.type, R.string.Cat);
        categoryView.addFooterView(footer);

        categoryView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StockJSON.getInstance().save();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StockJSON.getInstance().save();
    }

    public void showCategory(View view) {
        String categoryName = ((TextView) view).getText().toString();
        currentCategory = StockJSON.getInstance().getCategory(categoryName);

        setTitle(getString(R.string.rightButton) + " - " + categoryName);

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String amount = ((EditText) v).getText().toString();
                    if(amount.equals("")) {
                       amount = "0";
                    }
                    silentUpdateProductInView(v, Integer.parseInt(amount));
                }
            }
        };

        ListView itemsView = findViewById(R.id.itemsView);
        this.prodAdapter = new ProductAdapter(R.layout.row_item, R.id.itemName, R.id
                .itemQuantity, listener);

        for(Product product : currentCategory.getProducts()) {
            prodAdapter.add(product.getName(), product.getQuantity());
        }

        itemsView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                view.setTag(R.string.type, R.string.Prod);
                update(view);
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

                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    public void update(final View view) {
        int type = (int) view.getTag(R.string.type);
        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        String value = ((TextView) view).getText().toString();
        if(type == R.string.Cat) {
            updateCategory(content, value);
        } else if(type == R.string.Prod) {
            updateProduct(content, value);
        }
    }


    private void updateCategory(ViewGroup content, final String oldValue) {
        final EditText nameEditText = new EditText(this);

        nameEditText.setText(oldValue);
        content.addView(nameEditText);

        int message = R.string.newCat;

        showAlertDialog(content, message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newValue = nameEditText.getText().toString();

                        if(newValue.equals("")) {
                            StockJSON.getInstance().removeCategory(oldValue);
                        } else {
                            currentCategory.setName(newValue);
                        }

                        finish();
                        startActivity(getIntent());
                    }
                });
    }

    private void updateProduct(ViewGroup content, final String oldName) {
        final EditText nameEditText = new EditText(this);
        nameEditText.setText(oldName);
        final Product product = currentCategory.getProduct(oldName);

        final EditText priceEditText = new EditText(this);
        priceEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        priceEditText.setText((String.valueOf(product.getPrice())));

        content.addView(nameEditText);
        content.addView(priceEditText);

        int message = R.string.newProd;
        showAlertDialog(content, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = nameEditText.getText().toString();

                if(newName.equals("")) {
                    currentCategory.removeProduct(oldName);
                    prodAdapter.remove(oldName);
                } else {
                    product.setName(newName);
                    product.setPrice(Float.parseFloat(priceEditText.getText().toString()));
                    prodAdapter.update(oldName, newName);
                }
                prodAdapter.notifyDataSetChanged();

            }
        });
    }

    private void showAlertDialog(View content, int message, DialogInterface.OnClickListener positiveBtn) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setView(content)
                .setPositiveButton(R.string.confirm, positiveBtn)
                .setNegativeButton(R.string.cancel, null)
                .show();
    }
}
