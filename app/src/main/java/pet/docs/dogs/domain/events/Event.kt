package pet.docs.dogs.domain.events

import pet.docs.dogs.data.eventsDb.EventElement
import java.time.LocalDate
import kotlin.text.isNullOrEmpty as isNullOrEmpty

class Event(val type: EventType, val date: LocalDate, val comments: String?, val regularity: EventRegularity, val expiredDate: LocalDate) {
    constructor(eventType: EventType, eventDate: LocalDate, eventRegularity: EventRegularity) : this(eventType, eventDate, EMPTY_COMMENT, eventRegularity, LocalDate.parse("9999-12-31"))
    constructor(eventType: EventType, eventDate: LocalDate, comments: String?, eventRegularity: EventRegularity) : this(eventType, eventDate, comments, eventRegularity, LocalDate.parse("9999-12-31"))

    constructor(eventFromDb : EventElement): this(
       EventType.getEventTypeById(eventFromDb.eType),
        LocalDate.parse(eventFromDb.eDate),
        eventFromDb.eComments,
        EventRegularity.getRegularityById(eventFromDb.eRegularity),
        LocalDate.parse(eventFromDb.eExpiredDate))

    companion object {
        private const val EMPTY_COMMENT = ""
        private const val EVENT_TYPE = "eventType"
        private const val COMMENT = "comment"
        private const val DATE = "date"
    }

    fun convertToWriteInDb():EventElement{
       return EventElement(null, type.eventNum, date.toString(), comments, regularity.eventNum,expiredDate.toString())
    }

    fun convertParamsOfEventToShowInThePopUp() : HashMap<String, String> {
        val eventsMap : HashMap<String, String> = HashMap()
        eventsMap[EVENT_TYPE] = type.eventValue
        if (comments.isNullOrEmpty())
            eventsMap[COMMENT] = ""
        else
            eventsMap[COMMENT] = comments
        return eventsMap
    }

    fun convertParamsOfEventToShowInTheList() : HashMap<String, String> {
        val eventsMap : HashMap<String, String> = HashMap()
        eventsMap[EVENT_TYPE] = type.eventValue
        if (comments.isNullOrEmpty())
            eventsMap[COMMENT] = ""
        else
            eventsMap[COMMENT] = comments
        eventsMap[DATE] = date.toString()
        return eventsMap
    }

}