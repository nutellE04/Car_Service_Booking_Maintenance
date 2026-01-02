// File: app/src/main/java/com/group10/carservicebook/data/DatabaseModule.kt
package com.group10.carservicebook.data

import android.content.Context
import android.content.SharedPreferences
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "service_records")
data class ServiceRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serviceType: String,
    val mileageAtService: Int,
    val cost: Double,
    val date: Long,
    val notes: String?
)

@Dao
interface ServiceDao {
    @Query("SELECT * FROM service_records ORDER BY date DESC")
    fun getAllServices(): Flow<List<ServiceRecord>>

    @Query("SELECT * FROM service_records ORDER BY mileageAtService DESC LIMIT 1")
    suspend fun getLastService(): ServiceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertService(service: ServiceRecord)

    // ADDED: Explicit update method
    @Update
    suspend fun updateService(service: ServiceRecord)

    @Delete
    suspend fun deleteService(service: ServiceRecord)
}

@Database(entities = [ServiceRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "car_service_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class PrefsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("car_prefs", Context.MODE_PRIVATE)

    fun saveCurrentOdometer(km: Int) {
        prefs.edit().putInt("current_odometer", km).apply()
    }

    fun getCurrentOdometer(): Int {
        return prefs.getInt("current_odometer", 0)
    }

    fun saveServiceInterval(interval: Int) {
        prefs.edit().putInt("service_interval", interval).apply()
    }

    fun getServiceInterval(): Int {
        return prefs.getInt("service_interval", 5000)
    }
}