package com.subkhansarif.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.subkhansarif.bottomnavbar.BottomMenu
import com.subkhansarif.bottomnavbar.IBottomClickListener
import com.subkhansarif.sample.fragments.FoodFragment
import com.subkhansarif.sample.fragments.PetFragment
import com.subkhansarif.sample.fragments.ProfileFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), IBottomClickListener {

    private var menu: MutableList<BottomMenu> = ArrayList()
    private lateinit var profileFragment: ProfileFragment
    private lateinit var petFragment: PetFragment
    private lateinit var foodFragment: FoodFragment

    companion object {
        const val MENU_PERSON = 0L
        const val MENU_PETS = 1L
        const val MENU_RESTAURANT = 2L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        profileFragment = ProfileFragment()
        petFragment = PetFragment()
        foodFragment = FoodFragment()

        setupBottomNavbar()
    }

    private fun setupBottomNavbar() {
        // create menu
        menu.add(BottomMenu(MENU_PERSON, getString(R.string.lbl_menu_profile), R.drawable.ic_person_grey, "a_cup_of_coffee.json"))
        menu.add(BottomMenu(MENU_PETS, getString(R.string.lbl_menu_pet), R.drawable.ic_pets_grey, "a_cup_of_coffee.json"))
        menu.add(BottomMenu(MENU_RESTAURANT, getString(R.string.lbl_menu_Food), R.drawable.ic_restaurant_menu_grey, null) {
            Toast.makeText(this, "Custom action that override the 'menuClicked' method", Toast.LENGTH_SHORT).show()
            // return true to override button click
            true
        })

        bottom_navbar.setMenu(menu)
        bottom_navbar.setSelected(1)
        bottom_navbar.setNavbarPositionTop()
        bottom_navbar.setMenuClickListener(this)
    }

    override fun menuClicked(position: Int, id: Long) {
        val transaction = supportFragmentManager.beginTransaction()
        when (position) {
            0 -> transaction.replace(R.id.fragmentContainer, profileFragment)
            1 -> transaction.replace(R.id.fragmentContainer, petFragment)
            2 -> transaction.replace(R.id.fragmentContainer, foodFragment)
        }
        transaction.commit()

    }
}