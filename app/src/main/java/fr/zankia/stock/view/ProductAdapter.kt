package fr.zankia.stock.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

import java.util.ArrayList
import java.util.HashMap

class ProductAdapter(
    private val resource: Int,
    private val nameId: Int,
    private val quantityId: Int,
    private val listener: View.OnFocusChangeListener? = null
) : BaseAdapter() {

    private val itemList: ArrayList<String> = ArrayList()
    private val items: HashMap<String, Int?> = HashMap()

    override fun getCount() = itemList.size

    override fun getItem(position: Int) = itemList[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = if (convertView == null) {
            val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
                as LayoutInflater
            inflater.inflate(this.resource, null)
        } else {
            convertView
        }

        view.findViewById<TextView>(nameId).text = getItem(position)
        view.findViewById<TextView>(quantityId).text = items[getItem(position)].toString()

        if (listener != null) {
            view.findViewById<View>(quantityId).onFocusChangeListener = listener
        }

        return view
    }

    fun add(name: String, quantity: Int) {
        if (!itemList.contains(name)) {
            itemList.add(itemList.size, name)
        }
        items[name] = quantity
    }

    fun update(oldName: String, newName: String) {
        if (oldName == newName) {
            return
        }
        itemList[itemList.indexOf(oldName)] = newName
        items[newName] = items[oldName]
        items.remove(oldName)
    }

    fun remove(name: String) {
        itemList.remove(name)
        items.remove(name)
    }
}
