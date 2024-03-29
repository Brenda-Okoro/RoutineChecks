package com.example.routinechecks.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.*

@Entity
class Routines() : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    var name: String? = null

    var desc: String? = null

    var freqId: Int = 0

    @ColumnInfo(name = "routine_time")
    var date: Date? = null

    @ColumnInfo(name = "last_routine_date")
    var nextRoutineDate: Date? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readInt()
        name = parcel.readString()
        desc = parcel.readString()
        freqId = parcel.readInt()
        date = Date(parcel.readLong())
        nextRoutineDate = Date(parcel.readLong())
    }

    constructor(id: Int, name: String?, desc: String?, freqId: Int, date: Date?, lastRoutineDate: Date?) : this() {
        this.id = id
        this.name = name
        this.desc = desc
        this.freqId = freqId
        this.date = date
        this.nextRoutineDate = lastRoutineDate
    }

    @Ignore
    constructor(name: String?, desc: String?, freqId: Int, date: Date?, lastRoutineDate: Date?) : this() {
        this.name = name
        this.desc = desc
        this.freqId = freqId
        this.date = date
        this.nextRoutineDate = lastRoutineDate
    }

    @Ignore
    constructor(name: String?, desc: String?, freqId: Int, date: Date?) : this() {
        this.name = name
        this.desc = desc
        this.freqId = freqId
        this.date = date
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(desc)
        parcel.writeInt(freqId)
        parcel.writeLong(date?.time ?: 0)
        parcel.writeLong(nextRoutineDate?.time ?: 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Routines> {
        override fun createFromParcel(parcel: Parcel): Routines {
            return Routines(parcel)
        }

        override fun newArray(size: Int): Array<Routines?> {
            return arrayOfNulls(size)
        }
    }

}
