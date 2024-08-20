package com.example.gpresence

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class StatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stat, container, false)
        val tabLayout = view.findViewById<TabLayout>(R.id.ta_layout)
        val viewPager = view.findViewById<ViewPager2>(R.id.view_page)

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 ->HomeFragment()
                    1 ->EtatsGlobaux()
                    2 ->CodeFragment()
                    else -> throw IllegalStateException("Unexpected position $position")
                }
            }
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Home"
                1 -> "Etats Globaux"
                2 -> "Requete"
                else -> null
            }
        }.attach()

        return view
    }
}