package com.example.routinechecks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.routinechecks.R
import com.example.routinechecks.database.Routines
import com.example.routinechecks.utils.Helper

/**
 * @property mContext is the context of the caller
 * @property mListener is the action callback listener
 * */
class RoutineListViewAdapter (private val mContext: Context, private val mListener: OnRoutineClickListener) :
    RecyclerView.Adapter<RoutineListViewAdapter.ViewHolder>() {

    private lateinit var mDataList: ArrayList<Routines>

    override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.routine_list, container, false))
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, viewType: Int) {
        val currentItem: Routines = viewHolder.currentItem()

        viewHolder.tvRoutineName?.text = currentItem.name
        viewHolder.tvRoutineDesc?.text = currentItem.desc
        viewHolder.tvRoutineTime?.text = Helper.getTimeStringFromDate(mContext, currentItem.date)
        viewHolder.tvRoutineFreq?.text = Helper.getFreqById(currentItem.freqId)?.label ?: ""
        viewHolder.cvContainer?.setOnClickListener { mListener.onRoutineClick(viewHolder.adapterPosition, currentItem) }
    }

    fun setData(data: ArrayList<Routines>?) {
        if (data == null)
            return

        mDataList = data
        notifyDataSetChanged()
    }

    /**
     * @property itemView is a container of a Routine card
     * @constructor creates a RecyclerView ViewHolder
     * */
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvRoutineName: TextView? = null
        var tvRoutineDesc: TextView? = null
        var tvRoutineTime: TextView? = null
        var tvRoutineFreq: TextView? = null
        var cvContainer: CardView? = null


        init {
            tvRoutineName = itemView.findViewById(R.id.tv_routine_name)
            tvRoutineDesc = itemView.findViewById(R.id.tv_routine_desc)
            tvRoutineTime = itemView.findViewById(R.id.tv_routine_time)
            tvRoutineFreq = itemView.findViewById(R.id.tv_routine_freq)
            cvContainer = itemView.findViewById(R.id.container)
        }

        fun currentItem(): Routines {
            return mDataList[adapterPosition]
        }
    }

    interface OnRoutineClickListener {
        fun onRoutineClick(position: Int, clickedRoutine: Routines)
    }
}
