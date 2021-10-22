package com.mystikcoder.statussaver.framework.presentation.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mystikcoder.statussaver.framework.presentation.ui.fragment.WhatsAppImageFragment
import com.mystikcoder.statussaver.framework.presentation.ui.fragment.WhatsAppVideoFragment

class WhatsAppViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            WhatsAppImageFragment()
        } else {
            WhatsAppVideoFragment()
        }
    }
}
