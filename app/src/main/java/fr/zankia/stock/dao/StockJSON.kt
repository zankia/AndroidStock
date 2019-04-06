package fr.zankia.stock.dao

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import fr.zankia.stock.model.Category
import java.util.ArrayList
import java.util.LinkedList

object StockJSON : ValueEventListener {
    private var categories: ArrayList<Category> = ArrayList()
    private lateinit var dbReference: DatabaseReference

    val categoryNames: List<String>
        get() {
            val list = LinkedList<String>()
            for (c in categories) {
                list.add(c.name)
            }
            return list
        }

    init {
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    fun selectNode(currentUser: FirebaseUser) {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        dbReference = firebaseDatabase.getReference(currentUser.uid)
        dbReference.addValueEventListener(this)
        dbReference.keepSynced(true)
    }

    fun save() {
        dbReference.push()
        dbReference.setValue(categories)
    }

    fun getCategory(name: String): Category {
        for (c in categories) {
            if (c.name == name) {
                return c
            }
        }
        return Category(name)
    }

    fun addCategory(name: String) {
        categories.add(Category(name))
    }

    fun removeCategory(name: String) {
        categories.remove(getCategory(name))
    }

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        categories = ArrayList()
        for (postSnapshot in dataSnapshot.children) {
            val category = postSnapshot.getValue(Category::class.java)
            if (category != null) {
                categories.add(category)
            }
        }
    }

    override fun onCancelled(databaseError: DatabaseError) {
        //Nothing to do
    }
}
