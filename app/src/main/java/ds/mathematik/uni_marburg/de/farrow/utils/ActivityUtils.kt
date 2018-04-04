package ds.mathematik.uni_marburg.de.farrow.utils

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.MenuRes
import android.support.v4.app.FragmentActivity
import android.view.Menu
import ca.allanwang.kau.utils.setMenuIcons
import com.mikepenz.iconics.typeface.IIcon

/**
 * Initialize the contents of the Activity's standard options menu.
 * You should place your menu items in to [menu].
 *
 * @param menuRes
 * @param menu
 * @param color
 * @param iicons
 */
fun Activity.createOptionsMenu(
    @MenuRes menuRes: Int,
    menu: Menu?,
    @ColorInt color: Int = Color.WHITE,
    vararg iicons: Pair<Int, IIcon>
): Boolean {
    menu ?: return false
    menuInflater.inflate(menuRes, menu)
    setMenuIcons(menu, color, *iicons)
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