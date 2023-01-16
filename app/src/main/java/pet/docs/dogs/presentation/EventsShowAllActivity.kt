package pet.docs.dogs.presentation

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_events_show.*
import kotlinx.android.synthetic.main.activity_passport_create.*
import kotlinx.android.synthetic.main.popup_add_event.view.*
import kotlinx.android.synthetic.main.popup_add_event.view.comments
import kotlinx.android.synthetic.main.popup_add_event.view.eventType
import kotlinx.android.synthetic.main.popup_add_event.view.regularity
import kotlinx.android.synthetic.main.popup_edit_delete_activity.*
import kotlinx.android.synthetic.main.popup_edit_delete_activity.view.*
import kotlinx.android.synthetic.main.popup_show_events.view.*
import pet.docs.dogs.R
import pet.docs.dogs.data.eventsDb.EventDb
import pet.docs.dogs.domain.events.*
import pet.docs.dogs.domain.events.usecases.DeleteEventUseCase
import pet.docs.dogs.domain.events.usecases.GetEventUseCase
import pet.docs.dogs.domain.events.usecases.IsEventExistUseCase
import pet.docs.dogs.domain.events.usecases.UpdateEventUseCase
import pet.docs.dogs.domain.utils.Utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


private const val TAG = "EVENTS_SHOW_ALL_ACTIVITY"
class EventsShowAllActivity : AppCompatActivity() {

