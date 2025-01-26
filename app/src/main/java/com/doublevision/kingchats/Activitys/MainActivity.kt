package com.doublevision.kingchats.Activitys

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.doublevision.kingchats.Adapters.viewPagerAdapter
import com.doublevision.kingchats.R
import com.doublevision.kingchats.databinding.ActivityMainBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.tabs.TabLayoutMediator
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
        tabNavigationInitializer()
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
                       R.id.profile -> navigateToProfile()
                       R.id.logoutapp -> deslogarUser()
                   }
                    return true
                }

            }
        )
    }
    fun navigateToProfile(){
        startActivity(Intent(this, Profile::class.java))
    }
    fun tabNavigationInitializer(){
        var abas = listOf<String>("Conversas", "Contato")
            var tabhost = binding.tabLayout
            var pager = binding.viewPagerPrincipal //

        tabhost.isTabIndicatorFullWidth = true //indicador ter o tamanho inteiro da aba selecionada
        pager.adapter = viewPagerAdapter(abas,supportFragmentManager, lifecycle)
        TabLayoutMediator(tabhost, pager){aba,posicao ->
            aba.text = abas[posicao]
        }.attach()
    }
}
