package pet.docs.dogs.domain.events

import android.content.Context
import pet.docs.dogs.R

enum class EventRegularity (val eventValueId:Int, val eventNum:Int) {
    Never(R.string.never, 0),
    EveryDay(R.string.every_day, 1),
    EveryMonth(R.string.every_month, 2),
    ThreeMonth(R.string.every_three_months, 3),
    EveryYear(R.string.every_year, 4),
    ExceptionRegularity(R.string.exception_regularity, 9);

    companion object {
        fun returnAllNames(context: Context): List<String> {
            val names: MutableList<String> = mutableListOf()
            names.add(context.getString(Never.eventValueId))
            names.add(context.getString(EveryDay.eventValueId))
            names.add(context.getString(EveryMonth.eventValueId))
            names.add(context.getString(ThreeMonth.eventValueId))
            names.add(context.getString(EveryYear.eventValueId))
            return names
        }

        fun getRegularityById(id:Int): EventRegularity {
            return when (id) {
                0 -> Never
                1 -> EveryDay
                2 -> EveryMonth
                3 -> ThreeMonth
                4 -> EveryYear
                else -> ExceptionRegularity
            }
        }

        fun getRegularityByValue(textValue: String, context:Context): EventRegularity {
            return when(textValue){
                context.getString(R.string.never) -> Never
                context.getString(R.string.every_day) -> EveryDay
                context.getString(R.string.every_month) -> EveryMonth
                context.getString(R.string.every_three_months) -> ThreeMonth
                context.getString(R.string.every_year) -> EveryYear
                else -> ExceptionRegularity
            }
        }
    }
    fun getStringValue(context : Context) : String{
        return context.getString(this.eventValueId)
    }
}