package com.example.routinechecks.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.routinechecks.BaseActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity(com.example.routinechecks.R.layout.activity_main) {
    override val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
//            if (savedInstanceState == null) {
//                val newFragment = DialogFragment()
//                val ft = fragmentManager.beginTransaction()
//                ft.add(this, newFragment).commit()
//            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(com.example.routinechecks.R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            com.example.routinechecks.R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
