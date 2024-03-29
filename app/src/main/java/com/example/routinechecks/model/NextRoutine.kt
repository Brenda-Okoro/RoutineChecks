package com.example.routinechecks.model

import android.os.Parcel
import android.os.Parcelable

/**
 *
 * @property name is the name of the Routine
 *
 * @property estimate is the estimated elapsed time (in String) of the next occurrence of the Routine
 *
 * @constructor creates an instance of the NextUpRoutine
 *
 * */
class NextRoutine() : Parcelable {

    var name: String? = null
    var estimate: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        estimate = parcel.readString()
    }

    constructor(name: String?, estimate: String?) : this() {
        this.name = name
        this.estimate = estimate
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(estimate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<NextRoutine> {
        override fun createFromParcel(parcel: Parcel): NextRoutine {
            return NextRoutine(parcel)
        }

        override fun newArray(size: Int): Array<NextRoutine?> {
            return arrayOfNulls(size)
        }
    }
}