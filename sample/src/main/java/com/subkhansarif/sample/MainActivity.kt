package com.subkhansarif.sample

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.subkhansarif.sample.fragments.FoodFragment
import com.subkhansarif.sample.fragments.PetFragment
import com.subkhansarif.sample.fragments.ProfileFragment
import com.subkhansarif.bottomnavbar.BottomMenu
import com.subkhansarif.bottomnavbar.IBottomClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IBottomClickListener {

    private var menu: MutableList<BottomMenu> = ArrayList()

    companion object {
        const val MENU_PERSON = 0L
        const val MENU_PETS = 1L
        const val MENU_RESTAURANT = 2L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavbar()

        val pagerAdapter = MainFragmentPagerAdapter(supportFragmentManager)
        main_view_pager.adapter = pagerAdapter
        main_view_pager.offscreenPageLimit = 4
        main_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                bottom_navbar.setSelected(p0)
            }
        })

        if (savedInstanceState == null) {
            selectPage(1)
        }
    }

    private fun setupBottomNavbar() {
        // create menu
        menu.add(BottomMenu(MENU_PERSON, getString(R.string.lbl_menu_profile), R.drawable.ic_person_grey, "a_cup_of_coffee.json"))
        menu.add(BottomMenu(MENU_PETS, getString(R.string.lbl_menu_pet), R.drawable.ic_pets_grey, "a_cup_of_coffee.json"))
        menu.add(BottomMenu(MENU_RESTAURANT, getString(R.string.lbl_menu_Food), R.drawable.ic_restaurant_menu_grey, null))

        bottom_navbar.setSelected(1)
        bottom_navbar.setMenu(menu)
        bottom_navbar.setMenuClickListener(this)
    }

    private fun selectPage(page: Int) {
        main_view_pager.currentItem = page
    }

    override fun menuClicked(position: Int, id: Long) {
        selectPage(position)
    }
}

class MainFragmentPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            1 -> {
                ProfileFragment()
            }
            2 -> {
                PetFragment()
            }
            else -> {
                FoodFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 3
    }
}