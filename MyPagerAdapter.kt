package com.jetbrains.handson.mpp.ny

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MyPagerAdapter (fm : FragmentManager) : FragmentPagerAdapter(fm){

    override fun getCount(): Int {
        return 3
        //method returns 3 tabs
    }

    override fun getItem(position: Int): Fragment {
        return when (position){
            0 -> {
                HomeFragment()
            }
            
            1 -> {
                AktivitetFragment()
            }
            
            else -> {
                HelpFragment()
            }
            
        }
        
        //method set out tabs positions

    }
    override fun getPageTitle(position: Int) : CharSequence?{
        return when (position){
            0 -> "Home"
            1 -> "Activity"
            else -> "Help"

            //method sets out tabs titles

        }
    }
}
