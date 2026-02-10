package com.example.finallyy

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SavedProductsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SavedProductsAdapter
    private lateinit var deleteBtn: Button
    private val savedProductsList = mutableListOf<SavedProduct>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_products)

        deleteBtn = findViewById(R.id.btnDeleteSelected)
        recyclerView = findViewById(R.id.recyclerViewSaved)

        adapter = SavedProductsAdapter(savedProductsList) {
            deleteBtn.visibility =
                if (adapter.hasSelection()) View.VISIBLE else View.GONE
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        deleteBtn.setOnClickListener {
            confirmDelete()
        }

        fetchSavedProducts()
    }

    private fun fetchSavedProducts() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("savedProducts")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                savedProductsList.clear()
                for (child in snapshot.children) {
                    val product = child.getValue(SavedProduct::class.java)
                    product?.key = child.key
                    if (product != null) {
                        savedProductsList.add(product)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Products")
            .setMessage("Delete selected products?")
            .setPositiveButton("Delete") { _, _ ->
                deleteSelected()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSelected() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("savedProducts")

        adapter.getSelectedItems().forEach {
            it.key?.let { key ->
                ref.child(key).removeValue()
            }
        }

        adapter.clearSelection()
        Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
    }
}
