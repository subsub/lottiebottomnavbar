package com.subkhansarif.bottomnavbar

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView

/**
 * Created by subkhansarif on 18/09/18
 **/

class LottieBottomNavbar : LinearLayout {
    private var menu: MutableList<BottomMenu> = ArrayList()
    private var listener: IBottomClickListener? = null
    private var iconList: MutableList<LottieAnimationView> = ArrayList()
    private var titleList: MutableList<TextView> = ArrayList()
    private var containerList: MutableList<LinearLayout> = ArrayList()
    private var itemCount: Int = 1
    private var buttonContainerBackgroundColor: Int = Color.WHITE
    private var buttonsHeight: Float = 56f
    private var selectedItem: Int = 0
    private var containerWidth: Int = 0
    private var viewPager: UnswipeableViewPager? = null
    private var navbarContainer: LinearLayout? = null
    private var fragmentManager: FragmentManager? = null
    private var offscreenPageLimit: Int = 1
    private var enableViewPagerSwipe: Boolean = true
    private var viewPagerBackground: Int = Color.WHITE
    private var buttonColor: Int = Color.GRAY
    private var activeButtonColor: Int = Color.BLUE
    private var navbarElevation: Float = 0f


    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        getLayoutAtr(attrs)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle) {
        getLayoutAtr(attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val parentHeight = MeasureSpec.getSize(heightMeasureSpec)
        this.setMeasuredDimension(parentWidth, parentHeight)

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        containerWidth = parentWidth
        resizeContainer()
    }

    private fun resizeContainer() {

        // menu item width is equal: container width / size of menu item
        val itemWidth = containerWidth / itemCount

        containerList.forEach {
            val llLayoutParam = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.MATCH_PARENT)
            it.layoutParams = llLayoutParam
            it.invalidate()
        }
        invalidate()
    }

    private fun getLayoutAtr(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LottieBottomNavbar)
        val defaultButtonHeight = 56f * context.resources.displayMetrics.density
        val defaultElevation = 15f * context.resources.displayMetrics.density

        itemCount = a.getInt(R.styleable.LottieBottomNavbar_itemCount, 1)
        buttonContainerBackgroundColor = a.getColor(R.styleable.LottieBottomNavbar_buttonContainerBackgroundColor, Color.WHITE)
        buttonsHeight = a.getDimension(R.styleable.LottieBottomNavbar_buttonsHeight, defaultButtonHeight)
        offscreenPageLimit = a.getInt(R.styleable.LottieBottomNavbar_offscreenPageLimit, 1)
        enableViewPagerSwipe = a.getBoolean(R.styleable.LottieBottomNavbar_setViewPagerSwipeable, true)
        viewPagerBackground = a.getColor(R.styleable.LottieBottomNavbar_viewPagerBackground, Color.WHITE)
        buttonColor = a.getColor(R.styleable.LottieBottomNavbar_buttonColor, context.resources.getColor(R.color.colorGrey))
        activeButtonColor = a.getColor(R.styleable.LottieBottomNavbar_activeButtonColor, context.resources.getColor(R.color.colorLightBlue))
        navbarElevation = a.getDimension(R.styleable.LottieBottomNavbar_navbarElevation, defaultElevation)

        a.recycle()

        weightSum = 1f
        orientation = VERTICAL
    }

    private fun setupMenuItems() {

        // menu item width is equal: container width / size of menu item
        val itemWidth = containerWidth / itemCount

        iconList.clear()
        titleList.clear()
        containerList.clear()


        val iconDimen = 22f.toDp(context)
        val iconPadding = 2
        val textSize = 10f
        val llLayoutParam = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.MATCH_PARENT)
        val imgLayoutParam = LinearLayout.LayoutParams(iconDimen, iconDimen)
        val txtLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)


        setupViewPager()

        // create Button Container
        navbarContainer = LinearLayout(context)
        navbarContainer?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, buttonsHeight.toInt())
        navbarContainer?.setBackgroundColor(buttonContainerBackgroundColor)
        navbarContainer?.orientation = HORIZONTAL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            navbarContainer?.elevation = navbarElevation
        }


        // for each menu:
        // create item container, draw image icon and title, add click listener if set
        menu.forEachIndexed { index, bottomMenu ->

            // add linear layout as container for menu item
            val buttonContainer = LinearLayout(context)
            buttonContainer.layoutParams = llLayoutParam
            buttonContainer.orientation = LinearLayout.VERTICAL
            buttonContainer.gravity = Gravity.CENTER
            buttonContainer.background = context.resources.getDrawable(R.drawable.bg_menu_navbar)
            containerList.add(index, buttonContainer)


            // add image view to display menu icon
            val icon = LottieAnimationView(context)
            icon.layoutParams = imgLayoutParam
            icon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
            if (selectedItem == index) {
                if (!bottomMenu.animName.isNullOrBlank()) {
                    icon.setAnimation(bottomMenu.animName)
                    icon.repeatCount = 0
                    icon.setColorFilter(activeButtonColor, PorterDuff.Mode.SRC_ATOP)
                    icon.playAnimation()
                } else {
                    icon.setImageDrawable(context.resources.getDrawable(bottomMenu.icon))
                    icon.setColorFilter(activeButtonColor, PorterDuff.Mode.SRC_ATOP)
                }
            } else {
                icon.setImageDrawable(context.resources.getDrawable(bottomMenu.icon))
                icon.setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP)
            }
            iconList.add(index, icon)
            buttonContainer.addView(icon)


            // add text view to show title
            val title = TextView(context)
            title.layoutParams = txtLayoutParam
            title.text = bottomMenu.title
            title.textSize = textSize
            if (selectedItem == index) {
                title.setTextColor(activeButtonColor)
            } else {
                title.setTextColor(buttonColor)
            }
            titleList.add(index, title)
            buttonContainer.addView(title)


            // add click listener
            buttonContainer.setOnClickListener {
                setSelected(index)
            }


            // add view to container
            navbarContainer?.addView(buttonContainer)
        }

        addView(viewPager)
        addView(navbarContainer)
    }

    private fun setupViewPager() {
        // Create ViewPager Layout
        if (viewPager == null) {
            viewPager = UnswipeableViewPager(context)
            viewPager?.enableSwipe(enableViewPagerSwipe)
            viewPager?.id = R.id.main_view_pager
            viewPager?.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f)
            viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(p0: Int) {

                }

                override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

                }

                override fun onPageSelected(p0: Int) {
                    setSelected(p0)
                }
            })
            viewPager?.setBackgroundColor(viewPagerBackground)
        }
        if (fragmentManager != null) {
            viewPager?.adapter = MainFragmentPagerAdapter(fragmentManager!!, menu.map { it.fragment })
            viewPager?.offscreenPageLimit = offscreenPageLimit
        }
    }

    private fun handleItemClicked(index: Int, bottomMenu: BottomMenu) {
        changeColor(index)

        // invoke listener
        Handler().post {
            listener?.menuClicked(index, bottomMenu.id)
        }
    }

    private fun changeColor(newPosition: Int) {
        // change previously selected item color
        iconList[selectedItem].setImageDrawable(context.resources.getDrawable(menu[selectedItem].icon))
        iconList[selectedItem].setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP)
        titleList[selectedItem].setTextColor(buttonColor)
        iconList[selectedItem].invalidate()
        titleList[selectedItem].invalidate()

        // change currently selected item color

        if (!menu[newPosition].animName.isNullOrBlank()) {
            iconList[newPosition].setAnimation(menu[newPosition].animName)
            iconList[newPosition].repeatCount = 0
            iconList[newPosition].setColorFilter(activeButtonColor, PorterDuff.Mode.SRC_ATOP)
            iconList[newPosition].playAnimation()
        } else {
            iconList[newPosition].setColorFilter(activeButtonColor, PorterDuff.Mode.SRC_ATOP)
        }

        titleList[newPosition].setTextColor(activeButtonColor)
        iconList[newPosition].invalidate()
        titleList[newPosition].invalidate()

        selectedItem = newPosition
    }

    fun setSelected(position: Int) {
        if (menu.size > position) {
            handleItemClicked(position, menu[position])
        }
        selectedItem = position
        viewPager?.currentItem = selectedItem
    }

    fun setMenu(menu: List<BottomMenu>) {
        this.menu.clear()
        this.menu.addAll(menu)

        itemCount = this.menu.size
        resizeContainer()

        setupMenuItems()
        invalidate()
    }

    fun setFragmentManager(fm: FragmentManager) {
        fragmentManager = fm
    }

    fun setMenuClickListener(listener: IBottomClickListener) {
        this.listener = listener
    }
}

class MainFragmentPagerAdapter(fm: FragmentManager, fragments: List<Fragment>) : FragmentStatePagerAdapter(fm) {

    private val fragments: MutableList<Fragment> = fragments.toMutableList()


    fun updateFragments(newFragments: List<Fragment>) {
        fragments.clear()
        fragments.addAll(newFragments)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }
}

data class BottomMenu(val id: Long, val fragment: Fragment, val title: String, val icon: Int, val animName: String?)
interface IBottomClickListener {
    fun menuClicked(position: Int, id: Long)
}

fun Float.toDp(context: Context): Int {
    val r = context.resources
    val px = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            r.displayMetrics
    ).toInt()
    return px
}