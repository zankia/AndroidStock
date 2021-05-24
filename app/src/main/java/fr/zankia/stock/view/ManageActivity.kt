package fr.zankia.stock.view

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import com.woxthebox.draglistview.DragListView
import fr.zankia.stock.R
import fr.zankia.stock.dao.StockJSON
import fr.zankia.stock.model.Category

class ManageActivity : AppCompatActivity() {

    private var prodAdapter: ProductAdapter = ProductAdapter(R.layout.row_item, R.id.itemName, 0)
    private var currentCategory: Category = Category()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.manage_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.open_rearrange -> {
            rearrange()
            true
        }
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    @SuppressLint("InflateParams")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (actionBar != null) {
            actionBar!!.setDisplayHomeAsUpEnabled(true)
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

    override fun onResume() {
        super.onResume()
        showCategory(currentCategory.name)
    }

    fun showCategory(view: View) {
        showCategory((view as TextView).text.toString())
    }

    @SuppressLint("InflateParams")
    private fun showCategory(categoryName: String) {
        if (categoryName.isEmpty()) return

        currentCategory = StockJSON.getCategory(categoryName)

        title = getString(R.string.rightButton) + " - " + categoryName

        val viewSwitcher = findViewById<ViewSwitcher>(R.id.itemsViewSwitcher)
        if (viewSwitcher.nextView.findViewById<ListView>(R.id.itemsListView) != null) {
            viewSwitcher.showNext()
        }

        val listener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                var amount = (v as EditText).text.toString()
                if (amount == "") {
                    amount = "0"
                }
                silentUpdateProductInView(v, Integer.parseInt(amount))
            }
        }

        val itemsView = findViewById<ListView>(R.id.itemsListView)
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

        val dragListView = findViewById<DragListView>(R.id.itemsDragListView)
        val adapter = DragProductAdapter(R.layout.row_grab)
        adapter.itemList = currentCategory.products
        dragListView.isVerticalScrollBarEnabled = true
        dragListView.setLayoutManager(LinearLayoutManager(this))
        dragListView.setAdapter(adapter, true)
        dragListView.setCanDragHorizontally(false)
    }

    private fun rearrange() = findViewById<ViewSwitcher>(R.id.itemsViewSwitcher).showNext()

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
        prodAdapter.notifyDataSetChanged()
    }

    private fun silentUpdateProductInView(view: View, value: Int) {
        val label = ((view.parent as LinearLayout).getChildAt(0) as TextView)
            .text.toString()
        currentCategory.getProduct(label).quantity = value

        prodAdapter.add(label, value)
    }

    fun update(view: View) {
        val type = view.getTag(R.string.type) as Int
        val content = LinearLayout(this)
        content.orientation = LinearLayout.VERTICAL
        val value = when (view) {
            is TextView -> view.text.toString()
            else -> ""
        }
        when (type) {
            R.string.Cat -> updateCategory(content, value)
            R.string.Prod -> updateProduct(content, value)
            else -> throw IllegalStateException("Type $type not found")
        }
    }

    private fun updateCategory(content: ViewGroup, oldValue: String) {
        val nameEditText = addEditText(content, R.string.Name, oldValue)

        showAlertDialog(content, R.string.newCat) { _, _ ->
            val newValue = nameEditText.text.toString()

            when {
                newValue.isBlank() -> StockJSON.removeCategory(oldValue)
                oldValue.isBlank() -> StockJSON.addCategory(newValue)
                else -> StockJSON.getCategory(oldValue).name = newValue
            }

            finish()
            startActivity(intent)
        }
    }


    private fun updateProduct(content: ViewGroup, oldName: String) {
        val nameEditText = addEditText(content, R.string.Name, oldName)
        val product = currentCategory.getProduct(oldName)

        val priceEditText = addEditText(content, R.string.Price, product.price.toString())
        if (product.price == 0F) priceEditText.setText("")
        priceEditText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        showAlertDialog(content, R.string.newProd) { _, _ ->
            val newName = nameEditText.text.toString()
            val newPrice = priceEditText.text.toString()

            when {
                newName.isBlank() -> {
                    currentCategory.removeProduct(oldName)
                    prodAdapter.remove(oldName)
                }
                oldName.isBlank() -> {
                    currentCategory.addProduct(newName)
                    prodAdapter.add(newName, 0)
                }
                else -> {
                    product.name = newName
                    product.price = if (newPrice.isNotBlank()) newPrice.toFloat() else 0F
                    prodAdapter.update(oldName, newName)
                }
            }
            prodAdapter.notifyDataSetChanged()
        }
    }

    private fun addEditText(content: ViewGroup, label: Int, value: String): EditText {
        val editText = EditText(this)
        editText.setText(value)
        editText.setHint(label)
        content.addView(editText)
        return editText
    }

    private fun showAlertDialog(content: View, message: Int, positiveBtn: DialogInterface.OnClickListener) {
        AlertDialog.Builder(ContextThemeWrapper(this, R.style.AlertDialogTheme))
            .setMessage(message)
            .setView(content)
            .setPositiveButton(R.string.confirm, positiveBtn)
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
