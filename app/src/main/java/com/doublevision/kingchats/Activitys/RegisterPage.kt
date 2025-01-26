package com.doublevision.kingchats.Activitys

import android.content.Intent
import android.os.Bundle

import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.doublevision.kingchats.R
import com.doublevision.kingchats.Util.ShowMessage
import com.doublevision.kingchats.databinding.ActivityRegisterPageBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class RegisterPage : AppCompatActivity() {
    val binding by lazy {
        ActivityRegisterPageBinding.inflate(layoutInflater)
    }

    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebasestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val context by lazy {
        this
    }
    var verificationId: String? = null

    val loadingAnimation by lazy { findViewById<LottieAnimationView>(R.id.animationLoadingRegister) }
    val cardOfAnimation by lazy { binding.loadindCardRegister }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        toolbarInitializer(binding.toolbar)
    }
private fun unableFildsAndButtons(){
    binding.containerNome.isEnabled = false
    binding.containerSenha1.isEnabled = false
    binding.containerSenha2.isEnabled = false
    binding.mailContainer.isEnabled = false
    binding.btnRegister.isEnabled = false
    binding.toolbar.isEnabled =false

}
    private fun enableFildsAndButtons(){
        binding.containerNome.isEnabled = true
        binding.containerSenha1.isEnabled = true
        binding.containerSenha2.isEnabled = true
        binding.mailContainer.isEnabled = true
        binding.btnRegister.isEnabled = true
}
    fun RegisterUser(view: View) {
        onAnimationLoading()
        unableFildsAndButtons()
        if (validateDataUser()) {
        lifecycleScope.launch {

                var id = processRegister()
                if (id != null) {


                    var condititionSetDB = setUserInDataBase(id)
                    if (condititionSetDB) {
                        ShowMessage(binding.root, "EStranho mano")
                        offAnimationLoading()
                        startActivity(Intent(context, MainActivity::class.java))
                    } else {

                        offAnimationLoading()
                        enableFildsAndButtons()
                        binding.errorLogin?.visibility = View.VISIBLE
                        ShowMessage(binding.root, "Deu Ruim")

                }
                }
            }

        } else {
            offAnimationLoading()
            enableFildsAndButtons()
            binding.errorLogin?.visibility = View.VISIBLE
            ShowMessage(binding.root, "Valores invalidos")
        }
    }

    private suspend fun processRegister(): String? = suspendCancellableCoroutine { continuation ->
        try {

            val email = binding.inputMail.text.toString()
            val senha = binding.inputPasswordRegister.text.toString()
            firebaseauth.createUserWithEmailAndPassword(email, senha).addOnSuccessListener { user ->
                ShowMessage(binding.root, "Usuario foi Criado")
                continuation.resume(user.user?.uid)
            }.addOnFailureListener {
                continuation.resume(null)
            }
        } catch (e: Exception) {
            continuation.resumeWith(Result.failure(e))
        }
    }

    private suspend fun setUserInDataBase(id: String): Boolean = suspendCancellableCoroutine {
        val nome = binding.inputName.text.toString()
        val email = binding.inputMail.text.toString()
        firebasestore.collection("Users").document(id)
            .set(mapOf("id" to id, "Nome" to nome, "Email" to email))
            .addOnSuccessListener { sucess ->
                it.resume(true)
            }.addOnFailureListener { failure ->
            it.resume(false)

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

    private fun validateDataUser(): Boolean {
        val nome = binding.inputName.text.toString()
        val email = binding.inputMail.text.toString()
        val senha = binding.inputPasswordRegister.text.toString()
        val confirmSenha = binding.confirmPassword.text.toString()
        if (nome.length > 4) {

            if (email.length > 10) {

                if (senha.length > 5) {

                    if (senha.equals(confirmSenha)) {
                        return true
                    } else {
                        binding.confirmPassword?.error = "Não é igual"
                        ShowMessage(binding.root, "Senha incompativel")
                    }
                } else {
                    binding.inputPasswordRegister?.error = "Senha invalida"
                }
            } else {
                binding.inputMail.error = "Email invalido deve ser maior que 10 digitos"

            }
        } else {

            binding.inputName?.error = "Nome deve ser acima de 4 digitos"
        }

        return false
    }

    fun toolbarInitializer(tool: MaterialToolbar) {
        setSupportActionBar(tool)
        supportActionBar?.apply {
            title = "Register"
            setDisplayHomeAsUpEnabled(true)
        }

    }
}