package pet.docs.dogs.presentation

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_add_event.view.*
import pet.docs.dogs.R
import pet.docs.dogs.data.animalInfo.DogInformationStorageImpl
import pet.docs.dogs.data.eventsDb.AppDatabase
import pet.docs.dogs.data.eventsDb.EventElement
import pet.docs.dogs.domain.animals.IsDogExistUseCase
import pet.docs.dogs.domain.events.Event
import pet.docs.dogs.domain.events.EventRegularity
import pet.docs.dogs.domain.events.EventType
import java.time.LocalDate

private const val TAG = "MAIN_ACTIVITY"
private val DEFAULT_DATE: LocalDate = LocalDate.parse("0001-01-01")

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainActivityViewModel

    private var dateFromCalendar : LocalDate = DEFAULT_DATE //LocalDate.parse(DEFAULT_DATE)

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var popUp : PopupWindow
    private val userRepository by lazy (LazyThreadSafetyMode.NONE){ DogInformationStorageImpl(applicationContext) }
    private lateinit var appDb : AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "app created")
        setContentView(R.layout.activity_main)
        vm = ViewModelProvider(this)[MainActivityViewModel::class.java]

        drawerLayout = findViewById(R.id.mainActivityLayout)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)
        appDb = AppDatabase.getDatabase(this)
        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()

        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)
        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.animal1 -> {
                    val dogExistInStorage = IsDogExistUseCase(userRepository)
                    //проверяем есть ли данные о песике в памяти приложения
                    val intent = if (dogExistInStorage.execute()) Intent(
                        this@MainActivity,
                        PassportShowActivity::class.java
                    ) else Intent(this@MainActivity, PassportCreateActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {
                    false
                }
            }
        }
        vm.getAll.observe(this) { eventElement ->
            saveG(eventElement)}
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    fun saveG(eventElementList : List<EventElement>){

    }

    override fun onSupportNavigateUp(): Boolean {
        return if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
            false
        } else {
            drawerLayout.openDrawer(navView)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.show_this_month_events) {
            //Toast.makeText(this, "Item One Clicked", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, EventsShowAllActivity::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)

    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() = if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    fun addEvent(view: View) {
        popUp = PopupWindow(this)
        val viewPopUp = layoutInflater.inflate(R.layout.popup_add_event, null)

        popUp.contentView = viewPopUp
        readDate()
        val eventType = viewPopUp.findViewById<Spinner>(R.id.eventType)
        eventType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            EventType.returnAllNames()
        )

        val regularity = viewPopUp.findViewById<Spinner>(R.id.regularity)
        regularity.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            EventRegularity.returnAllNames()
        )
        val commentBox = viewPopUp.findViewById<EditText>(R.id.comments)
        commentBox.setOnFocusChangeListener { v, hasFocus ->

            println(
                "commentBox.isPressed = " + commentBox.isPressed + ", hasFocus = " + hasFocus
                        + "popUp.isShowing = " + popUp.isShowing
            )
            if (hasFocus) {
                println("commentBox isPressed")
                //popUp.isFocusable = true
                showEvent.visibility = View.GONE
                addEvent.visibility = View.GONE
            } else {
                showEvent.visibility = View.VISIBLE
                addEvent.visibility = View.VISIBLE
                println("commentBox isUnPressed")
            }
        }
        popUp.isFocusable = true
        popUp.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popUp.showAsDropDown(addEvent)
    }

    fun showEvents(view: View) {
        popUp = PopupWindow(this)
        val viewPopUp = layoutInflater.inflate(R.layout.popup_show_events, null)

        popUp.contentView = viewPopUp
        var elementList : List<EventElement>? = null
        val m = vm.getAll.observe(this) { eventElement ->
            elementList = eventElement
            //println("here result = " + eventElement.size)
        }
        var adapter : SimpleAdapter

        if (elementList != null) {
            adapter = SimpleAdapter(
                this, convertToShowList(convertT(elementList!!)), R.layout.pattern_show_events,
                arrayOf("eventType", "comment"), intArrayOf(R.id.event_title, R.id.comment)
            )
        } else {
            adapter = SimpleAdapter(
                this, convertToShowList(eventsList()), R.layout.pattern_show_events,
                arrayOf("eventType", "comment"), intArrayOf(R.id.event_title, R.id.comment)
            )
        }
        val te = viewPopUp.findViewById<ListView>(R.id.viewEventsFromPopUp)
        te.adapter = adapter


        //readDateFromDb(vm.getEventsWithRegularity(0))
        popUp.isFocusable = true
        popUp.update()
        popUp.showAsDropDown(addEvent, -340, 0, Gravity.END)
    }

    fun convertT(eventElementList : List<EventElement>): List<Event>
    {
        val listOfEvents = mutableListOf<Event>()
        for (eventElement in eventElementList){
        listOfEvents.add(Event(eventElement))
        }
        return listOfEvents
    }


private fun eventsList():MutableList<Event>{
val event1 = Event(EventType.BuyingFood, LocalDate.now(), EventRegularity.Never)
val event2 = Event(EventType.RabiesVaccination, LocalDate.now(), EventRegularity.EveryMonth )
val event3 = Event(EventType.TickPill, LocalDate.now(), "test", EventRegularity.EveryYear )
return mutableListOf(event1, event2, event3)
}

private fun convertToShowList(listOfEvents : List<Event>) : List<HashMap<String, String>>{
val eventsList : MutableList<HashMap<String, String>> = mutableListOf()
for (event in listOfEvents) {
    eventsList.add(event.convertParamsOfEventToShowInThePopUp())
}
return eventsList
}

fun cancelClicked(view: View) {
dateFromCalendar = DEFAULT_DATE
popUp.dismiss()
}

fun saveClicked(view: View) {
    val event = readEventFromScreen()
    if (event != null) {
        vm.insert(event.convertToWriteInDb())
    }
    if (event != null) {
        dateFromCalendar = DEFAULT_DATE
        popUp.dismiss()
    }
}

private fun readDate(){
calendarView.setOnDateChangeListener { _, i, i1, i2 ->
    dateFromCalendar = LocalDate.of(i, (i1 + 1), i2) }
}

private fun readEventFromScreen(): Event? {
if (dateFromCalendar == DEFAULT_DATE) {
    Toast.makeText(this, "Choose the date before", Toast.LENGTH_LONG).show()
    popUp.dismiss()
    return null
}
val eventType = EventType.getEventTypeById(popUp.contentView.eventType.selectedItemId.toInt())
val regularity = EventRegularity.getRegularityById(popUp.contentView.regularity.selectedItemId.toInt())
val comment = popUp.contentView.comments.text.toString()
return Event(eventType, dateFromCalendar, comment, regularity)
}
}