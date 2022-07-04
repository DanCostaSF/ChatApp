package br.com.android.chatapp.ui.util

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

fun Fragment.navTo(@IdRes dest: Int) = findNavController().navigate(dest)
fun Fragment.navTo(directions: NavDirections) = findNavController().navigate(directions)
fun Fragment.navTo(@IdRes dest: Int, args: Bundle) = findNavController().navigate(dest, args)
fun Fragment.navBack() = findNavController().navigateUp()

fun Fragment.hasPermission(permissions: String) : Boolean {
    val permissionCheckResult = ContextCompat.checkSelfPermission(requireContext(), permissions)
    return PackageManager.PERMISSION_GRANTED == permissionCheckResult
}

fun Fragment.toast(msg: String) = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
