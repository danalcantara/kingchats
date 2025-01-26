package com.doublevision.kingchats.Adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.doublevision.kingchats.fragments.ContatoFragments
import com.doublevision.kingchats.fragments.ConversasFragment

class viewPagerAdapter( val abas:List<String>,

    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
): FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return abas.size
    }

    override fun createFragment(position: Int): Fragment {
        when (position){

            1 -> return ContatoFragments()
        }
        return ConversasFragment()
    }
}