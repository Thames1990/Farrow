package ds.mathematik.uni_marburg.de.farrow.utils

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.MenuRes
import android.support.v4.app.FragmentActivity
import android.support.v4.graphics.drawable.DrawableCompat
import android.view.Menu

/**
 * Initialize the contents of the Activity's standard options menu.
 * You should place your menu items in to [menu].
 *
 * @param menuRes
 * @param menu
 * @param color
 * @param icons
 */
fun Activity.createOptionsMenu(
    @MenuRes menuRes: Int,
    menu: Menu?,
    @ColorInt color: Int = Color.WHITE,
    vararg icons: Pair<Int, Drawable>
): Boolean {
    menu ?: return false
    menuInflater.inflate(menuRes, menu)
    setMenuIcons(menu, color, *icons)
    return true
}

/**
 * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given Activity
 * is alive. More detailed explanation is in {@link ViewModel}.
 * <p>
 * It uses {@link ViewModelProvider.AndroidViewModelFactory} to instantiate new ViewModels.
 *
 * @return a ViewModelProvider instance
 */
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(): T =
    ViewModelProviders.of(this)[T::class.java]

/**
 * Themes the base menu icons and adds icons programmatically based on ids
 *
 * Call in [Activity.onCreateOptionsMenu]
 */
fun setMenuIcons(
    menu: Menu,
    @ColorInt color: Int = Color.WHITE,
    vararg icons: Pair<Int, Drawable>
) = icons.forEach { (id, icon) ->
    menu.findItem(id).icon = icon.apply { DrawableCompat.setTint(this, color) }
}