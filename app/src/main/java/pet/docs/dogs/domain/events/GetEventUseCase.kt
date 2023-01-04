package pet.docs.dogs.domain.events

import android.content.Context
import pet.docs.dogs.data.eventsDb.EventDb
import java.time.LocalDate
import java.time.YearMonth
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class GetEventUseCase(private var userRepository: EventDb, private var context : Context) {

    private fun getAllEventsFromDb() : List<Event>{
        val eventElementList = userRepository.getAll()
        val listOfEvents = mutableListOf<Event>()
        for (eventElement in eventElementList){
            listOfEvents.add(Event(eventElement))
        }
        return listOfEvents
    }
 
    fun getEvents(month: Int, year: Int) : List<HashMap<String, String>> {
        val eventElementList = getAllEventsFromDb()
        var listOfEvents : MutableList<HashMap<String, String>> = mutableListOf()
        for (eventElement in eventElementList) {
            if (eventElement.relevantInCurrentMonth(month)) {
                listOfEvents.add(eventElement.convertParamsOfEventToShowInTheList(context, month, year))
            }
        }
        Collections.sort(listOfEvents, object : Comparator<HashMap<String, String>> {
            override fun compare(one: HashMap<String, String>, two: HashMap<String, String>): Int {
                return (one[Event.Companion.DATE_TO_SORT]!!.toInt()).compareTo((two[Event.Companion.DATE_TO_SORT]!!).toInt())
            }
        })
     return listOfEvents
    }

    fun getEvents(date: LocalDate) : List<HashMap<String, String>> {
        val eventElementList = getAllEventsFromDb()
        val listOfEvents : MutableList<HashMap<String, String>> = mutableListOf()

        for (eventElement in eventElementList) {
            if (eventElement.relevantInCurrentDay(date)) {
                listOfEvents.add(eventElement.convertParamsOfEventToShowInThePopUp(context))
            }
        }
       // listOfEvents.sortWith(CustomComparator<HashMap<String, String>>)
        return listOfEvents
    }
}
