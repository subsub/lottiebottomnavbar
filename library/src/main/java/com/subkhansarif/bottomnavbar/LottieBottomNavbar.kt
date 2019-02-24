package com.subkhansarif.bottomnavbar

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Handler
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView


/**
 * Created by subkhansarif on 18/09/18
 **/

private const val DEFAULT_HEIGHT = 56f
private const val DEFAULT_ICON_PADDING = 2
private const val DEFAULT_ICON_SIZE = 24f
private const val DEFAULT_TEXT_SIZE = 10f

class LottieBottomNavbar : LinearLayout {
    private var menu: MutableList<BottomMenu> = ArrayList()
    private var listener: IBottomClickListener? = null
    private var iconList: MutableList<LottieAnimationView> = ArrayList()
    private var titleList: MutableList<TextView> = ArrayList()
    private var containerList: MutableList<LinearLayout> = ArrayList()
    private var itemCount: Int = 1
    private var buttonContainerBackgroundColor: Int = Color.WHITE
    private var buttonsHeight: Float = DEFAULT_HEIGHT
    private var selectedItem: Int = 0
    private var containerWidth: Int = 0
    private var navbarContainer: LinearLayout? = null
    private var buttonColor: Int = Color.GRAY
    private var activeButtonColor: Int = Color.TRANSPARENT
    private var drawableRippleBackground: Int = -1

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        getLayoutAtr(attrs)
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyle: Int) : super(ctx, attrs, defStyle) {
        getLayoutAtr(attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        resizeContainer()
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        this.setMeasuredDimension(parentWidth, buttonsHeight.toInt())

        super.onMeasure(widthMeasureSpec, buttonsHeight.toInt())

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
        val defaultButtonHeight = DEFAULT_HEIGHT * context.resources.displayMetrics.density

        buttonContainerBackgroundColor = a.getColor(R.styleable.LottieBottomNavbar_buttonContainerBackgroundColor, Color.WHITE)
        buttonsHeight = a.getDimension(R.styleable.LottieBottomNavbar_buttonsHeight, defaultButtonHeight)

        buttonColor = a.getColor(R.styleable.LottieBottomNavbar_buttonColor, ContextCompat.getColor(context, R.color.colorGrey))
        activeButtonColor = a.getColor(R.styleable.LottieBottomNavbar_activeButtonColor, ContextCompat.getColor(context, R.color.transparent))
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

        val iconDimen = DEFAULT_ICON_SIZE.toDp(context)
        val llLayoutParam = LinearLayout.LayoutParams(itemWidth, LinearLayout.LayoutParams.MATCH_PARENT)
        val imgLayoutParam = LinearLayout.LayoutParams(iconDimen, iconDimen)
        val txtLayoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // create Button Container
        navbarContainer = LinearLayout(context)
        navbarContainer?.let {
            it.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, buttonsHeight.toInt())
            it.setBackgroundColor(buttonContainerBackgroundColor)
            it.orientation = HORIZONTAL
        }


        // for each menu:
        // create item container, draw image icon and title, add click listener if set
        menu.forEachIndexed { index, bottomMenu ->

            // add linear layout as container for menu item
            val buttonContainer = LinearLayout(context)
            buttonContainer.layoutParams = llLayoutParam
            buttonContainer.orientation = LinearLayout.VERTICAL
            buttonContainer.gravity = Gravity.CENTER
            buttonContainer.setBackgroundColor(Color.TRANSPARENT)
            containerList.add(index, buttonContainer)


            // add image view to display menu icon
            val icon = LottieAnimationView(context)
            icon.layoutParams = imgLayoutParam
            icon.setPadding(DEFAULT_ICON_PADDING, DEFAULT_ICON_PADDING, DEFAULT_ICON_PADDING, DEFAULT_ICON_PADDING)
            if (selectedItem == index && !bottomMenu.animName.isNullOrBlank()) {
                icon.setAnimation(bottomMenu.animName)
                icon.repeatCount = 0
                icon.setColorFilter(activeButtonColor, PorterDuff.Mode.SRC_ATOP)
                icon.playAnimation()
            } else {
                icon.setImageDrawable(ContextCompat.getDrawable(context, bottomMenu.icon))
                icon.setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP)
            }
            iconList.add(index, icon)
            buttonContainer.addView(icon)


            // add text view to show title
            val title = TextView(context)
            title.layoutParams = txtLayoutParam
            title.text = bottomMenu.title
            title.textSize = DEFAULT_TEXT_SIZE
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
        layoutContent()
    }

    private fun layoutContent() {
        when {
            (indexOfChild(navbarContainer) >= 0) -> removeView(navbarContainer)
        }
        addView(navbarContainer)
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
        iconList[selectedItem].setImageDrawable(ContextCompat.getDrawable(context, menu[selectedItem].icon))
        iconList[selectedItem].setColorFilter(buttonColor, PorterDuff.Mode.SRC_ATOP)
        titleList[selectedItem].setTextColor(buttonColor)
        iconList[selectedItem].invalidate()
        titleList[selectedItem].invalidate()

        // change button container background
        containerList.forEachIndexed { index, linearLayout ->
            if (index == newPosition) {
                linearLayout.background = ContextCompat.getDrawable(context, if (drawableRippleBackground < 0) R.drawable.bg_menu_navbar else drawableRippleBackground)
            } else {
                linearLayout.setBackgroundColor(Color.TRANSPARENT)
            }
        }

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

    fun setRippleDrawable(rippleDrawable: Int) {
        this.drawableRippleBackground = rippleDrawable
        containerList.onEach {
            it.background = ContextCompat.getDrawable(context, if (drawableRippleBackground < 0) R.drawable.bg_menu_navbar else drawableRippleBackground)
        }
        invalidate()
    }

    fun setSelected(position: Int) {
        if (menu.size > position) {
            if (menu[position].overrideFragmentClick != null && menu[position].overrideFragmentClick!!.invoke()) {
                return
            }
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

    fun setNavbarPositionTop() {
        layoutContent()
        invalidate()
    }
}

data class BottomMenu(val id: Long, val title: String, val icon: Int, val animName: String?, val overrideFragmentClick: (() -> Boolean)? = null)
interface IBottomClickListener {
    fun menuClicked(position: Int, id: Long)
}

fun Float.toDp(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics).toInt()
}