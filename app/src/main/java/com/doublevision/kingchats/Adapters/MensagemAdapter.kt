package com.doublevision.kingchats.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.doublevision.kingchats.Model.Mensagens
import com.doublevision.kingchats.databinding.ItemDestinatarioMensagensBinding
import com.doublevision.kingchats.databinding.ItemRemetenteMensagensBinding
import com.google.firebase.auth.FirebaseAuth

class MensagemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listaMensagens = mutableListOf<Mensagens>()

    // Atualiza a lista de mensagens
    fun updateMessages(newMessages: List<Mensagens>) {
        listaMensagens.clear()
        listaMensagens.addAll(newMessages)
        notifyDataSetChanged()
    }

    // ViewHolder para mensagens do remetente
    class MensagemRemetenteViewHolder(
        private val binding: ItemRemetenteMensagensBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mensagem: Mensagens) {
            binding.mensagemRemetente.text = mensagem.texto
        }
    }

    // ViewHolder para mensagens do destinatário
    class MensagemDestinatarioViewHolder(
        private val binding: ItemDestinatarioMensagensBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mensagem: Mensagens) {
            binding.mensagemDestinatario.text = mensagem.texto
        }
    }

    // Determina o tipo de visualização com base no ID do usuário
    override fun getItemViewType(position: Int): Int {
        val mensagem = listaMensagens[position]
        val idUsuarioAtual = FirebaseAuth.getInstance().currentUser?.uid

        return if (idUsuarioAtual == mensagem.idRemetente) {
            VIEW_TYPE_REMETENTE
        } else {
            VIEW_TYPE_DESTINATARIO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return if (viewType == VIEW_TYPE_REMETENTE) {
            val binding = ItemRemetenteMensagensBinding.inflate(inflater, parent, false)
            MensagemRemetenteViewHolder(binding)
        } else {
            val binding = ItemDestinatarioMensagensBinding.inflate(inflater, parent, false)
            MensagemDestinatarioViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mensagem = listaMensagens[position]

        when (holder) {
            is MensagemRemetenteViewHolder -> holder.bind(mensagem)
            is MensagemDestinatarioViewHolder -> holder.bind(mensagem)
        }
    }

    override fun getItemCount(): Int = listaMensagens.size

    companion object {
        private const val VIEW_TYPE_REMETENTE = 0
        private const val VIEW_TYPE_DESTINATARIO = 1
    }
}
