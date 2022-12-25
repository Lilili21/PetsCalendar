package pet.docs.dogs.domain.events

import java.time.LocalDate

class Event(val eventType: EventType, val eventDate: LocalDate, val comments: String, val eventRegularity: EventRegularity) {
    constructor(eventType: EventType, eventDate: LocalDate, eventRegularity: EventRegularity) : this(eventType, eventDate, EMPTY_COMMENT, eventRegularity)

    companion object {
        private const val EMPTY_COMMENT = ""
        private const val EVENT_TYPE = "eventType"
        private const val COMMENT = "comment"
        private const val DATE = "date"
    }

    fun convertParamsOfEventToShowInThePopUp() : HashMap<String, String> {
        val eventsMap : HashMap<String, String> = HashMap()
        eventsMap[EVENT_TYPE] = eventType.eventValue
        eventsMap[COMMENT] = comments
        return eventsMap
    }

    fun convertParamsOfEventToShowInTheList() : HashMap<String, String> {
        val eventsMap : HashMap<String, String> = HashMap()
        eventsMap[EVENT_TYPE] = eventType.eventValue
        eventsMap[COMMENT] = comments
        eventsMap[DATE] = eventDate.toString()
        return eventsMap
    }
}