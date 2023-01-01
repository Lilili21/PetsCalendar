package pet.docs.dogs.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pet.docs.dogs.data.eventsDb.AppDatabase
import pet.docs.dogs.data.eventsDb.EventElement
import pet.docs.dogs.data.eventsDb.EventRepository
import java.time.LocalDate

private const val TAG = "MAIN_ACTIVITY"

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    val getAll: LiveData<List<EventElement>>
    private val repository: EventRepository

    init {
        val eventDao = AppDatabase.getDatabase(application).eventDao()
        repository = EventRepository(eventDao)
        getAll = repository.getAll
        Log.d(TAG, "Main Activity View Model created")
    }

    fun insert(eventDb: EventElement){
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(eventDb)
        }
    }

    fun getOnDate(currentDate: LocalDate){


    }

    /*fun getEventsWithRegularity(eRegularity: Int) : List<EventElement> {
        var eventElementList : List<EventElement> = listOf<EventElement>()
        viewModelScope.launch(Dispatchers.IO) {
            eventElementList = repository.getEventsWithRegularity(eRegularity)
        }
        return eventElementList
    }*/


    override fun onCleared() {
        Log.d(TAG, "Main Activity View Model cleared")
        super.onCleared()
    }

    /*private val getDogInfoUseCase = GetDogInfoUseCase()
    private val getEventUseCase = GetEventUseCase()*/

}