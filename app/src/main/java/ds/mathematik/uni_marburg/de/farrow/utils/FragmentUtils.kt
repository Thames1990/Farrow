package ds.mathematik.uni_marburg.de.farrow.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.MenuRes
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater

/**
 * Initialize the contents of the Activity's standard options menu.
 * You should place your menu items in to [menu].
 *
 * @param menuRes
 * @param menu
 * @param color
 * @param icons
 */
inline fun createOptionsMenu(
    inflater: MenuInflater?,
    @MenuRes menuRes: Int,
    menu: Menu?,
    @ColorInt color: Int = Color.WHITE,
    vararg icons: Pair<Int, Drawable>,
    block: () -> Unit = {}
) {
    inflater ?: return
    menu ?: return
    inflater.inflate(menuRes, menu)
    setMenuIcons(menu, color, *icons)
    block()
}

/**
 * Creates a {@link ViewModelProvider}, which retains ViewModels while a scope of given Activity
 * is alive. More detailed explanation is in {@link ViewModel}.
 * <p>
 * It uses {@link ViewModelProvider.AndroidViewModelFactory} to instantiate new ViewModels.
 *
 * @return a ViewModelProvider instance
 */
inline fun <reified T : ViewModel> Fragment.getViewModel(): T =
    ViewModelProviders.of(requireActivity())[T::class.java]