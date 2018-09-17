package com.subkhansarif.bottomnavbar

import android.content.Context
import android.graphics.PorterDuff
import android.os.Handler
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
    private var selectedItem: Int = 0
    private var containerWidth: Int = 0


    constructor(ctx: Context) : super(ctx)
    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        getItemCount(attrs)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle) {
        getItemCount(attrs)
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

    private fun getItemCount(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.LottieBottomNavbar)
        itemCount = a.getInt(R.styleable.LottieBottomNavbar_itemCount, 1)
        a.recycle()
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
                    icon.playAnimation()
                } else {
                    icon.setImageDrawable(context.resources.getDrawable(bottomMenu.icon))
                    icon.setColorFilter(context.resources.getColor(R.color.colorLightBlue), PorterDuff.Mode.SRC_ATOP)
                }
            } else {
                icon.setImageDrawable(context.resources.getDrawable(bottomMenu.icon))
                icon.setColorFilter(context.resources.getColor(R.color.colorGrey), PorterDuff.Mode.SRC_ATOP)
            }
            iconList.add(index, icon)
            buttonContainer.addView(icon)


            // add text view to show title
            val title = TextView(context)
            title.layoutParams = txtLayoutParam
            title.text = bottomMenu.title
            title.textSize = textSize
            if (selectedItem == index) {
                title.setTextColor(context.resources.getColor(R.color.colorLightBlue))
            } else {
                title.setTextColor(context.resources.getColor(R.color.colorGrey))
            }
            titleList.add(index, title)
            buttonContainer.addView(title)


            // add click listener
            buttonContainer.setOnClickListener {
                handleItemClicked(index, bottomMenu)
            }


            // add view to container
            addView(buttonContainer)
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
        iconList[selectedItem].setColorFilter(context.resources.getColor(R.color.colorGrey), PorterDuff.Mode.SRC_ATOP)
        titleList[selectedItem].setTextColor(context.resources.getColor(R.color.colorGrey))
        iconList[selectedItem].invalidate()
        titleList[selectedItem].invalidate()

        // change currently selected item color

        if (!menu[newPosition].animName.isNullOrBlank()) {
            iconList[newPosition].setAnimation(menu[newPosition].animName)
            iconList[newPosition].repeatCount = 0
            iconList[newPosition].playAnimation()
        } else {
            iconList[newPosition].setColorFilter(context.resources.getColor(R.color.colorLightBlue), PorterDuff.Mode.SRC_ATOP)
        }

        titleList[newPosition].setTextColor(context.resources.getColor(R.color.colorLightBlue))
        iconList[newPosition].invalidate()
        titleList[newPosition].invalidate()

        selectedItem = newPosition
    }

    fun setSelected(position: Int) {
        if (menu.size > position) {
            handleItemClicked(position, menu[position])
        }
        selectedItem = position
    }

    fun setMenu(menu: List<BottomMenu>) {
        this.menu.clear()
        this.menu.addAll(menu)

        itemCount = this.menu.size
        resizeContainer()

        setupMenuItems()
        invalidate()
    }

    fun setMenuClickListener(listener: IBottomClickListener) {
        this.listener = listener
    }
}

data class BottomMenu(val id: Long, val title: String, val icon: Int, val animName: String?)
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