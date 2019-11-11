package com.example.routinechecks.fragments

import android.content.Context
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.example.routinechecks.BaseActivity
import com.example.routinechecks.custom.ProgressBarDialog
import com.example.routinechecks.utils.Prefs

open class BaseDialogFragment : DialogFragment() {

    protected lateinit var appCompatActivity: AppCompatActivity
    protected lateinit var mBaseActivity: BaseActivity
    protected lateinit var prefs: Prefs
    private var progressDialog: ProgressBarDialog? = null

    fun dismissAllDialogs(manager: FragmentManager) {
        val fragments = manager.fragments

        for (fragment in fragments) {
            if (fragment is DialogFragment) {
                fragment.dismissAllowingStateLoss()
            }

            val childFragmentManager = fragment.childFragmentManager
            dismissAllDialogs(childFragmentManager)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AppCompatActivity)
            appCompatActivity = context

        if (context is BaseActivity)
            mBaseActivity = context

        prefs = Prefs.getsInstance()
    }

    fun toast(msg: Any?) {
        if (msg == null) {
            return
        }

        Toast.makeText(appCompatActivity.applicationContext, msg.toString(), Toast.LENGTH_SHORT).show()
    }

    fun toastLong(msg: Any?) {
        if (msg == null) {
            return
        }
        Toast.makeText(appCompatActivity.applicationContext, msg.toString(), Toast.LENGTH_LONG).show()
    }

    fun makeProgressDialog(isCancelable: Boolean) {
        progressDialog = ProgressBarDialog(appCompatActivity)
        progressDialog!!.setCancelable(isCancelable)
        progressDialog!!.setCanceledOnTouchOutside(isCancelable)
    }

    fun showProgressDialog() {
        if (progressDialog != null && !progressDialog!!.isShowing)
            progressDialog!!.show()
    }

    fun dismissProgressDialog() {
        if (progressDialog != null && progressDialog!!.isShowing)
            progressDialog!!.dismiss()
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}
