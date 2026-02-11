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

class OtherSavedFoodsActivity : AppCompatActivity() {

    private lateinit var adapter: ManualFoodAdapter
    private lateinit var deleteBtn: Button
    private val foodList = mutableListOf<ManualFood>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_saved_foods)

        val recycler = findViewById<RecyclerView>(R.id.recyclerManualFoods)
        deleteBtn = findViewById(R.id.btnDeleteManual)

        adapter = ManualFoodAdapter(foodList) {
            deleteBtn.visibility =
                if (adapter.hasSelection()) View.VISIBLE else View.GONE
        }

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        deleteBtn.setOnClickListener { confirmDelete() }

        fetchManualFoods()
    }

    private fun fetchManualFoods() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val date = java.text.SimpleDateFormat("yyyy-MM-dd",
            java.util.Locale.getDefault()).format(java.util.Date())

        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("dailyLogs")
            .child(date)
            .child("foods")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodList.clear()
                for (child in snapshot.children) {
                    val food = child.getValue(ManualFood::class.java)
                    food?.key = child.key
                    if (food != null) foodList.add(food)
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle("Delete Foods")
            .setMessage("Delete selected foods?")
            .setPositiveButton("Delete") { _, _ -> deleteSelected() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteSelected() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        val date = java.text.SimpleDateFormat("yyyy-MM-dd",
            java.util.Locale.getDefault()).format(java.util.Date())

        val ref = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)
            .child("dailyLogs")
            .child(date)
            .child("foods")

        adapter.getSelectedItems().forEach {
            it.key?.let { key ->
                ref.child(key).removeValue()
            }
        }

        adapter.clearSelection()
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
    }
}
