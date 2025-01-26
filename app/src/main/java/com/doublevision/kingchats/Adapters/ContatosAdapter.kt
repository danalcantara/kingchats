package com.doublevision.kingchats.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.doublevision.kingchats.Model.Usuario
import com.doublevision.kingchats.databinding.FragmentContatoFragmentsBinding
import com.doublevision.kingchats.databinding.ItemContatosBinding

class ContatosAdapter(val clickContatos: (Usuario) -> Unit):Adapter<ContatosAdapter.ContatoViewHolder>() {

    private var listaContatos = emptyList<Usuario>()

    fun addusertoList(list:List<Usuario>){
        listaContatos = list
        notifyDataSetChanged()
    }

    inner class ContatoViewHolder( private val binding: ItemContatosBinding): ViewHolder(binding.root){
            fun bind(usuario: Usuario){
                binding.nomeUser.text = usuario.Nome
                binding.cardUser.setOnClickListener{
                    clickContatos(usuario)
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContatoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val itemInflado = ItemContatosBinding.inflate(
            inflater, parent, false
        )
        return ContatoViewHolder(itemInflado)
    }

    override fun getItemCount(): Int {
      return listaContatos.size
    }

    override fun onBindViewHolder(holder: ContatoViewHolder, position: Int) {
        var usuario = listaContatos[position]
        holder.bind(usuario)
    }
}