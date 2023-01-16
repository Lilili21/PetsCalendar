package pet.docs.dogs.data.eventsDb

import androidx.room.*
import pet.docs.dogs.domain.events.EventElement

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(eventElement: EventElement)

    @Update
    fun update(eventElement: EventElement)

    @Query("SELECT * FROM events_list")
    fun getAll(): List<EventElement>

    @Query("SELECT * FROM events_list WHERE regularity = :eRegularity")
    fun getEventsWithRegularity(eRegularity: Int): List<EventElement>

    @Query("SELECT * FROM events_list WHERE type = :eType")
    fun getEventsWithType(eType: Int): List<EventElement>

    @Query("SELECT * FROM events_list WHERE id = :id")
    fun getEventById(id: Int): EventElement

    @Delete
    fun delete(eventDb: EventElement)

    @Query("DELETE FROM events_list")
    fun deleteAll()

    @Query("DELETE FROM events_list WHERE id = :id")
    fun deleteByEventId(id: Int)

    @Query("SELECT EXISTS(SELECT * FROM events_list WHERE id = :id)")
    fun isExists(id: Int): Boolean

    //suspend - for coroutines


}