package pet.docs.dogs.data.eventsDb

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {

    @Query("SELECT * FROM events_list")
    fun getAll(): LiveData<List<EventElement>>

    @Query("SELECT * FROM events_list WHERE regularity = :eRegularity")
    fun getEventsWithRegularity(eRegularity: Int): LiveData<List<EventElement>>

    @Query("SELECT * FROM events_list WHERE type = :eType")
    fun getEventsWithType(eType: Int): LiveData<List<EventElement>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(eventDb: EventElement)

    @Delete
    suspend fun delete(eventDb: EventElement)

    @Query("DELETE FROM events_list")
    suspend fun deleteAll()

    //suspend - for coroutines


}