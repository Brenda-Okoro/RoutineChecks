package com.example.routinechecks.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.example.routinechecks.R
import com.example.routinechecks.database.AppDatabase
import com.example.routinechecks.database.RoutineOccurrence
import com.example.routinechecks.database.Routines
import com.example.routinechecks.utils.App
import com.example.routinechecks.utils.Constants
import com.example.routinechecks.utils.Helper
import com.example.routinechecks.utils.ParcelableUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 *
 * This class handles the scheduling (creating, removing and updating) of routine occurrence instances.
 *
 * It is trigger when a new routine is added to the Routine List
 *
 * @constructor creates an instance of the AlarmHelper
 *
 * */
class AlarmHelper {

    private var mAlarmManager: AlarmManager? = null
    private var mContext: Context? = null

    init {
        if (mContext == null)
            mContext = App.getsInstance()?.appContext

        if (mAlarmManager == null)
            mAlarmManager = App.getsInstance()?.appContext
                ?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    /**
     * @param occurrence is an instance of the Routine next to be scheduled/removed/updated
     * @param action is the unique identifier for the operation to be performed on the routine occurrence
     * */
    fun execute(occurrence: RoutineOccurrence?, action: Int) {
        GlobalScope.launch(Dispatchers.Default) {
            withContext(Dispatchers.IO) {
                if (action < 0)
                    throw IllegalArgumentException(mContext?.getString(R.string.text_invalid_action_identifier))

                when (action) {
                    ACTION_SCHEDULE_ALARM -> schedule(occurrence, false)

                    ACTION_UPDATE_ALARM -> update(occurrence)

                    ACTION_DELETE_ALARM -> delete(occurrence)
                }
            }
        }
    }

    private fun delete(occurrence: RoutineOccurrence?) {
        val pendingIntent = getPendingIntentFor(occurrence, false)
        mAlarmManager?.cancel(pendingIntent)
    }

    private fun update(occurrence: RoutineOccurrence?) {
        //delete the previously scheduled alarm for this occurrence
        delete(occurrence)

        //re-schedule
        schedule(occurrence, true)
    }

    private fun schedule(occurrence: RoutineOccurrence?, isUpdate: Boolean) {

        if (Helper.hasTimePassed(occurrence?.occurrenceDate!!)) {
            occurrence.occurrenceDate = Helper.computeNextRoutineTime(occurrence.freqId, occurrence.occurrenceDate)
        }

        val pendingIntent = getPendingIntentFor(occurrence, isUpdate)
        val alarmTime = (occurrence.occurrenceDate!!.time - Constants.MINIMUM_NOTIF_TIME_TO_START_TIME_MILLIS)
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> mAlarmManager?.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> mAlarmManager?.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
            else -> mAlarmManager?.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent)
        }

        //create the Occurrence
        val mDb = AppDatabase.getInstance(mContext!!)
        mDb?.dao?.addOccurrence(occurrence)

        //Update the routine date
        val routineInstance: Routines = mDb?.dao!!.getRoutineByIdAsync(occurrence.routineId)
        routineInstance.date = occurrence.occurrenceDate

        mDb.dao.updateRoutine(routineInstance)
    }

    private fun getPendingIntentFor(occurrence: RoutineOccurrence?, isUpdate: Boolean): PendingIntent {
        val alarmIntent = Intent(mContext, AlarmReceiver::class.java)
        val occurrenceByte = ParcelableUtil.marshall(occurrence!!)
        alarmIntent.putExtra(KEY_EXTRA_OCCURRENCE, occurrenceByte)
        alarmIntent.data = Uri.parse(mContext?.getString(R.string.prefix_text) + occurrence.alarmId)
        alarmIntent.action = occurrence.alarmId.toString()

        if (isUpdate) {
            alarmIntent.putExtra(AlarmReceiver.EXTRA_KEY_ALARM_RECEIVER_ACTION, AlarmReceiver.ALARM_ACTION_UPDATE_ALARM)
        }

        return PendingIntent.getBroadcast(mContext, occurrence.alarmId, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {

        const val ACTION_SCHEDULE_ALARM = 0
        const val ACTION_UPDATE_ALARM = 1
        const val ACTION_DELETE_ALARM = 2
        const val KEY_EXTRA_OCCURRENCE = "key_extra_occurrence"
    }
}
