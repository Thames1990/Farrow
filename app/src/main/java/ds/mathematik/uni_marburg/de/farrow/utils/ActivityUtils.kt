package ds.mathematik.uni_marburg.de.farrow.utils

import android.app.Activity
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.MenuRes
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