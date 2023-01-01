package pet.docs.dogs.data.eventsDb

import androidx.lifecycle.LiveData

class EventRepository(private val eventDao : EventDao) {

    val getAll: LiveData<List<EventElement>> = eventDao.getAll()

    suspend fun insert(eventDb: EventElement) {
        eventDao.insert(eventDb)
    }

    fun getEventsWithRegularity(eRegularity: Int) : LiveData<List<EventElement>> {
        return eventDao.getEventsWithRegularity(eRegularity)
    }

}