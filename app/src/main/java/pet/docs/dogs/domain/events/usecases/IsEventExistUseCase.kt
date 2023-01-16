package pet.docs.dogs.domain.events.usecases

import pet.docs.dogs.data.eventsDb.EventDb
import pet.docs.dogs.domain.events.Event

class IsEventExistUseCase(private var userRepository: EventDb)  {

    fun execute(id: Int) : Boolean{
        return if (id == -1)
            false
        else {
            userRepository.isExists(id)
        }
    }
}