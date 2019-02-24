
# Lottie Navigation Bar

This library is a fork of [subsub Lottie Bottom Navbar](https://github.com/subsub/lottiebottomnavbar) that I have modified to match my needs.

Any comments or change request are welcome. Please Star this project if you like it! :)

## What is different in this forked version?
  - Rewrote in Kotlin
 - Updated to AndroidX
 - Removed integration of the view pager from the library
 - Code cleaning

### Current Latest Version is `1.0.0`

# Usage

### Add maven url Inside **Project Level build.gradle**
```bash
buildscript {
   ...
    repositories {
       ...
        maven {
              mavenCentral()
        }
    }
    dependencies {
       ...
    }
}

...

allprojects {
    repositories {
       ...
        maven {
              mavenCentral()
        }
    }
}
```

### Add dependency into your app build.gradle
```bash
implementation "com.ysdc.libs:lottienavbarlib:{latestVersion}"
```

# Sample

See sample project.

### Add LottieBottomNavbar to your layout
```xml
<com.ysdc.bottomnavbar.LottieBottomNavbar
        android:id="@+id/bottom_navbar"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        app:activeButtonColor="@color/colorWhite"
        app:buttonColor="@color/colorAccent"
        app:buttonContainerBackgroundColor="@color/colorWhite"
        app:buttonsHeight="56dp" />
```

### Setup menus for navbar
```kotlin
menu.add(BottomMenu(MENU_PERSON, getString(R.string.lbl_menu_profile), R.drawable.ic_person_grey, "a_cup_of_coffee.json"))
menu.add(BottomMenu(MENU_PETS, getString(R.string.lbl_menu_pet), R.drawable.ic_pets_grey, "a_cup_of_coffee.json"))
menu.add(BottomMenu(MENU_RESTAURANT, getString(R.string.lbl_menu_Food), R.drawable.ic_restaurant_menu_grey, null) {
  Toast.makeText(this, "Custom action that override the 'menuClicked' method", Toast.LENGTH_SHORT).show()
    // return true to override button click
  true
})
```
BottomMenu constructor parameters:
- `id: Long`, id for your button
- `label: String`, label for this button. This will be shown below button icon
- `icon: drawable res`, image resource for this button
- `(optional) lottie_animation: String`, a String of json file name for Lottie Animation.

### Add menus to navbar
```kotlin
bottom_navbar.setMenu(menu)
bottom_navbar.setSelected(1)
bottom_navbar.setNavbarPositionTop()
bottom_navbar.setMenuClickListener(this)
```

### Layout Properties
Name | Type | Description
--- | --- | ---
`buttonContainerBackgroundColor` | Color | Navbar background color
`buttonsHeight` | Dimension | Navbar Height
`activeButtonColor` | Color | Color for the selected button, this affect text and image tint, but won't affect to Lottie animation image color.
`buttonColor` | Color | Default color for when button is not selected

You will see in the sample that there is a ripple effect. You can reuse it as it is or change the color by simply declaring `rippleColor` in your colors.xml or call the `setRippleDrawable` on the bottombar.