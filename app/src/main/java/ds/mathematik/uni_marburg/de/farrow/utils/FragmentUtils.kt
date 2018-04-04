package ds.mathematik.uni_marburg.de.farrow.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.MenuRes
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuInflater
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
inline fun Fragment.createOptionsMenu(
    inflater: MenuInflater?,
    @MenuRes menuRes: Int,
    menu: Menu?,
    @ColorInt color: Int = Color.WHITE,
    vararg iicons: Pair<Int, IIcon>,
    block: () -> Unit = {}
) {
    inflater ?: return
    menu ?: return
    inflater.inflate(menuRes, menu)
    val context = requireContext()
    context.setMenuIcons(menu, color, *iicons)
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