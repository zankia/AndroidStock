package fr.zankia.stock.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.GridView

import fr.zankia.stock.R
import fr.zankia.stock.dao.StockJSON

class GridActivity : Activity() {

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
        setTitle(R.string.leftButton)
        setContentView(R.layout.activity_grid)

        val categoryView = findViewById<GridView>(R.id.itemsView)
        val adapter = ArrayAdapter<String>(this, R.layout.row_button)
        adapter.addAll(StockJSON.categoryNames)
        categoryView.adapter = adapter
    }

    fun showCategory(view: View) {
        val intent = Intent(this, ProductSummaryActivity::class.java)
        intent.putExtra(this.getString(R.string.name), (view as Button).text)
        startActivity(intent)
    }
}
