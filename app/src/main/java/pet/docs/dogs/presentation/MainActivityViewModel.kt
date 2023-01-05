package pet.docs.dogs.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import pet.docs.dogs.data.animalInfo.DogInformationStorageImpl
import pet.docs.dogs.data.eventsDb.EventDb
import pet.docs.dogs.domain.animals.GetDogInfoUseCase
import pet.docs.dogs.domain.animals.IsDogExistUseCase
import pet.docs.dogs.domain.events.GetEventUseCase

private const val TAG = "MAIN_ACTIVITY"

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository : DogInformationStorageImpl
   // private val eventDb: EventDb

    init {
        userRepository = DogInformationStorageImpl(application)
       // eventDb = EventDb(application)
        Log.d(TAG, "Main Activity View Model created")
    }

    fun needToChangeTitle(): Boolean{
        val dogExistInStorage = IsDogExistUseCase(userRepository)
        if (dogExistInStorage.execute()) {
            val name = GetDogInfoUseCase(userRepository).getDogName()
            if (name.isNotEmpty())
                return true
        }
        return false
    }

    fun dogName(): String{
        return GetDogInfoUseCase(userRepository).getDogName()
    }

    fun isDogExist(): Boolean {
        return IsDogExistUseCase(userRepository).execute()
    }



    /*fun getEventsWithRegularity(eRegularity: Int) : List<EventElement> {
        var eventElementList : List<EventElement> = listOf<EventElement>()
        viewModelScope.launch(Dispatchers.IO) {
            eventElementList = repository.getEventsWithRegularity(eRegularity)
        }
        return eventElementList
    }*/


   /* override fun onCleared() {
        Log.d(TAG, "Main Activity View Model cleared")
        super.onCleared()
    }*/

    /*private val getDogInfoUseCase = GetDogInfoUseCase()
    private val getEventUseCase = GetEventUseCase()*/

}