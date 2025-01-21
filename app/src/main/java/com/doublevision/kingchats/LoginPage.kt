package com.doublevision.kingchats

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doublevision.kingchats.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.concurrent.thread

class LoginPage : AppCompatActivity() {
    val binding by lazy {
        ActivityLoginPageBinding.inflate(layoutInflater)
    }
    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
    }
    val context by lazy {
        this
    }
    override fun onStart() {
        super.onStart()
        if (firebaseauth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


    }

    fun loginAccount(view: View) {

        var controller = false
        GlobalScope.launch {
            launch {
                controller = logarAccount()
                withContext(Dispatchers.Main) {
                    if (controller) {
                        startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        Toast.makeText(context, "Erro ao fazer login", Toast.LENGTH_SHORT).show()
                    }
                }
            }
//Sempre da erro de login e demora muito
        }


    }
suspend fun logarAccount(): Boolean{
    var acc = false
    try {
    firebaseauth.signInWithEmailAndPassword(
        binding.inputEmailLogin.text.toString(),
        binding.inputPasswordLogin.text.toString()
    ).addOnSuccessListener {
        acc = true
    }

} catch (e:Exception){
            e.printStackTrace()
            return acc
        }
    return acc
}

    fun navigationToRegister(view: View) {
        startActivity(
            Intent(this, RegisterPage::class.java)
        )
    }
}