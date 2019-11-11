package com.example.routinechecks.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.TimePicker
import com.example.routinechecks.R
import com.example.routinechecks.custom.CustomArraySpinner
import com.example.routinechecks.database.Routines
import com.example.routinechecks.utils.Constants
import com.example.routinechecks.utils.Frequency
import kotlinx.android.synthetic.main.dialog_add_routine.*
import java.io.Serializable
import java.util.*

class RoutineDialogFragment : BaseDialogFragment(), TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener {
    private lateinit var mListener: OnSaveOccurrence
    private var mRoutine: Routines? = null
    private var isEdit: Boolean = false
    private var is24Hour: Boolean = false
    private var selectedFrequencyIndex: Int = 0
    private var selectedRoutineTime: Calendar = Calendar.getInstance()
    private var isTimeSelected: Boolean = false
    private var titleText: String? = null
    private var descText: String? = null
    private lateinit var mTimePicker: TimePickerDialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.dialog_add_routine, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        if (savedInstanceState != null) {
            selectedRoutineTime.timeInMillis = savedInstanceState.getLong(Constants.ARG_TIME)
            titleText = savedInstanceState.getString(Constants.ARG_ROUTINE_TITLE)
            descText = savedInstanceState.getString(Constants.ARG_ROUTINE_DESC)
            selectedRoutineTime.time = Date(savedInstanceState.getLong(Constants.ARG_TIME))
            isTimeSelected = savedInstanceState.getBoolean(Constants.ARG_IS_TIME_SELECTED, false)
            selectedFrequencyIndex = savedInstanceState.getInt(Constants.ARG_ROUTINE_PRIORITY)
            mListener = savedInstanceState.getSerializable(Constants.ARG_LISTENER) as OnSaveOccurrence

            if (isEdit)
                mRoutine = savedInstanceState.getParcelable<Parcelable>(Constants.ARG_CURRENT_ROUTINE) as Routines
        }

        val dialog = super.onCreateDialog(savedInstanceState)

        //get device width and height
        val metrics = resources.displayMetrics
        val deviceWidth = metrics.widthPixels
        val deviceHeight = metrics.heightPixels

        val width = (deviceWidth * 0.9).toInt()
        val height: Int = (deviceHeight * 0.6).toInt()

