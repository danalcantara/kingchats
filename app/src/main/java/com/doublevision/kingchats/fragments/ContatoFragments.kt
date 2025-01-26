package com.doublevision.kingchats.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.doublevision.kingchats.Activitys.MensagemActivity
import com.doublevision.kingchats.Adapters.ContatosAdapter
import com.doublevision.kingchats.Model.Usuario
import com.doublevision.kingchats.Util.constantes
import com.doublevision.kingchats.databinding.FragmentContatoFragmentsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ContatoFragments : Fragment() {

    lateinit var binding: FragmentContatoFragmentsBinding
    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }
   lateinit var contatosAdapter: ContatosAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContatoFragmentsBinding.inflate(
            inflater, container ,false
        )

        contatosAdapter = ContatosAdapter {  usuario ->
            var intent = Intent(context, MensagemActivity::class.java)


            intent.putExtra("dadosDestinatario", usuario)
            intent.putExtra("origem", constantes.ORIGEM_CONTATO) //origem da conversa
            startActivity(intent)
        }
        binding.rvContatos.adapter = contatosAdapter
        binding.rvContatos.layoutManager = LinearLayoutManager(context)
        binding.rvContatos.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        recuperarContatosListener()
    }

    private fun recuperarContatosListener() {
        firebaseStore.collection("Users").addSnapshotListener{
            querySnapshot, error ->
            var documents = querySnapshot?.documents
            var listaContatos = mutableListOf<Usuario>()
            documents?.forEach { documentosSnap ->
                Log.i("entrou", "sera que deu")
                var usuario = documentosSnap.toObject(Usuario::class.java)
                if (usuario != null) {
                    var idUsuario = firebaseAuth.currentUser?.uid
                    if (idUsuario != usuario.id) {
                        listaContatos.add(usuario)
                     }
                }
            }
            if (listaContatos.isNotEmpty()){
                contatosAdapter.addusertoList(listaContatos)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }
      }
