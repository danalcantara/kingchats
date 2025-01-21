package com.doublevision.kingchats

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doublevision.kingchats.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    val firebaseauth by lazy {
        FirebaseAuth.getInstance()
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
        toolbarInitializer(binding.toolbarlogin)
    }

    fun deslogarUser() {
        AlertDialog.Builder(this).setTitle("Deslogar")
            .setMessage("Deseja Realmente sair?").setPositiveButton("Confirmar"){
                dialog, posicao->
                firebaseauth.signOut()
                startActivity(Intent(this, LoginPage::class.java))
            }.setNeutralButton("Cancelar"){ dialog, posicao -> }.show()
    }

    fun toolbarInitializer(tool: MaterialToolbar) {
        setSupportActionBar(tool)
        supportActionBar?.apply {
            title = "KChats"
        }
        addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.menu_main, menu)
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                   when (menuItem.itemId){
                       R.id.logoutapp-> deslogarUser()
                   }
                    return true
                }

            }
        )
    }
}
