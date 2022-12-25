package pet.docs.dogs.presentation

import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_events_show.*
import pet.docs.dogs.R
import pet.docs.dogs.domain.events.Event
import pet.docs.dogs.domain.events.EventRegularity
import pet.docs.dogs.domain.events.EventType
import java.time.LocalDate

class EventsShowAllActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_show)
        val listEvents = findViewById<ListView>(R.id.ListView)
        val adapter = SimpleAdapter(this, convertToShowList(eventsList()),R.layout.pattern_show_all_events,
            arrayOf("eventType","comment","date"), intArrayOf(R.id.event_title2, R.id.comment2, R.id.date))
        listEvents.adapter = adapter

        month.text = "November"

        val listEvents2 = findViewById<ListView>(R.id.ListView2)
        val adapter2 = SimpleAdapter(this, convertToShowList(eventsList()),R.layout.pattern_show_all_events,
            arrayOf("eventType","comment","date"), intArrayOf(R.id.event_title2, R.id.comment2, R.id.date))
        listEvents2.adapter = adapter2

        month2.text = "December"


    }

    private fun eventsList():MutableList<Event>{
        val event1 = Event(EventType.BuyingFood, LocalDate.now(), EventRegularity.Never )
        val event2 = Event(EventType.RabiesVaccination, LocalDate.now(), EventRegularity.EveryMonth )
        val event3 = Event(EventType.TickPill, LocalDate.now(), "test", EventRegularity.EveryYear )
        return mutableListOf(event1, event2, event3)
    }

    private fun convertToShowList(listOfEvents : List<Event>) : List<HashMap<String, String>>{
        val eventsList : MutableList<HashMap<String, String>> = mutableListOf()
        for (event in listOfEvents) {
            eventsList.add(event.convertParamsOfEventToShowInTheList())
        }
        return eventsList
    }
}