    private var monthToShow: Int = LocalDate.now().monthValue
    private lateinit var popUp: PopupWindow
    private var idOfChosenElem = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_show)
        val currentDate = LocalDate.now()
        initBlocks(currentDate)
    }

    private fun initBlocks(currentDate: LocalDate) {
        val elementList =
            GetEventUseCase(EventDb(this), this).getEvents(monthToShow, currentDate.year)
        numberPicker.minValue = 0
        numberPicker.maxValue = 11
        numberPicker.displayedValues = monthArray()
        numberPicker.wrapSelectorWheel = true
        numberPicker.setOnValueChangedListener { _, _, newVal ->
            monthToShow = newVal + 1
        }
        listViewOfEvents.adapter = initAdapterForElementsList(elementList)
        listViewOfEvents.setOnItemClickListener { adapter, view, position, id ->
            addPopUp(adapter as AdapterView<Adapter?>, position)
        }
    }

    fun updateEventsInTheChosenMonth(view: View) {
        val elementList =
            GetEventUseCase(EventDb(this), this).getEvents(monthToShow, LocalDate.now().year)
        listViewOfEvents.adapter = initAdapterForElementsList(elementList)
        listViewOfEvents.setOnItemClickListener { adapter, view, position, id ->
            addPopUp(adapter as AdapterView<Adapter?>, position)
        }
    }

    private fun addPopUp(adapter: AdapterView<Adapter?>, position: Int) {
        val element = adapter.getItemAtPosition(position) as HashMap<String, String>
        if (element.size != 2) {
            val eventInDBId = element[Event.EVENT_ID]
            if (eventInDBId != null) {
                idOfChosenElem = eventInDBId.toInt()
                if (IsEventExistUseCase(EventDb(this)).execute(idOfChosenElem)) {
                    val eventInDb =
                        GetEventUseCase(EventDb(adapter.context), adapter.context).getEvent(
                            idOfChosenElem
                        )
                    /*eventInDb.expiredDate = LocalDate.parse("2023-02-02")
                val isUpdated = UpdateEventUseCase(EventDb(adapter.context)).execute(eventInDb)
                Log.d(TAG, "is updated = " + isUpdated)
                    val eventInDb2 = GetEventUseCase(EventDb(adapter.context), adapter.context).getEvent(intV)
                Log.d(TAG, "expired2" + eventInDb2.expiredDate)*/
                    val viewPopUp =
                        layoutInflater.inflate(R.layout.popup_edit_delete_activity, null)
                    popUp = PopupWindow(adapter.context)
                    popUp.contentView = viewPopUp

                    val expiredDate = viewPopUp.findViewById<EditText>(R.id.expired_date)
                    if (expiredDate != null) {
                        Utils.datePattern(expiredDate, TAG)
                        if (eventInDb.expiredDate.toString() == Event.DEFAULT_DATE)
                            expiredDate.hint = getString(R.string.no_expiration_date)
                        else
                            expiredDate.hint = getString(R.string.expired_date_hint) + " " + eventInDb.expiredDate.toString()
                        Log.d(TAG, "expired date")
                    }
                    val comments = viewPopUp.findViewById<EditText>(R.id.comments)
                    if (comments != null) {
                        if (eventInDb.comments != null && eventInDb.comments != "")
                            comments.hint = eventInDb.comments
                        else
                            comments.hint = getString(R.string.comment)
                        Log.d(TAG, "comments")
                    }
                    val type = viewPopUp.findViewById<Spinner>(R.id.eventType)
                    val typeValue = element[Event.EVENT_TYPE]
                    if (type != null && typeValue != null) {
                        val typeElements =
                            addChosenElementOnFirstPositionInTheList(
                                typeValue,
                                EventType.returnAllNames(this)
                            )
                        val spinnerArrayAdapter = initAdapterForSpinner(typeElements)
                        type.adapter = spinnerArrayAdapter
                    }

                    val regularity = viewPopUp.findViewById<Spinner>(R.id.regularity)
                    val regularityValue = eventInDb.regularity.getStringValue(this)
                    if (regularity != null) {
                        val regularityElements =
                            addChosenElementOnFirstPositionInTheList(
                                regularityValue,
                                EventRegularity.returnAllNames(this)
                            )
                        val spinnerArrayAdapter = initAdapterForSpinner(regularityElements)
                        regularity.adapter = spinnerArrayAdapter
                        Log.d(TAG, "regularity = " + regularityValue)
                    }
                    popUp.isFocusable = true
                    popUp.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    popUp.showAtLocation(viewPopUp, Gravity.CENTER, 0, 0)
                }
                else
                    Toast.makeText(this,getString(R.string.event_deleted), Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun deleteClicked(view: View) {
        if (idOfChosenElem != -1) {
            if (IsEventExistUseCase(EventDb(this)).execute(idOfChosenElem)) {
                DeleteEventUseCase(EventDb(this)).deleteEventById(idOfChosenElem)
                DeleteEventUseCase(EventDb(this)).deleteEventById(idOfChosenElem)
                Toast.makeText(this,getString(R.string.event_deleted_update), Toast.LENGTH_SHORT).show()
                idOfChosenElem = -1
                popUp.dismiss()
            }
        }
    }

    fun saveClicked(view: View) {
        var dissmissPopUp = true
        if (idOfChosenElem != -1) {
            if (IsEventExistUseCase(EventDb(this)).execute(idOfChosenElem)) {
                val eventType = EventType.getEventTypeByValue(popUp.contentView.eventType.selectedItem.toString(), this)
                val eventRegularity = EventRegularity.getRegularityByValue(popUp.contentView.regularity.selectedItem.toString(), this)
                val comments = popUp.contentView.comments.text.toString()
                val expiredDate = popUp.contentView.expired_date
                val expiredDate2 = popUp.contentView.expired_date.text.toString()
                val eventInDb2 = GetEventUseCase(EventDb(this), this).getEvent(idOfChosenElem)
                var needToUpdate = false
                Utils.datePattern(expiredDate, TAG)
                if (eventInDb2.type != eventType) {
                    eventInDb2.type = eventType
                    needToUpdate = true
                }
                if (eventInDb2.regularity != eventRegularity) {
                    eventInDb2.regularity = eventRegularity
                    needToUpdate = true
                }
                if (eventInDb2.comments != comments && comments != "") {
                    eventInDb2.comments = comments
                    needToUpdate = true
                }
                if (eventInDb2.expiredDate.toString() != expiredDate2 && expiredDate2 != "" || expiredDate2 != "" ) {
                    if (Utils.checkBirthdayDate(expiredDate2)) {
                        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                        val expDate = LocalDate.parse(expiredDate2, formatter)
                        if (compareDates(expDate, eventInDb2.date)) {
                            needToUpdate = true
                            eventInDb2.expiredDate = expDate
                        } else {
                            dissmissPopUp = false
                            Toast.makeText(this,getString(R.string.early_expired_date), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        dissmissPopUp = false
                        Toast.makeText(this,getString(R.string.incorrect_expired_date), Toast.LENGTH_SHORT).show()
                    }
                }
                if (needToUpdate) {
                    val isUpdated = UpdateEventUseCase(EventDb(this)).execute(eventInDb2)
                    if (isUpdated)
                        Toast.makeText(this,getString(R.string.event_changed_update), Toast.LENGTH_SHORT).show()
                }

            }  else
                Toast.makeText(this,getString(R.string.event_deleted), Toast.LENGTH_SHORT).show()
        }
        if (dissmissPopUp)
            popUp.dismiss()
    }
    private fun compareDates(expiredDate2 : LocalDate, startD: LocalDate) : Boolean{
       return expiredDate2 > startD
    }

    private fun initAdapterForSpinner(elements: List<String>) : SpinnerAdapter{
        val spinnerArrayAdapter: ArrayAdapter<String?> =
            object : ArrayAdapter<String?>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                elements
            ) {
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {

                    // Get the item view
                    val view = super.getDropDownView(
                        position, convertView, parent
                    )
                    val textView = view as TextView
                    if (position == 0) {
                        // Set the hint text color gray
                        textView.setTextColor(Color.GRAY)
                    } else {
                        textView.setTextColor(Color.BLACK)
                    }
                    return view
                }
            }
        return spinnerArrayAdapter

    }

    private fun addChosenElementOnFirstPositionInTheList(elem: String, listElem: List<String>): List<String> {
        val resultList = listElem.toMutableList()
        resultList.remove(elem)
        resultList.add(0, elem)
        return resultList
    }

    private fun monthArray(): Array<String> {
        return arrayOf(
            getString(R.string.january),
            getString(R.string.february),
            getString(R.string.march),
            getString(R.string.april),
            getString(R.string.may),
            getString(R.string.june),
            getString(R.string.july),
            getString(R.string.august),
            getString(R.string.september),
            getString(R.string.october),
            getString(R.string.november),
            getString(R.string.december)
        )
    }

    private fun initAdapterForElementsList(elementList: List<HashMap<String, String>>): SimpleAdapter {
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
                intArrayOf(R.id.event_title, R.id.comment)
            )
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