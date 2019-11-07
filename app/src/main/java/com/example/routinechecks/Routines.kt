package com.example.routinechecks

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Routines(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val userId: Int,
    val fingerPosition: String

) {
    @Ignore
    constructor(
        userId: Int,
        fingerPosition: String

    ) : this(
        0, userId, fingerPosition
    )
}