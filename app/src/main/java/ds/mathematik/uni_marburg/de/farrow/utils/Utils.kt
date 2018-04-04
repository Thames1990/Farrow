package ds.mathematik.uni_marburg.de.farrow.utils

import android.annotation.SuppressLint
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.content.res.ColorStateList
import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.design.widget.FloatingActionButton
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.showIf
import ca.allanwang.kau.utils.string
import com.mikepenz.iconics.typeface.IIcon

@SuppressLint("NewApi")
inline fun FloatingActionButton.showWithOptions(
    icon: IIcon,
    @StringRes tooltipTextRes: Int,
    @ColorInt color: Int = Color.WHITE,
    backgroundColor: ColorStateList = ColorStateList.valueOf(Color.RED),
    crossinline onClickListener: () -> Unit,
    show: Boolean = true
) {
    setIcon(icon, color)
    backgroundTintList = backgroundColor
    if (buildIsOreoAndUp) tooltipText = context.string(tooltipTextRes)
    setOnClickListener { onClickListener() }
    showIf(show)
}

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(
    liveData: L,
    onChanged: (T?) -> Unit
) = liveData.observe(this, Observer(onChanged))