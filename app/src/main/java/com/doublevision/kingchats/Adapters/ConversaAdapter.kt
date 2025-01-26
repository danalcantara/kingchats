package com.doublevision.kingchats.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.doublevision.kingchats.Model.Conversa
import com.doublevision.kingchats.databinding.ItemConversaBinding

class ConversaAdapter(val clickContatos: (Conversa) -> Unit):Adapter<ConversaAdapter.ConversaViewHolder>() {
    private var listaConversa = emptyList<Conversa>()

    fun adicionaMensagemParaALista(list:List<Conversa>){
        listaConversa = list
        notifyDataSetChanged()
    }

    inner class ConversaViewHolder( private val binding: ItemConversaBinding): ViewHolder(binding.root){
        fun bind(conversa: Conversa){
            binding.nomeUserConversa.text = conversa.nome
            binding.ultimaConversaItem.text = conversa.ultimaMensagem
            binding.cardUserConversa.setOnClickListener{
                clickContatos(conversa)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversaViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemInflado = ItemConversaBinding.inflate(
            inflater, parent, false
        )
        return ConversaViewHolder(itemInflado)
    }

    override fun getItemCount(): Int {
        return listaConversa.size
    }

    override fun onBindViewHolder(holder: ConversaViewHolder, position: Int) {
        var conversa = listaConversa[position]
        holder.bind(conversa)
    }

}
