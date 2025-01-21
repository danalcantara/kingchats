package com.doublevision.kingchats

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.doublevision.kingchats.databinding.ActivityLoginPageBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

import kotlin.coroutines.resume

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
    val loadingAnimation by lazy { findViewById<LottieAnimationView>(R.id.animationLoadingLogin) }
    val cardOfAnimation by lazy { binding.loadindCard }
    override fun onStart() {
        super.onStart()
        if (firebaseauth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
        } else {
            if (binding.inputEmailLogin.text.toString().isNotEmpty())
            binding.inputEmailLogin.text?.clear()
        }
        if (binding.inputPasswordLogin.text.toString().isNotEmpty())
            binding.inputPasswordLogin.text?.clear()
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


        onAnimationLoading()
        lifecycleScope.launch {
            var usuario_id = logarUsuarioNoApp()

            if (usuario_id != null) {
                offAnimationLoading()
                startActivity(Intent(context, MainActivity::class.java))
            } else {
                offAnimationLoading()
                Toast.makeText(context, "Erro ao fazer login", Toast.LENGTH_SHORT).show()

            }
        }

    }

    fun onAnimationLoading() {
        cardOfAnimation?.visibility = View.VISIBLE
        loadingAnimation.playAnimation()

    }

    fun offAnimationLoading() {
        loadingAnimation.pauseAnimation()
        cardOfAnimation?.visibility = View.INVISIBLE

    }

    private suspend fun logarUsuarioNoApp(): String? = suspendCancellableCoroutine { continuation ->
        try {
            firebaseauth.signInWithEmailAndPassword(
                binding.inputEmailLogin.text.toString(),
                binding.inputPasswordLogin.text.toString()
            ).addOnSuccessListener { user ->
                continuation.resume(user.user?.uid)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            continuation.resumeWith(Result.failure(e))
        }
    }


    fun navigationToRegister(view: View) {
        startActivity(
            Intent(this, RegisterPage::class.java)
        )
    }
}
