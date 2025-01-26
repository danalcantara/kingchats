package com.doublevision.kingchats.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.doublevision.kingchats.Activitys.MensagemActivity
import com.doublevision.kingchats.Adapters.ContatosAdapter
import com.doublevision.kingchats.Adapters.ConversaAdapter
import com.doublevision.kingchats.Model.Conversa
import com.doublevision.kingchats.Model.Usuario
import com.doublevision.kingchats.R
import com.doublevision.kingchats.Util.constantes
import com.doublevision.kingchats.databinding.FragmentContatoFragmentsBinding
import com.doublevision.kingchats.databinding.FragmentConversasBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class ConversasFragment : Fragment() {
    lateinit var binding: FragmentConversasBinding
    val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }
    lateinit var eventSnapshot:ListenerRegistration
    lateinit var conversaAdapter:ConversaAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConversasBinding.inflate(
            inflater, container ,false
        )
        conversaAdapter = ConversaAdapter {  conversa ->
            var intent = Intent(context, MensagemActivity::class.java)

            var usuario = Usuario(id = conversa.idUsuarioDestinatario, Nome = conversa.nome)
            intent.putExtra("dadosDestinatario", usuario)

            startActivity(intent)
        }
        binding.rvConversas.adapter = conversaAdapter
        binding.rvConversas.layoutManager = LinearLayoutManager(context)
        binding.rvConversas.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        recuperarContatosListener()
    }

    private fun recuperarContatosListener() {
        var idUsuario_Remetente = firebaseAuth.currentUser?.uid
        if (idUsuario_Remetente != null){
            eventSnapshot = firebaseStore.collection("conversas").document(idUsuario_Remetente).collection("ultimas_conversas").addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                        Toast.makeText(context, "Erro ao listar Mensagens", Toast.LENGTH_SHORT)
                } else {


                    var documents = querySnapshot?.documents
                    var listaConversa = mutableListOf<Conversa>()
                    documents?.forEach { documentosSnap ->
                        Log.i("conv", "${documentosSnap}")
                        Log.i("entrou", "sera que deu")
                        var conversa = documentosSnap.toObject(Conversa::class.java)
                        if (conversa != null) {

                            var idUsuario = firebaseAuth.currentUser?.uid
                            if (idUsuario != conversa.idUsuarioDestinatario) {
                                listaConversa.add(conversa)
                                Log.i("conversa", "${conversa}")
                            }

                        }
                    }
                    if (listaConversa.isNotEmpty()) {
                       conversaAdapter.adicionaMensagemParaALista( listaConversa )
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        eventSnapshot.remove()
    }
}