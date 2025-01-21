package com.doublevision.kingchats

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doublevision.kingchats.databinding.ActivityRegisterPageBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPage : AppCompatActivity() {
    val binding by lazy{
        ActivityRegisterPageBinding.inflate(layoutInflater)
    }
    val nome: String by lazy {binding.inputName?.text.toString()}
    val email:String  by lazy {binding.inputMail.text.toString()}
    val senha:String  by lazy {binding.inputPasswordRegister?.text.toString()}
    val senhaConfirm:String  by lazy {binding.confirmPassword?.text.toString()}
    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebasestore by lazy {
        FirebaseFirestore.getInstance()
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
        toolbarInitializer(binding.toolbar)
    }
    fun RegisterUser(view: View){
        if (validateDataUser()){
            firebaseauth.createUserWithEmailAndPassword(email, senha).addOnSuccessListener { user ->
                ShowMessage(binding.root, "Usuario foi Criado")
                var uid = firebaseauth.currentUser?.uid
                if (uid != null) {

                    firebasestore.collection("Users").document(uid).set( mapOf("Nome" to nome, "Email" to email)).addOnSuccessListener {
                        startActivity(Intent(this, MainActivity::class.java))
                    }.addOnFailureListener {
                        binding.errorLogin?.visibility = View.VISIBLE
                        ShowMessage(binding.root, "Deu Ruim")
                    }

                }
                }.addOnFailureListener { erro ->
                try {
                    throw erro
                } catch (e: FirebaseAuthUserCollisionException){
                    ShowMessage(binding.root ,"Ja existe esse email cadastrado no aplicativo ")
                }catch (e: FirebaseAuthInvalidCredentialsException){
                    ShowMessage(binding.root ,"Email invalido digite outro")
                }catch (e: FirebaseAuthWeakPasswordException){
                    ShowMessage(binding.root ,"Senha muito fraca tente uma mais forte")
                }
            }
        }
    }

    private fun validateDataUser(): Boolean {
        if (nome.length > 4) {
            if (email.length > 10) {
                if (senha.length > 5 ){
                    if (senha.equals(senhaConfirm)){
                        return true
                    } else{
                        binding.confirmPassword?.error = "Não é igual"
                        ShowMessage(binding.root ,"Senha incompativel")
                    }
                } else{
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