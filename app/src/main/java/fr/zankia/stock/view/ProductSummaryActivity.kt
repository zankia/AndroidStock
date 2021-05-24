package fr.zankia.stock.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.GridView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import fr.zankia.stock.R
import fr.zankia.stock.dao.StockJSON
import fr.zankia.stock.model.Category

class ProductSummaryActivity : AppCompatActivity() {

    private lateinit var category: Category

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        category = StockJSON.getCategory(
            intent.getStringExtra(getString(R.string.name)) ?: ""
        )

        actionBar?.setDisplayHomeAsUpEnabled(true)

        title = category.name
        setContentView(R.layout.activity_product_summary)

        val productView = findViewById<GridView>(R.id.itemsView)
        val adapter = ProductAdapter(R.layout.row_simple_item, R.id.itemName, R.id.itemQuantity)

        for ((name, quantity) in category.products) {
            if (quantity != 0) {
                adapter.add(name, quantity)
            }
        }

        productView.adapter = adapter
    }

    fun empty(view: View) {
        AlertDialog.Builder(this)
            .setMessage("${getString(R.string.emptyConfirm)} ${category.name}${getString(R.string.qMark)}")
            .setPositiveButton(R.string.emptyAction) { _, _ ->
                emptyDb()
                finish()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun emptyDb() {
        for (product in category.products) {
            product.quantity = 0
        }
        StockJSON.save()
    }
}
