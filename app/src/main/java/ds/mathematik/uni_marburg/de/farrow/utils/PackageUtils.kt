package ds.mathematik.uni_marburg.de.farrow.utils

import android.os.Build
import ds.mathematik.uni_marburg.de.farrow.BuildConfig

inline val buildIsOreoAndUp: Boolean
    get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

inline val isDebugBuild: Boolean
    get() = BuildConfig.DEBUG

inline val isReleaseBuild: Boolean
    get() = !isDebugBuild