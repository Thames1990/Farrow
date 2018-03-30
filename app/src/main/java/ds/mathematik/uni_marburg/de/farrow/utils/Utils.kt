package ds.mathematik.uni_marburg.de.farrow.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

fun <T : Any, L : LiveData<T>> LifecycleOwner.observe(
    liveData: L,
    onChanged: (T?) -> Unit
) = liveData.observe(this, Observer(onChanged))