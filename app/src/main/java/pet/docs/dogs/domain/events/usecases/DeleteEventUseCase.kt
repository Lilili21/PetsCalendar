package pet.docs.dogs.domain.events.usecases

import pet.docs.dogs.data.eventsDb.EventDb
import pet.docs.dogs.domain.events.Event

class DeleteEventUseCase(private var userRepository: EventDb) {

    fun deleteEvent(event: Event?) : Boolean{
        if (event != null) {
            userRepository.deleteEvent(event.convertToWriteInDb())
            return true
        }
        return false
    }

    fun deleteEventById(id: Int) : Boolean{
        if (id != -1) {
            userRepository.deleteByEventId(id)
            return true
        }
        return false
    }
}