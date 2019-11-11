package com.example.routinechecks

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.example.routinechecks.dagger.ViewModelFactory
import com.example.routinechecks.utils.Prefs
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject

abstract class BaseActivity(@LayoutRes private val layoutId: Int) : AppCompatActivity() {
    protected abstract val TAG: String
    private var actionBar: ActionBar? = null
    private val currentFragment: Fragment? = null
    protected var fragmentManager: FragmentManager = supportFragmentManager
    protected lateinit var prefs: Prefs
    private var root: FrameLayout? = null

    val isActivityStarted: Boolean
        get() = lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = Prefs.getsInstance()
        root = findViewById(android.R.id.content)
                setContentView(layoutId)
    }

    fun setActionBarTitle(title: String) {

        if (actionBar == null)
            actionBar = supportActionBar

        actionBar!!.title = title
    }

    fun toast(msg: Any?) {
        if (msg == null) {
            return
        }
        Toast.makeText(applicationContext, msg.toString(), Toast.LENGTH_SHORT).show()
    }

    fun toastLong(msg: Any?) {
        if (msg == null) return
        Toast.makeText(applicationContext, msg.toString(), Toast.LENGTH_LONG).show()
    }


    protected fun createAlertDialog(message: String): AlertDialog.Builder {
        return AlertDialog.Builder(this)
            .setMessage(message)
    }
}
