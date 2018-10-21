package fr.zankia.stock.model

import java.util.ArrayList

data class Category(
    var name: String = "",
    val products: ArrayList<Product> = ArrayList()
) {

    fun getProduct(name: String): Product {
        for (p in products) {
            if (p.name == name) {
                return p
            }
        }
        return Product(name)
    }

    fun addProduct(name: String) = products.add(Product(name))


    fun removeProduct(name: String) = products.remove(getProduct(name))
}
