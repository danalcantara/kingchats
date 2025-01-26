package com.doublevision.kingchats.Activitys

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.doublevision.kingchats.Adapters.MensagemAdapter
import com.doublevision.kingchats.Model.Conversa
import com.doublevision.kingchats.Model.Mensagens
import com.doublevision.kingchats.Model.Usuario
import com.doublevision.kingchats.R
import com.doublevision.kingchats.databinding.ActivityMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.util.Date

class MensagemActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMessageBinding.inflate(layoutInflater)
    }
    private var dadosDestinatario: Usuario? = null
    private var dadosUsuario: Usuario? = null
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val firebaseStore by lazy {
        FirebaseFirestore.getInstance()
    }
    private lateinit var conversasAdapter: MensagemAdapter
    private var listenerRegistration: ListenerRegistration? = null

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove() // Remove o listener para evitar vazamentos de memória
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recuperarDadosUsuarioDestinatario()
        toolbarInitializer()
        inicializarRecyclerView()
        addClickEvents()
        addListeners()
    }

    private fun inicializarRecyclerView() {
        conversasAdapter = MensagemAdapter()
        binding.rvMensagens.apply {
            adapter = conversasAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun addListeners() {
        val idDoRemetente = firebaseAuth.currentUser?.uid
        val idDoDestinatario = dadosDestinatario?.id

        if (idDoRemetente != null && idDoDestinatario != null) {
            listenerRegistration = firebaseStore.collection("mensagens")
                .document(idDoRemetente)
                .collection("conversas")
                .document(idDoDestinatario)
                .collection("mensagens")
                .orderBy("data", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Toast.makeText(this, "Erro ao recuperar mensagens", Toast.LENGTH_SHORT).show()
                        Log.e("Firestore", "Erro ao recuperar mensagens: ${error.message}", error)
                        return@addSnapshotListener
                    }

                    val mensagensAtualizadas = mutableListOf<Mensagens>()
                    snapshot?.documents?.forEach { document ->
                        val mensagem = document.toObject(Mensagens::class.java)
                        if (mensagem != null) {
                            mensagensAtualizadas.add(mensagem)
                        }
                    }

                    if (mensagensAtualizadas.isNotEmpty()) {
                        conversasAdapter.updateMessages(mensagensAtualizadas)
                    }
                }
        }
    }

    private fun addClickEvents() {
        binding.btnSendMessages.setOnClickListener {
            val mensagemTexto = binding.messagesField.text.toString()

            if (mensagemTexto.isNotEmpty()) {
                salvarMensagem(mensagemTexto)
                binding.messagesField.text?.clear()
            } else {
                Toast.makeText(this, "Digite uma mensagem antes de enviar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun salvarMensagem(textoMensagem: String) {
        val idDoRemetente = firebaseAuth.currentUser?.uid
        val idDoDestinatario = dadosDestinatario?.id

        if (idDoRemetente != null && idDoDestinatario != null) {
            val mensagem = Mensagens(
                idRemetente = idDoRemetente,
                texto = textoMensagem,
                data = Date()
            )

            salvarMensagemNoFirestore(idDoRemetente, idDoDestinatario, mensagem)
            var salvarConversaRemetente = Conversa(
                idDoRemetente,idDoDestinatario, dadosDestinatario?.Nome, mensagem.texto
            )
            salvarConversaFireStore( salvarConversaRemetente )
            salvarMensagemNoFirestore(idDoDestinatario, idDoRemetente, mensagem)
            var salvarConversaDestinatario = Conversa(
                idDoDestinatario,idDoRemetente, dadosUsuario?.Nome, mensagem.texto
            )
            salvarConversaFireStore( salvarConversaDestinatario )

        }
    }

    private fun salvarConversaFireStore(conversa: Conversa) {
        firebaseStore.collection("conversas").document(conversa.idUsuarioRemetente).collection("ultimas_conversas").document(conversa.idUsuarioDestinatario).set(conversa).addOnFailureListener {
            Toast.makeText(this, "Erro ao Salvar a Conversa", Toast.LENGTH_SHORT)
        }
    }


    private fun salvarMensagemNoFirestore(id: String, destinatarioId: String, mensagem: Mensagens) {
        firebaseStore.collection("mensagens")
            .document(id)
            .collection("conversas")
            .document(destinatarioId)
            .collection("mensagens")
            .add(mensagem)
            .addOnSuccessListener {
                Log.i("Firestore", "Mensagem salva com sucesso.")
            }
            .addOnFailureListener { error ->
                Log.e("Firestore", "Erro ao salvar mensagem: ${error.message}", error)
                Toast.makeText(this, "Erro ao salvar mensagem.", Toast.LENGTH_SHORT).show()
            }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun recuperarDadosUsuarioDestinatario() {

        var idUsuario = firebaseAuth.currentUser?.uid

        firebaseStore.collection("Users").document(idUsuario.toString()).get().addOnSuccessListener {
    snap ->
            var usuario = snap.toObject(Usuario::class.java)
            dadosUsuario = usuario
        }
        val extras = intent.extras
        if (extras != null) {
            dadosDestinatario = extras.getParcelable("dadosDestinatario", Usuario::class.java)
        }
    }

    private fun toolbarInitializer() {
        setSupportActionBar(binding.materialToolbar)
        supportActionBar?.apply {
            title = ""
            dadosDestinatario?.let {
                binding.nomeDestinatarioMessage.text = it.Nome
            }
            setDisplayHomeAsUpEnabled(true)
        }
    }
}
