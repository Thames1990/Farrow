package ds.mathematik.uni_marburg.de.farrow.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle

@Suppress("DEPRECATION")
inline fun <reified T : Activity> Context.startActivity(
    clearStack: Boolean = false,
    bundleBuilder: Bundle.() -> Unit = {},
    intentBuilder: Intent.() -> Unit = {}
) = startActivity(T::class.java, clearStack, bundleBuilder, intentBuilder)

@Deprecated(
    message = "Use reified generic instead of padding class",
    replaceWith = ReplaceWith("startActivity<T>(clearStack, bundleBuilder, intentBuilder)"),
    level = DeprecationLevel.WARNING
)
inline fun <T> Context.startActivity(
    clazz: Class<T>,
    clearStack: Boolean,
    bundleBuilder: Bundle.() -> Unit,
    intentBuilder: Intent.() -> Unit
) {
    val intent = Intent(this, clazz)
    if (clearStack) intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.intentBuilder()
    val bundle = Bundle()
    bundle.bundleBuilder()
    startActivity(intent, if (bundle.isEmpty) null else bundle)
    if (clearStack && this is Activity) finish()
}
