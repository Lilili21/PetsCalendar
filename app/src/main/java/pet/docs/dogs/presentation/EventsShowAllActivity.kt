package pet.docs.dogs.presentation

import android.os.Bundle
import android.util.Log
import android.widget.SimpleAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_events_show.*
import pet.docs.dogs.R
import pet.docs.dogs.data.eventsDb.EventDb
import pet.docs.dogs.domain.events.Event
import pet.docs.dogs.domain.events.GetEventUseCase
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import kotlin.collections.HashMap

private const val TAG = "EVENTS_SHOW_ALL_ACTIVITY"
class EventsShowAllActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_show)
        val currentDate = LocalDate.now()
        initBlocks(currentDate, 1)
        initBlocks(currentDate.plusMonths(1), 2)
    }

    private fun initBlocks(currentDate:LocalDate, num : Int) {
        val elementList = GetEventUseCase(EventDb(this), this).getEvents(currentDate.month.value, currentDate.year)
        var idInList = 100
        var positionInList = 100
        if (num == 1) {
            month.text =
                currentDate.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            listViewOfEvents.adapter = initAdapter(elementList)
            listViewOfEvents.setOnItemClickListener { _, view, position, id ->
                idInList = id.toInt()
                positionInList = position
                print("This id2 = " + idInList)
                Log.d(TAG, "This id2 = " + idInList)
            }
        } else {
            month2.text =
                currentDate.month.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
            listViewOfEvents2.adapter = initAdapter(elementList)
            listViewOfEvents2.setOnItemClickListener { _, view, position, id ->
                idInList = id.toInt()
                positionInList = position
                print("This id3 = " + idInList)
                Log.d(TAG, "This id3 = " + idInList)
            }
        }
        print("This id1 = " + idInList)
        Log.d(TAG, "This id1 = " + idInList)
    }

    private fun initAdapter(elementList: List<HashMap<String, String>>): SimpleAdapter{
        if (elementList.isNotEmpty()) {
           return SimpleAdapter(
                this,
                elementList,
                R.layout.pattern_show_all_events,
                arrayOf(Event.EVENT_TYPE, Event.COMMENT, Event.DATE),
                intArrayOf(R.id.event_title2, R.id.comment2, R.id.date)
            )
        } else {
           return SimpleAdapter(
                this,
                emptyEventList(),
                R.layout.pattern_show_events,
                arrayOf(Event.EVENT_TYPE, Event.COMMENT),
                intArrayOf(R.id.event_title, R.id.comment))
        }
    }

    private fun emptyEventList(): List<HashMap<String, String>> {
        val listOfEvents: MutableList<HashMap<String, String>> = mutableListOf()
        val eventsMap: HashMap<String, String> = HashMap()
        eventsMap[Event.EVENT_TYPE] = getString(R.string.empty_events_list_this_month)
        eventsMap[Event.COMMENT] = Event.EMPTY_COMMENT
        listOfEvents.add(eventsMap)
        return listOfEvents
    }
}