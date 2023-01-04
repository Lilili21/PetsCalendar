package pet.docs.dogs.data.eventsDb

import androidx.room.*
import pet.docs.dogs.domain.events.EventElement

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(eventElement: EventElement)

    @Query("SELECT * FROM events_list")
    fun getAll(): List<EventElement>

    @Query("SELECT * FROM events_list WHERE regularity = :eRegularity")
    fun getEventsWithRegularity(eRegularity: Int): List<EventElement>

    @Query("SELECT * FROM events_list WHERE type = :eType")
    fun getEventsWithType(eType: Int): List<EventElement>

    @Delete
    fun delete(eventDb: EventElement)

    @Query("DELETE FROM events_list")
    fun deleteAll()

    //suspend - for coroutines


}