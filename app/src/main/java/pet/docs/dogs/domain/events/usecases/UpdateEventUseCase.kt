package pet.docs.dogs.domain.events.usecases

import pet.docs.dogs.data.eventsDb.EventDb
import pet.docs.dogs.domain.events.Event

class UpdateEventUseCase(private var userRepository: EventDb)  {

    fun execute(event: Event?) : Boolean{
        if (event != null) {
            userRepository.updateEvent(event.convertToWriteInDb())
            return true
        }
        return false
    }
}