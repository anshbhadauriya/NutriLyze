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

class MainActivity2 : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main2)
        auth = FirebaseAuth.getInstance()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

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
                            user?.sendEmailVerification()?.addOnCompleteListener { verifyTask ->
                                if (verifyTask.isSuccessful) {
                                    Toast.makeText(this, "Registration successful. Verification email sent to ${user.email}. Please verify your email before login.", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this, MainActivity3::class.java)
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


}