        val window = dialog.window
        if (window != null) {

            //remove window title
            window.requestFeature(Window.FEATURE_NO_TITLE)

            //set Width and Height
            dialog.setContentView(R.layout.dialog_add_routine)
            window.setLayout(width, height)

            //set window layout
            dialog.setContentView(R.layout.dialog_add_routine)
        }

        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
    }

    private fun initializeViews() {

        if (isEdit) {
            val cal = Calendar.getInstance()
            cal.time = mRoutine?.date
            var hr = if (is24Hour) cal.get(Calendar.HOUR_OF_DAY) else cal.get(Calendar.HOUR)
            if (!is24Hour && cal.get(Calendar.AM_PM) == Calendar.PM)
                hr += 12
            val min = cal.get(Calendar.MINUTE)

            selectedRoutineTime = Calendar.getInstance()
            selectedRoutineTime.time = mRoutine?.date

            et_routine_title?.setText(mRoutine?.name)
            et_routine_desc?.setText(mRoutine?.desc)
            tv_routine_start_time?.text = getTimeText(is24Hour, hr, min)
            selectedFrequencyIndex = mRoutine?.freqId?.plus(1) ?: 0
            tv_header_text.text = appCompatActivity.getString(R.string.text_edit_routine)
            btn_add_another_routine.visibility = View.GONE
            btn_add_routine.text = appCompatActivity.getString(R.string.text_done)
            isTimeSelected = true
        }

        val freqList = ArrayList<String>()
        freqList.add(appCompatActivity.getString(R.string.text_select_a_frequency))
        freqList.add(Frequency.HOURLY.label)
        freqList.add(Frequency.DAILY.label)
        freqList.add(Frequency.WEEKLY.label)
        freqList.add(Frequency.MONTHLY.label)
        freqList.add(Frequency.YEARLY.label)

        val spinnerArrayAdapter = CustomArraySpinner(appCompatActivity, R.layout.spinner_selected_item, freqList)
        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down)
        spinnerArrayAdapter.setSelectedItemColor(R.color.black)
        spinnerArrayAdapter.setUnselectableItemColor(R.color.black)
        spinnerArrayAdapter.setUnselectablePosition(0)
        spinnerArrayAdapter.setSelectedItemTextSize(16f)
        spinner_routine_freq.adapter = spinnerArrayAdapter
        spinner_routine_freq.prompt = appCompatActivity.getString(R.string.text_select_a_frequency)
        spinner_routine_freq.setSelection(selectedFrequencyIndex)
        spinner_routine_freq.onItemSelectedListener = this@RoutineDialogFragment

        if (!TextUtils.isEmpty(titleText))
            et_routine_title.setText(titleText)

        if (!TextUtils.isEmpty(descText))
            et_routine_desc.setText(descText)

        tv_routine_start_time.setOnClickListener {
            val hour = if (is24Hour) selectedRoutineTime.get(Calendar.HOUR_OF_DAY) else selectedRoutineTime.get(Calendar.HOUR)

            mTimePicker = TimePickerDialog(appCompatActivity, this, hour,
                selectedRoutineTime.get(Calendar.MINUTE), is24Hour)

            mTimePicker.show()
        }

        if (isTimeSelected)
            tv_routine_start_time.text = getTimeText(is24Hour, selectedRoutineTime.get(Calendar.HOUR_OF_DAY), selectedRoutineTime.get(Calendar.MINUTE))

        btn_add_routine.setOnClickListener { saveRoutine(true) }

        btn_add_another_routine.setOnClickListener { saveRoutine(false) }
    }

    companion object {
        fun newInstance(routines: Routines, listener: OnSaveOccurrence, isEdit: Boolean): RoutineDialogFragment {
            val args = Bundle()
            val fragment = RoutineDialogFragment()
            fragment.mRoutine = routines
            fragment.mListener = listener
            fragment.isEdit = isEdit
            fragment.arguments = args
            return fragment
        }

        fun newInstance(listener: OnSaveOccurrence, isEdit: Boolean): RoutineDialogFragment {
            val args = Bundle()
            val fragment = RoutineDialogFragment()
            fragment.mListener = listener
            fragment.isEdit = isEdit
            fragment.arguments = args
            return fragment
        }
    }

    private fun resetAllViews() {
        et_routine_title.setText("")
        et_routine_desc.setText("")
        tv_routine_start_time.text = ""
        spinner_routine_freq.setSelection(0)

        if (isEdit)
            selectedRoutineTime.time = mRoutine!!.date
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        isTimeSelected = true
        if (is24Hour) {
            selectedRoutineTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedRoutineTime.set(Calendar.MINUTE, minute)
        } else {
            selectedRoutineTime.set(Calendar.HOUR, hourOfDay % 12)
            selectedRoutineTime.set(Calendar.MINUTE, minute)
            selectedRoutineTime.set(Calendar.AM_PM, if (hourOfDay >= 12) Calendar.PM else Calendar.AM)
        }
        tv_routine_start_time.text = getTimeText(is24Hour, hourOfDay, minute)
    }

    private fun getTimeText(is24Hour: Boolean, hourOfDay: Int, minute: Int): String {
        return if (is24Hour)
            String.format(Locale.getDefault(), "%02d", hourOfDay) + ":" + minute.toString()
        else
            (if (hourOfDay % 12 == 0) 12 else hourOfDay % 12).toString() + ":" + java.lang.String.format(
                Locale.getDefault(),
                "%02d",
                minute
            ) + if (hourOfDay >= 12) " PM" else " AM"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        selectedFrequencyIndex = position - 1
    }

    private fun getRoutineObject(): Routines? {

        titleText = et_routine_title.text.toString()

        if (TextUtils.isEmpty(titleText)) {
            et_routine_title.error = "Title cannot be empty"
            return null
        }

        descText = et_routine_desc.text.toString()
        if (TextUtils.isEmpty(descText)) {
            et_routine_desc.error = "Description cannot be empty"
            return null
        }

        if (!isTimeSelected) {
            toast("Select time")
            return null
        }

        if (selectedFrequencyIndex < 0) {
            toast("Select a frequency")
            return null
        }

        if (isEdit) {
            mRoutine?.name = titleText
            mRoutine?.desc = descText
            mRoutine?.freqId = selectedFrequencyIndex
            mRoutine?.date = selectedRoutineTime.time
            return mRoutine
        } else {
            return Routines(titleText, descText, selectedFrequencyIndex, selectedRoutineTime.time, selectedRoutineTime.time)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(Constants.ARG_LISTENER, mListener)
        outState.putInt(Constants.ARG_ROUTINE_PRIORITY, selectedFrequencyIndex)

        titleText = et_routine_title.text.toString()
        descText = et_routine_desc.text.toString()

        if (!TextUtils.isEmpty(titleText))
            outState.putString(Constants.ARG_ROUTINE_TITLE, titleText)

        if (!TextUtils.isEmpty(descText))
            outState.putString(Constants.ARG_ROUTINE_DESC, descText)

        if (mRoutine != null)
            outState.putParcelable(Constants.ARG_CURRENT_ROUTINE, mRoutine)

        if (isTimeSelected) {
            outState.putLong(Constants.ARG_TIME, selectedRoutineTime.timeInMillis)
            outState.putBoolean(Constants.ARG_IS_TIME_SELECTED, true)
        }
    }

    interface OnSaveOccurrence : Serializable {
        fun onSaveRoutine(routines: Routines, isEdit: Boolean)
    }

    private fun saveRoutine(dismissDialog: Boolean) {

        val routine = getRoutineObject() ?: return

        mListener.onSaveRoutine(routine, isEdit)

        if (dismissDialog) {
            if (isAdded)
                dismiss()
        } else {
            resetAllViews()
        }
    }
}