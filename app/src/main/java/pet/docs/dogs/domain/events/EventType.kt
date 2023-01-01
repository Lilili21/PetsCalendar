package pet.docs.dogs.domain.events

enum class EventType(val eventValue:String, val eventNum:Int) {
    RabiesVaccination("Прививка от Бешенства", 0),
    TickPill("Таблетка от клещей", 1),
    TickSpray("Нанесен спрей от клещей", 2),
    WormPill("Таблетка от глистов", 3),
    BuyingFood("Корм куплен", 4),
    ExceptionType("Ошибка", 9);

    companion object {
        fun returnAllNames(): List<String> {
            val names: MutableList<String> = mutableListOf()
            names.add(RabiesVaccination.eventValue)
            names.add(TickPill.eventValue)
            names.add(TickSpray.eventValue)
            names.add(WormPill.eventValue)
            names.add(BuyingFood.eventValue)
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
}