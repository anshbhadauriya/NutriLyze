package com.example.finallyy

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity2 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference





        val firstName = findViewById<EditText>(R.id.firstName)
        val backImage = findViewById<ImageView>(R.id.myImageView)
        val registerButton = findViewById<Button>(R.id.Move)
        val email = findViewById<EditText>(R.id.email)
        val password = findViewById<EditText>(R.id.password)
        val confirmPassword = findViewById<EditText>(R.id.confirmPassword)


        backImage.setOnClickListener {
            finish()
        }



        registerButton.setOnClickListener() {
            val emailText = email.text.toString()
            val passwordText = password.text.toString()
            val confirmPasswordText = confirmPassword.text.toString()
            val firstNameText = firstName.text.toString()

            if (emailText.isEmpty() || passwordText.isEmpty() || confirmPasswordText.isEmpty() || firstName.text.isEmpty()) {

                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show()
            }
            else if (passwordText != confirmPasswordText) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            }
            else {
                auth.createUserWithEmailAndPassword(emailText,passwordText)
                    .addOnCompleteListener(this){ task->
                        if (task.isSuccessful) {


                            val user = auth.currentUser
                            val data = mapOf("first_name" to firstNameText,
                                           "email" to emailText)
                            db.child("Details").push().setValue(data)

                            user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    savename(firstNameText,emailText)
                                    Toast.makeText(this, "Registration successful. Verification email sent to ${user.email}. Please verify your email before login.", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, Turtorial1Activity::class.java)
                                    intent.putExtra("firstName", firstName.text.toString())
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Failed to send verification email: ${verifyTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Registration Failed : ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }


        }


    }
    fun savename(firstName: String, email: String) {
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("FirstName", firstName)
            putString("Email", email)
            apply()
        }
    }



}