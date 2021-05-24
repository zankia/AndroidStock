package fr.zankia.stock.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.woxthebox.draglistview.DragItemAdapter
import fr.zankia.stock.R
import fr.zankia.stock.model.Product

class DragProductAdapter(
    var resource: Int
) : DragItemAdapter<Product, MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(this.resource, parent, false)
        return MyViewHolder(view, R.id.itemGrab, false)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.itemView.findViewById<TextView>(R.id.itemName).text = itemList[position].name
    }

    override fun getUniqueItemId(position: Int) = itemList[position].hashCode().toLong()
}

class MyViewHolder(itemView: View?, handleResId: Int, dragOnLongPress: Boolean) :
    DragItemAdapter.ViewHolder(
        itemView,
        handleResId,
        dragOnLongPress
    )