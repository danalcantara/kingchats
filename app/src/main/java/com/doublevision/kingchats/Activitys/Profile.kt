package com.doublevision.kingchats.Activitys

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.doublevision.kingchats.R
import com.doublevision.kingchats.databinding.ActivityProfileBinding
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Profile : AppCompatActivity() {
    val binding by lazy {
        ActivityProfileBinding.inflate(layoutInflater)
    }
    val firebaseauth by lazy{
        FirebaseAuth.getInstance()
    }
    val firebasestore by lazy {
        FirebaseFirestore.getInstance()
    }
    val loadingAnimation by lazy { findViewById<LottieAnimationView>(R.id.animationLoadingProfile) }
    val cardOfAnimation by lazy { binding.loadindCardProfile }

    var gerenciadorDeGaleria = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ){
        uri ->
        if (uri != null){
            binding.photoUser.setImageURI(uri)
        } else {
            Toast.makeText(this,"Erro ao selecionar foto", Toast.LENGTH_SHORT )

        }

    }

   var permissaoCamera = false
    var permissaoGaleria = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        verificaPermissao()
        startDatas()
        unableFields()
        toolbarInitializer(binding.toolbarProfile)
    }

    fun openGalery(view: View){
        if (permissaoGaleria){
            gerenciadorDeGaleria.launch("image/*")
        } else {
            Toast.makeText(this,"Você Não tem permissao de Galeria", Toast.LENGTH_SHORT )
            verificaPermissao()
        }
    }

    fun startDatas(){
        var id = firebaseauth.currentUser?.uid
        onAnimationLoading()
        firebasestore.collection("Users").document("$id").get().addOnSuccessListener {
            document ->
            binding.inputNomeProfile.setText("${document.getString("Nome")}")
            if (document.getString("Bio") != null) {
                binding.InputBioProfile.setText("${document.getString("Bio")}")
            } else {
                binding.InputBioProfile.setText("Adicione Uma Bio...")
            }

            offAnimationLoading()
        }.addOnFailureListener {
            binding.inputNomeProfile.setText("null")
            binding.InputBioProfile.setText("null")
            offAnimationLoading()
        }

    }
    fun toolbarInitializer(tool: MaterialToolbar) {

        setSupportActionBar(tool)
        supportActionBar?.apply {
            title = "KChats"
            setDisplayHomeAsUpEnabled(true)
        }
        addMenuProvider(
        object:MenuProvider{
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.profile_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when(menuItem.itemId){
                    R.id.EditFields -> {
                        editableFields()
                    }

                }
                return true

            }

        }
        )
    }
    fun unableFields(){
        binding.inputNomeProfile.isEnabled = false
        binding.InputBioProfile.isEnabled =false
    }
    fun enableFields(){

        binding.inputNomeProfile.isEnabled = true
        binding.InputBioProfile.isEnabled =true
    }
     fun editableFields() {

        if (binding.inputNomeProfile.isEnabled) {
            confirmEditDialog{ confirm ->
                if (confirm) {

                    setContents()
                    unableFields()
                    Toast.makeText(this,"Modo de Edição Desabilitado", Toast.LENGTH_SHORT )
                } else {
                    startDatas()
                }

            }

        } else {
            Toast.makeText(this,"Modo de Edição habilitado", Toast.LENGTH_SHORT )

            enableFields()
            startDatas()
        }
    }

    private fun confirmEditDialog(confirm: (Boolean)-> Unit){
        var cond = false
        AlertDialog.Builder(this).setTitle("Edit Campo")
                .setMessage("Deseja Concluir a Edição de campos?")
                .setPositiveButton("Sim") { dialog, posicao ->
                    confirm(true)
                    Toast.makeText(this,"clicou sim", Toast.LENGTH_SHORT )

                }.setNegativeButton("Não") { dialog, posicao ->
                confirm(false)

            }.show()
        Toast.makeText(this,"valor que ficou -> ${cond}", Toast.LENGTH_SHORT )
        }



    private fun setContents() {
        var fields = mapOf(
            "Nome" to binding.inputNomeProfile.text.toString(),
            "Bio" to binding.InputBioProfile.text.toString()

        )
        try {


            firebasestore.collection("Users").document("${firebaseauth.currentUser?.uid}").get()
                .addOnSuccessListener { usuario ->
                    firebasestore.collection("Users").document("${firebaseauth.currentUser?.uid}")
                        .update(fields)
                }.addOnFailureListener {

            }
        } catch (e:Exception){
            e.printStackTrace()
            Toast.makeText(this,"Erro ao adicionar usuario no banco de dados", Toast.LENGTH_SHORT )
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
    fun verificaPermissao() {
        permissaoCamera = ContextCompat.checkSelfPermission(
            this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        permissaoGaleria = ContextCompat.checkSelfPermission(
            this, Manifest.permission.READ_MEDIA_IMAGES
        ) == PackageManager.PERMISSION_GRANTED

        var permissoesNegadas = mutableListOf<String>()

        if (!permissaoCamera) {
            permissoesNegadas.add(Manifest.permission.CAMERA)
        }
        if (!permissaoGaleria) {
            permissoesNegadas.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (permissoesNegadas.isNotEmpty()) {

            var gerenciadorDePermissoes = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissao ->
                permissaoCamera = permissao[Manifest.permission.CAMERA] ?: permissaoCamera
                permissaoGaleria =
                    permissao[Manifest.permission.READ_MEDIA_IMAGES] ?: permissaoGaleria
            }
            gerenciadorDePermissoes.launch(permissoesNegadas.toTypedArray())
        }
    }
    }
