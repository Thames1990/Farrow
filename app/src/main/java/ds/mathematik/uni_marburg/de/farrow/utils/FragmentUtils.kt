package ds.mathematik.uni_marburg.de.farrow.utils

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.Fragment

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