package com.example.routinechecks.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.IdRes
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.routinechecks.BaseActivity
import com.example.routinechecks.R
import com.example.routinechecks.adapters.RoutineListViewAdapter
import com.example.routinechecks.alarm.AlarmHelper
import com.example.routinechecks.custom.AppViewHolder
import com.example.routinechecks.dagger.ViewModelModule
import com.example.routinechecks.database.RoutineOccurrence
import com.example.routinechecks.database.Routines
import com.example.routinechecks.fragments.RoutineDialogFragment
import com.example.routinechecks.model.NextRoutine
import com.example.routinechecks.utils.Constants
import com.example.routinechecks.utils.Frequency
import com.example.routinechecks.utils.Helper
import com.example.routinechecks.utils.Prefs
import com.example.routinechecks.viewmodels.BaseViewModel
import com.example.routinechecks.viewmodels.RoutineListViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*


class MainActivity : BaseActivity(R.layout.activity_main), RoutineListViewAdapter.OnRoutineClickListener,
    RoutineDialogFragment.OnSaveOccurrence {
    override val TAG = "MainActivity"

    inner class ViewHolder(private val mContainer: View) : AppViewHolder(mContainer) {

        private lateinit var adapter: RoutineListViewAdapter
        private var mState: State

        init {
            mState = State()
            init(mContainer)
        }

        @SuppressLint("WrongConstant")
        override fun init(container: View) {
            //setup FAB
            fab.setOnClickListener {
                openAddRoutineDialog()
            }

            //setup RecyclerView
            adapter = RoutineListViewAdapter(this@MainActivity, this@MainActivity)
            rv_rountine_list?.layoutManager = LinearLayoutManager(
                this@MainActivity,
                LinearLayoutManager.VERTICAL, false
            )
            rv_rountine_list?.setHasFixedSize(true)
            rv_rountine_list?.isFocusable = true
            rv_rountine_list?.clipToPadding = false
            rv_rountine_list?.adapter = adapter
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    deleteRoutine(viewHolder.adapterPosition)
                }
            }).attachToRecyclerView(rv_rountine_list)
        }

        fun refreshPage(routineList: List<Routines>) {

            addOrRemoveMenuOption(model.mNextUpList.isNotEmpty(), R.id.action_next_up)

            if (routineList.isEmpty()) {
                mState.setNoData()
            } else {
                adapter.setData(routineList as java.util.ArrayList<Routines>)
                mState.setHasData()
            }
        }

        fun switchScreenState(state: Int) {
            when (state) {
                BaseViewModel.STATE_LOADING -> mState.setLoading()
                BaseViewModel.STATE_LOADED -> mState.setLoaded()
            }
        }

        private fun addOrRemoveMenuOption(isVisible: Boolean, @IdRes menuId: Int) {
            if (mMenu == null)
                return

            val option = mMenu?.findItem(menuId) ?: return

            option.isVisible = isVisible
        }

        inner class State : AbsViewState {
            override fun setBlank() {

            }

            override fun setNoData() {
                show(tv_empty_state)
                gone(rv_rountine_list)
                gone(loading)
            }

            override fun setHasData() {
                show(rv_rountine_list)
                gone(tv_empty_state)
                gone(loading)
            }

            override fun setLoading(tag: String) {
                show(loading)
                gone(tv_empty_state)
                gone(rv_rountine_list)
            }

            override fun setLoaded(tag: String) {

            }

            override fun setLoading() {

            }

            override fun setLoaded() {

            }
        }
    }

    /**
     * @constructor creates an instance of the model
     * */
    inner class Model {

        private var mActivity: MainActivity = this@MainActivity
        private var mViewModel: RoutineListViewModel = ViewModelModule().getRoutineListViewModel(
            mActivity.application, mActivity
        ) as RoutineListViewModel

        private var mRoutineList: List<Routines>? = null
        lateinit var mNextUpList: ArrayList<NextRoutine>

        init {

            //observe for Routine List data
            mViewModel.getRoutineListObserver().observe(mActivity, Observer {
                if (it == null)
                    return@Observer

                mRoutineList = it
                mNextUpList = getNextUpRoutineList(mRoutineList!!)
                refreshPage(it)

                if (isAddition) {
                    isAddition = false
                    val addedRoutine = it[it.size - 1]
                    var date = addedRoutine.date

                    if (addedRoutine.freqId != Frequency.HOURLY.id)
                        date = Helper.computeNextRoutineTime(addedRoutine.freqId, addedRoutine.date)

                    val occurrence = RoutineOccurrence(
                        addedRoutine.id, Constants.Status.UNKNOWN.id, date,
                        Prefs.getsInstance().nextAlarmId, addedRoutine.name, addedRoutine.desc, addedRoutine.freqId
                    )
                    AlarmHelper().execute(occurrence, AlarmHelper.ACTION_SCHEDULE_ALARM)
                }
            })

            //Observe for UI state change
            mViewModel.getUiStateObserver().observe(mActivity, Observer {
                if (it == null)
                    return@Observer

                switchScreenState(it)
            })

            //Query for data
            mViewModel.getAllRoutines()
        }

        fun saveRoutine(routines: Routines) {
            mViewModel.addRoutine(routines)
        }

        fun getCurrentRoutineList(): List<Routines> {
            return mRoutineList!!
        }

        fun deleteRoutine(index: Int) {
            mViewModel.deleteRoutine(mRoutineList!![index])
        }
    }

    private lateinit var views: ViewHolder
    private lateinit var model: Model
    private var mMenu: Menu? = null
    private var isAddition: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ViewHolder(window.decorView.findViewById(android.R.id.content))
        model = Model()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.menu_routine_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {

            R.id.action_add_routine -> {
                openAddRoutineDialog()
                true
            }

            R.id.action_next_up -> {
                openNextUpActivity()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openAddRoutineDialog() {
        RoutineDialogFragment.newInstance(this, false).show(
            this.fragmentManager,
            RoutineDialogFragment::class.java.name
        )
    }
    private fun openNextUpActivity() {
//        val intent = Intent(this, NextUpActivity::class.java)
//        intent.putParcelableArrayListExtra(NextUpActivity.EXTRA_NEXT_UP_ROUTINE_LIST, model.mNextUpList)
//        startActivity(intent)
    }

    private fun switchScreenState(state: Int) {
        views.switchScreenState(state)
    }

    private fun refreshPage(routineList: List<Routines>) {
        views.refreshPage(routineList)
    }

    private fun deleteRoutine(index: Int) {
        createAlertDialog(getString(R.string.text_delete_routine))
            .setNegativeButton(getString(R.string.alert_dialog_negative_button_label)) { dialog, which ->
                refreshPage(
                    model.getCurrentRoutineList()
                )
            }
            .setPositiveButton(getString(R.string.alert_dialog_positive_button_label)) { dialog, which ->
                model.deleteRoutine(index)
            }.create().show()
    }

    fun getNextUpRoutineList(routineList: List<Routines>): ArrayList<NextRoutine> {
        val list: ArrayList<NextRoutine> = ArrayList()

        for (routines: Routines in routineList) {
            val nextTime = routines.date!!.time
            val now = Calendar.getInstance().timeInMillis
            val diff = nextTime - now
            if (diff > 0 && diff <= Constants.TWELVE_HOURS_IN_MILLIS) {
                list.add(NextRoutine(routines.name, Helper.getUpNext(routines.freqId, routines.date)))
            }
        }

        return list
    }
}
