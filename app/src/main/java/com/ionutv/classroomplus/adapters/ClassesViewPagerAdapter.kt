package com.ionutv.classroomplus.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ionutv.classroomplus.Constants
import com.ionutv.classroomplus.fragments.ClassesTabFragment

class ClassesViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val fragment = ClassesTabFragment()
        fragment.arguments = Bundle().apply {
            putInt(Constants.ARG_POSITION, position + 1)
        }
        return fragment
    }
}