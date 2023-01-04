package pet.docs.dogs.domain.events

import android.content.Context
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class Event(val type: EventType,
            val date: LocalDate,
            val comments: String?,
            val regularity: EventRegularity,
            val expiredDate: LocalDate) {

    constructor(eventType: EventType, eventDate: LocalDate, eventRegularity: EventRegularity) : this(eventType, eventDate, EMPTY_COMMENT, eventRegularity, LocalDate.parse("9999-12-31"))
    constructor(eventType: EventType, eventDate: LocalDate, comments: String?, eventRegularity: EventRegularity) : this(eventType, eventDate, comments, eventRegularity, LocalDate.parse("9999-12-31"))

    constructor(eventFromDb : EventElement): this(
       EventType.getEventTypeById(eventFromDb.eType),
        LocalDate.parse(eventFromDb.eDate),
        eventFromDb.eComments,
        EventRegularity.getRegularityById(eventFromDb.eRegularity),
        LocalDate.parse(eventFromDb.eExpiredDate))

    companion object {
        const val EMPTY_COMMENT = ""
        const val EVENT_TYPE = "eventType"
        const val COMMENT = "comment"
        const val DATE = "date"
        const val DATE_TO_SORT = "date_to_sort"
    }

    fun convertToWriteInDb(): EventElement {
       return EventElement(null, type.eventNum, date.toString(), comments, regularity.eventNum,expiredDate.toString())
    }

    fun relevantInCurrentMonth(month: Int): Boolean{
        if (date.month.value == month)
            return true
        return when (regularity) {
            EventRegularity.EveryDay -> true
            EventRegularity.EveryMonth-> true
            EventRegularity.ThreeMonth-> {
                (date.month.value % 3 == month % 3 )
            }
            else -> false
        }
    }
    //1, 4, 7, 10, 1
    //2, 5, 8, 11
    //3, 6, 9, 12

    fun relevantInCurrentDay(currentDate: LocalDate): Boolean{
        if (date == currentDate)
            return true
        val isEndOfMonth = isEndOfMonth(currentDate)

        return when (regularity) {
            EventRegularity.EveryDay -> true
            EventRegularity.EveryMonth->
                (date.dayOfMonth == currentDate.dayOfMonth || isEndOfMonth && date.dayOfMonth > currentDate.dayOfMonth)
            EventRegularity.ThreeMonth->
                date.month.value % 3 == currentDate.month.value % 3
                        && (date.dayOfMonth == currentDate.dayOfMonth || isEndOfMonth && date.dayOfMonth > currentDate.dayOfMonth)
            EventRegularity.EveryYear -> (date.dayOfYear == currentDate.dayOfYear)
            else -> false
        }
    }

    private fun isEndOfMonth(currentDate: LocalDate) : Boolean{
        return  YearMonth.from(currentDate).lengthOfMonth() == currentDate.dayOfMonth
    }

    fun convertParamsOfEventToShowInThePopUp(context: Context) : HashMap<String, String> {
        val eventsMap : HashMap<String, String> = HashMap()
        eventsMap[EVENT_TYPE] = type.getStringValue(context)
        if (comments.isNullOrEmpty())
            eventsMap[COMMENT] = EMPTY_COMMENT
        else
            eventsMap[COMMENT] = comments
        return eventsMap
    }

    fun convertParamsOfEventToShowInTheList(context: Context, month : Int, year: Int) : HashMap<String, String> {
        val eventsMap : HashMap<String, String> = HashMap()
        eventsMap[EVENT_TYPE] = type.getStringValue(context)
        if (comments.isNullOrEmpty())
            eventsMap[COMMENT] = EMPTY_COMMENT
        else
            eventsMap[COMMENT] = comments

        eventsMap[DATE_TO_SORT] = date.dayOfMonth.toString()
        val ym = YearMonth.of(year, month)
        val endOfMonth = ym.atEndOfMonth()
        if (endOfMonth.dayOfMonth < date.dayOfMonth){
            eventsMap[DATE] = endOfMonth.dayOfMonth.toString() +  ", " +
                    endOfMonth.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE,
                Locale.getDefault())
        } else {
            val tmp = LocalDate.of(year, month, date.dayOfMonth)
            eventsMap[DATE] = tmp.dayOfMonth.toString() + ", " +
                    tmp.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE,
                        Locale.getDefault())
        }
        return eventsMap
    }

}