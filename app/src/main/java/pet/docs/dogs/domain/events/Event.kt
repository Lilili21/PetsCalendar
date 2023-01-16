package pet.docs.dogs.domain.events

import android.content.Context
import pet.docs.dogs.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

class Event(var type: EventType,
            var date: LocalDate,
            var comments: String?,
            var regularity: EventRegularity,
            var expiredDate: LocalDate,
            val id: Int) {

    constructor():this(EventType.RabiesVaccination, LocalDate.now(), EventRegularity.ExceptionRegularity)
    constructor(eventType: EventType, eventDate: LocalDate, eventRegularity: EventRegularity) : this(eventType, eventDate, EMPTY_COMMENT, eventRegularity, LocalDate.parse("9999-12-31"), 0)
    constructor(eventType: EventType, eventDate: LocalDate, eventRegularity: EventRegularity, expiredDate: LocalDate) : this(eventType, eventDate, EMPTY_COMMENT, eventRegularity, expiredDate, 0)
    constructor(eventType: EventType, eventDate: LocalDate, comments: String?, eventRegularity: EventRegularity) : this(eventType, eventDate, comments, eventRegularity, LocalDate.parse("9999-12-31"), 0)
    constructor(eventType: EventType, eventDate: LocalDate, comments: String?, eventRegularity: EventRegularity, expiredDate: LocalDate) : this(eventType, eventDate, comments, eventRegularity, expiredDate, 0)

    constructor(eventFromDb : EventElement): this(
       EventType.getEventTypeById(eventFromDb.eType),
        LocalDate.parse(eventFromDb.eDate),
        eventFromDb.eComments,
        EventRegularity.getRegularityById(eventFromDb.eRegularity),
        LocalDate.parse(eventFromDb.eExpiredDate),
        eventFromDb.id)

    companion object {
        const val EMPTY_COMMENT = ""
        const val EVENT_TYPE = "eventType"
        const val EVENT_ID = "eventID"
        const val COMMENT = "comment"
        const val DATE = "date"
        const val DATE_TO_SORT = "date_to_sort"
        const val DATE_TO_SORT_FOR_EVERY_DAY = "0"
        const val DEFAULT_DATE="9999-12-31"
    }

    fun convertToWriteInDb(): EventElement {
       return EventElement(id, type.eventNum, date.toString(), comments, regularity.eventNum,expiredDate.toString())
    }

    fun relevantInCurrentMonth(month: Int): Boolean{
        if (checkExpired(LocalDate.of(2023, month, 1), false))
            return false
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

    //expired date before current date return true
    //expired year < current year || (expired year == current year && expired month < current month)
    private fun checkExpired(currentDate: LocalDate, checkDay:Boolean) : Boolean {
        expiredDate.isAfter(currentDate)
        return (expiredDate.year < currentDate.year ||
           (expiredDate.year == currentDate.year && expiredDate.month < currentDate.month) ||
           (checkDay && expiredDate.year == currentDate.year && expiredDate.month == currentDate.month && expiredDate.dayOfMonth < currentDate.dayOfMonth))
    }
    //1, 4, 7, 10, 1
    //2, 5, 8, 11
    //3, 6, 9, 12

    fun relevantInCurrentDay(currentDate: LocalDate): Boolean{
        if (checkExpired(currentDate, true))
            return false
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
            eventsMap[COMMENT] = comments!!
        return eventsMap
    }

    fun convertParamsOfEventToShowInTheList(context: Context, month : Int, year: Int) : HashMap<String, String> {
        val eventsMap : HashMap<String, String> = HashMap()
        eventsMap[EVENT_ID] = id.toString()
        eventsMap[EVENT_TYPE] = type.getStringValue(context)
        if (comments.isNullOrEmpty())
            eventsMap[COMMENT] = EMPTY_COMMENT
        else
            eventsMap[COMMENT] = comments!!

        eventsMap[DATE_TO_SORT] = date.dayOfMonth.toString()
        val ym = YearMonth.of(year, month)
        val endOfMonth = ym.atEndOfMonth()
        if(regularity == EventRegularity.EveryDay) {
            //divide for months
            if (date.month.value < month) {
                eventsMap[DATE] = context.getString(R.string.every_day)
                eventsMap[DATE_TO_SORT] = DATE_TO_SORT_FOR_EVERY_DAY
            } else {
                eventsMap[DATE] =  context.getString(R.string.every_day_since) + " " + date.dayOfMonth.toString()
            }
        } else if (endOfMonth.dayOfMonth < date.dayOfMonth){
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

    override fun equals(other: Any?): Boolean {
        return if (other == null ||  other.javaClass != Event.javaClass) {
            false
        } else {
            val castedElem = other as Event
            (castedElem.type == type &&
                    castedElem.regularity == regularity &&
                    castedElem.expiredDate == expiredDate &&
                    castedElem.comments.equals(comments))
        }
    }
}