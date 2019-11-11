package com.example.routinechecks.alarm

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.routinechecks.R
import com.example.routinechecks.activities.MainActivity
import com.example.routinechecks.database.AppDatabase
import com.example.routinechecks.database.RoutineOccurrence
import com.example.routinechecks.jobs.SimulatedJob
import com.example.routinechecks.notification.NotificationHelper
import com.example.routinechecks.notification.NotificationParam
import com.example.routinechecks.utils.Constants
import com.example.routinechecks.utils.Helper
import com.example.routinechecks.utils.ParcelableUtil
import com.example.routinechecks.utils.Prefs
import kotlinx.coroutines.*

/**
 * This is the Alarm trigger listener for the scheduled routine occurrences
 *
 * It coordinates the actions to be performed when a routine occurrence goes off
 *
 * @constructor creates an instance of the AlarmReceiver
 * */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (!intent.hasExtra(AlarmHelper.KEY_EXTRA_OCCURRENCE))
            return

        val occurrenceByte = intent.getByteArrayExtra(AlarmHelper.KEY_EXTRA_OCCURRENCE)
        val occurrence = ParcelableUtil.unmarshall(occurrenceByte, RoutineOccurrence.CREATOR)

        //trigger PUSH: clear previous ones first
        val param = NotificationParam()
        val notifHelper = NotificationHelper(context)
        param.setRemovePreviousNotif(true)
        param.title = context.getString(R.string.text_push_title)
        param.body = getNotifBody(context, occurrence)
        param.priorityType = NotificationCompat.PRIORITY_HIGH
        param.btnOneText = context.getString(R.string.text_push_btn_two_label)
        param.btnOnePendingIntent = getRoutineUpdatePendingIntent(context, occurrence)
        param.bodyPendingIntent = getLaunchAppPendingIntent(context, occurrence.alarmId)
        param.isAutoCancel = true
        notifHelper.notifyUser(param)

        GlobalScope.launch(Dispatchers.Default) {
            updateOccurrence(context, occurrence)
        }
    }

    private suspend fun updateOccurrence(context: Context, occurrence: RoutineOccurrence) = coroutineScope {
        withContext(Dispatchers.IO) {

            //update Routines's Status and update the database
            val mDb = AppDatabase.getInstance(context)
            val currentOccurrence: RoutineOccurrence? = mDb?.dao?.getRoutineOccurrenceByAlarmId(occurrence.alarmId)

            //update Routines's Status and update the database
            if (currentOccurrence?.status == Constants.Status.UNKNOWN.id) {
                //If the user hasn't changed the status, toggle it
                currentOccurrence.status = Constants.Status.PROGRESS.id
                mDb.dao.updateOccurrence(currentOccurrence)
            }

            delay(Constants.MINIMUM_NOTIF_TIME_TO_START_TIME_MILLIS.toLong())

            //schedule for the next routine
            val nextOccurrencePeriod = Helper.computeNextRoutineTime(currentOccurrence!!.freqId, currentOccurrence.occurrenceDate)
            val nextOccurrence = RoutineOccurrence(currentOccurrence.routineId, Constants.Status.UNKNOWN.id,
                nextOccurrencePeriod, Prefs.getsInstance().nextAlarmId, currentOccurrence.name, currentOccurrence.desc, currentOccurrence.freqId)

            //schedule next routine
            AlarmHelper().execute(nextOccurrence, AlarmHelper.ACTION_SCHEDULE_ALARM)


            //start Simulated Job
            withContext(Dispatchers.Default) {
                SimulatedJob(context, currentOccurrence).runJob()
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun getNotifBody(context: Context, occurrence: RoutineOccurrence): String {
        return context.getString(R.string.notif_body, occurrence.name, occurrence.desc,
            Helper.getTimeStringFromDate(context, occurrence.occurrenceDate))
    }

    private fun getRoutineUpdatePendingIntent(context: Context, occurrence: RoutineOccurrence): PendingIntent {
        val intent = Intent(context, NotificationActionService::class.java)
        intent.action = NotificationActionService.ACTION_UPDATE_ROUTINE
        val occurrenceByte = ParcelableUtil.marshall(occurrence)
        intent.putExtra(AlarmHelper.KEY_EXTRA_OCCURRENCE, occurrenceByte)
        return PendingIntent.getService(context, occurrence.alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getLaunchAppPendingIntent(context: Context, alarmId: Int): PendingIntent {
        return PendingIntent.getActivity(context, alarmId, Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)
    }

    companion object {
        const val EXTRA_KEY_ALARM_RECEIVER_ACTION = "key_update_alarm"
        const val ALARM_ACTION_UPDATE_ALARM = "action_update_alarm"
    }
}
