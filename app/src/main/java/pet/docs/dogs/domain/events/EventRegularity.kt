package pet.docs.dogs.domain.events

enum class EventRegularity (val eventValue:String, val eventNum:Int) {
    Never("Не повторять", 0),
    Once("Разово", 1),
    EveryDay("Ежедневно", 2),
    EveryMonth("Ежемесячно", 3),
    ThreeMonth("Каждые 3 месяца", 4),
    EveryYear("Ежегодно", 5);

    companion object {
        fun returnAllNames(): List<String> {
            val names: MutableList<String> = mutableListOf()
            names.add(Never.eventValue)
            names.add(Once.eventValue)
            names.add(EveryDay.eventValue)
            names.add(EveryMonth.eventValue)
            names.add(ThreeMonth.eventValue)
            names.add(EveryYear.eventValue)
            return names
        }
    }
}