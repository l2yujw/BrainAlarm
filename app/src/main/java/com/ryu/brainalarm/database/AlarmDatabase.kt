package com.ryu.brainalarm.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File
import java.util.concurrent.Executors


@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {
    abstract fun getAlarmDao() : AlarmDao

    companion object{
        private const val TAG = "AlarmDatabase"
        const val VERSION = 1
        private const val DATABASE_NAME = "alarm_database.db"

        //인자 개수만큼의 고정된 스레드 풀을 생성 (alarmRepository 에서의 활용 수를 기준으로 생성했음)
        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        //다른 thread에서 접근 가능하게 만드는 것
        @Volatile
        private var instance : AlarmDatabase? = null

        fun getInstance(context : Context) : AlarmDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also{ instance = it}
            }

        private fun buildDatabase(context : Context) : AlarmDatabase{
            ///storage/sdcard0/Android/data/package/files
            val filesDir = context.getExternalFilesDir(null)
            val dataDir = File(filesDir, "data")

            //디렉터리가 없을시 생성
            if(!dataDir.exists())
                dataDir.mkdir()

            //DB생성 ( 경로 누락시 기존 데이터를 잃을 수도 있음 )
            val builder = Room.databaseBuilder(
                context,
                AlarmDatabase::class.java,
                File(dataDir, DATABASE_NAME).toString()
            ).fallbackToDestructiveMigration()

            return builder.build()
        }

        // synchronized는 데이터베이스를 instance화 시킴
        fun getDatabase(context: Context): AlarmDatabase? {
            if (instance == null) {
                synchronized(AlarmDatabase::class.java) {
                    if (instance == null) {
                        instance = Room.databaseBuilder(
                            context.applicationContext,
                            AlarmDatabase::class.java,
                            DATABASE_NAME
                        ).build()
                    }
                }
            }
            return instance
        }
    }
}