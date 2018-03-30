package ds.mathematik.uni_marburg.de.farrow.utils

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import java.util.concurrent.Executors

fun ioThread(f: () -> Unit) = Executors.newSingleThreadExecutor().execute(f)

inline fun <reified T : RoomDatabase> Context.roomDbBuilder(
    name: String
) = Room.databaseBuilder(this, T::class.java, name)

inline fun <reified T : RoomDatabase> Context.roomDb(
    name: String,
    crossinline onFirstCreate: () -> Unit = {}
) = roomDbBuilder<T>(name)
    .addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) = ioThread { onFirstCreate() }
    })
    .build()