package pet.docs.dogs.domain.events

import pet.docs.dogs.data.eventsDb.EventDb

class SaveEventUseCase(private var userRepository: EventDb) {

    fun execute(event: Event?) : Boolean{
        if (event != null) {
            userRepository.addEvent(event.convertToWriteInDb())
            return true
        }
        return false
    }
}
