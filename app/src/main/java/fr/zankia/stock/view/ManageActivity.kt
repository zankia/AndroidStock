package fr.zankia.stock.view

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import fr.zankia.stock.R
import fr.zankia.stock.dao.StockJSON
import fr.zankia.stock.model.Category

@SuppressLint("InflateParams")
class ManageActivity : Activity() {

    private lateinit var prodAdapter: ProductAdapter
    private lateinit var currentCategory: Category

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
        setTitle(R.string.rightButton)
        setContentView(R.layout.activity_manage)

        val categoryView = findViewById<ListView>(R.id.categoryView)
        val adapter = ArrayAdapter<String>(this, R.layout.row_button)
        adapter.addAll(StockJSON.categoryNames)

        categoryView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, view, _, _ ->
            view.setTag(R.string.type, R.string.Cat)
            update(view)
            true
        }

        val footer = layoutInflater.inflate(R.layout.row_button_add, null)
        footer.setTag(R.string.type, R.string.Cat)
        categoryView.addFooterView(footer)

        categoryView.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
        StockJSON.save()
    }

    override fun onDestroy() {
        super.onDestroy()
        StockJSON.save()
    }

    fun showCategory(view: View) {
        val categoryName = (view as TextView).text.toString()
        currentCategory = StockJSON.getCategory(categoryName)

        title = getString(R.string.rightButton) + " - " + categoryName

        val listener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                var amount = (v as EditText).text.toString()
                if (amount == "") {
                    amount = "0"
                }
                silentUpdateProductInView(v, Integer.parseInt(amount))
            }
        }

        val itemsView = findViewById<ListView>(R.id.itemsView)
        prodAdapter = ProductAdapter(R.layout.row_item, R.id.itemName, R.id.itemQuantity, listener)

        for ((name, quantity) in currentCategory.products) {
            prodAdapter.add(name, quantity)
        }

        itemsView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _, childView, _, _ ->
            childView.setTag(R.string.type, R.string.Prod)
            update(childView)
            true
        }

        val footer = layoutInflater.inflate(R.layout.row_button_add, null)
        footer.tag = R.string.Prod
        footer.setTag(R.string.type, R.string.Prod)
        if (itemsView.footerViewsCount > 0) {
            itemsView.removeFooterView(itemsView.findViewWithTag(R.string.Prod))
        }
        itemsView.addFooterView(footer)

        itemsView.adapter = prodAdapter
    }

    fun removeOne(view: View) {
        val count = (view.parent as LinearLayout).getChildAt(2) as EditText
        var text = count.text.toString()
        if (text.isEmpty()) {
            text = "1"
        }
        var value = Integer.parseInt(text)
        if (value == 0) {
            return
        }
        updateProductInView(view, --value)
    }

    fun addOne(view: View) {
        val count = (view.parent as LinearLayout).getChildAt(2) as EditText
        var text = count.text.toString()
        if (text.isEmpty()) {
            text = "0"
        }
        var value = Integer.parseInt(text)
        updateProductInView(view, ++value)
    }

    private fun updateProductInView(view: View, value: Int) {
        silentUpdateProductInView(view, value)
        this.prodAdapter.notifyDataSetChanged()
    }

    private fun silentUpdateProductInView(view: View, value: Int) {
        val label = ((view.parent as LinearLayout).getChildAt(0) as TextView)
            .text.toString()
        currentCategory.getProduct(label).quantity = value

        this.prodAdapter.add(label, value)
    }

    fun addCategory(view: View) {
        val editText = EditText(this)
        val type = view.getTag(R.string.type) as Int

        var message = 0
        if (type == R.string.Cat) {
            message = R.string.newCat
        } else if (type == R.string.Prod) {
            message = R.string.newProd
        }
        AlertDialog.Builder(this)
            .setMessage(message)
            .setView(editText)
            .setPositiveButton(R.string.confirm) { _, _ ->
                val newValue = editText.text.toString()

                if (type == R.string.Cat) {
                    StockJSON.addCategory(newValue)
                    finish()
                    startActivity(intent)
                } else if (type == R.string.Prod) {
                    currentCategory.addProduct(newValue)
                    prodAdapter.add(newValue, 0)
                    prodAdapter.notifyDataSetChanged()
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun update(view: View) {
        val type = view.getTag(R.string.type) as Int
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        val value = (view as TextView).text.toString()
        if (type == R.string.Cat) {
            updateCategory(content, value)
        } else if (type == R.string.Prod) {
            updateProduct(content, value)
        }
    }

    private fun updateCategory(content: ViewGroup, oldValue: String) {
        val nameEditText = EditText(this)

        nameEditText.setText(oldValue)
        content.addView(nameEditText)

        val message = R.string.newCat

        showAlertDialog(content, message, DialogInterface.OnClickListener { _, _ ->
            val newValue = nameEditText.text.toString()

            if (newValue == "") {
                StockJSON.removeCategory(oldValue)
            } else {
                currentCategory.name = newValue
            }

            finish()
            startActivity(intent)
        })
    }

    private fun updateProduct(content: ViewGroup, oldName: String) {
        val nameEditText = EditText(this)
        nameEditText.setText(oldName)
        val product = currentCategory.getProduct(oldName)

        val priceEditText = EditText(this)
        priceEditText.inputType = InputType.TYPE_CLASS_NUMBER
        priceEditText.setText(product.price.toString())

        content.addView(nameEditText)
        content.addView(priceEditText)

        val message = R.string.newProd
        showAlertDialog(content, message, DialogInterface.OnClickListener { _, _ ->
            val newName = nameEditText.text.toString()

            if (newName == "") {
                currentCategory.removeProduct(oldName)
                prodAdapter.remove(oldName)
            } else {
                product.name = newName
                product.price = java.lang.Float.parseFloat(priceEditText.text.toString())
                prodAdapter.update(oldName, newName)
            }
            prodAdapter.notifyDataSetChanged()
        })
    }

    private fun showAlertDialog(content: View, message: Int, positiveBtn: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setView(content)
            .setPositiveButton(R.string.confirm, positiveBtn)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
