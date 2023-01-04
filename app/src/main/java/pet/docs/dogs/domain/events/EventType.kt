package pet.docs.dogs.domain.events

import android.content.Context
import pet.docs.dogs.R

enum class EventType(val eventValueId:Int, val eventNum:Int) {
    RabiesVaccination(R.string.rabies_vaccination, 0),
    TickPill(R.string.tick_pill, 1),
    TickSpray(R.string.tick_spray, 2),
    WormPill(R.string.worm_pill, 3),
    BuyingFood(R.string.buying_food, 4),
    ExceptionType(R.string.exception_type, 9);

    companion object {
        fun returnAllNames(context: Context): List<String> {
            val names: MutableList<String> = mutableListOf()
            names.add(context.getString(RabiesVaccination.eventValueId))
            names.add(context.getString(TickPill.eventValueId))
            names.add(context.getString(TickSpray.eventValueId))
            names.add(context.getString(WormPill.eventValueId))
            names.add(context.getString(BuyingFood.eventValueId))
            return names
        }

        fun getEventTypeById(id:Int): EventType {
            return when (id) {
                0 -> RabiesVaccination
                1 -> TickPill
                2 -> TickSpray
                3 -> WormPill
                4 -> BuyingFood
                else -> ExceptionType
            }
        }
    }

    fun getStringValue(context : Context) : String{
        return context.getString(this.eventValueId)
    }
}