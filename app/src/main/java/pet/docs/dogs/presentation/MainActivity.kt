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
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_add_event.view.*
import pet.docs.dogs.R
import pet.docs.dogs.data.animalInfo.DogInformationStorageImpl
import pet.docs.dogs.data.eventsDb.EventDb
import pet.docs.dogs.domain.animals.GetDogInfoUseCase
import pet.docs.dogs.domain.animals.IsDogExistUseCase
import pet.docs.dogs.domain.events.*
import java.time.LocalDate

private const val TAG = "MAIN_ACTIVITY"
private val DEFAULT_DATE: LocalDate = LocalDate.parse("0001-01-01")

class MainActivity : AppCompatActivity() {

    private var dateFromCalendar: LocalDate = DEFAULT_DATE

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var popUp: PopupWindow
    private val userRepository by lazy(LazyThreadSafetyMode.NONE) {
        DogInformationStorageImpl(
            applicationContext
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "app created")
        setContentView(R.layout.activity_main)
        // vm = ViewModelProvider(this)[MainActivityViewModel::class.java]

        drawerLayout = findViewById(R.id.mainActivityLayout)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        // Display the hamburger icon to launch the drawer
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()

       //
        val dogExistInStorage = IsDogExistUseCase(userRepository)

        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)
        if (dogExistInStorage.execute()) {
            val name = GetDogInfoUseCase(userRepository).getDogName()
            if (name.isNotEmpty())
                navView.menu.findItem(R.id.animal1).title = name
        }
        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.animal1 -> {

                    //проверяем есть ли данные о песике в памяти приложения
                    val intent = if (dogExistInStorage.execute()) Intent(
                        this@MainActivity,
                        PassportShowActivity::class.java
                    ) else Intent(this@MainActivity, PassportCreateActivity::class.java)
                    Log.d(TAG, "Pressed first animal information")
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {
                    false
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
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
            Log.d(TAG, "Show all events activity button pressed")
            startActivity(Intent(this, EventsShowAllActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)

    }

    // override the onBackPressed() function to close the Drawer when the back button is clicked
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() =
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    onBackPressedDispatcher.onBackPressed()
                }
        }

    fun addEvent(view: View) {
        val viewPopUp = layoutInflater.inflate(R.layout.popup_add_event, null)
        initPopUp(viewPopUp)
        readDate()
        val eventType = viewPopUp.findViewById<Spinner>(R.id.eventType)
        eventType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            EventType.returnAllNames(this)
        )

        val regularity = viewPopUp.findViewById<Spinner>(R.id.regularity)
        regularity.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item,
            EventRegularity.returnAllNames(this)
        )
        val commentBox = viewPopUp.findViewById<EditText>(R.id.comments)
        commentBox.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showEvent.visibility = View.GONE
                addEvent.visibility = View.GONE
            } else {
                showEvent.visibility = View.VISIBLE
                addEvent.visibility = View.VISIBLE
            }
        }
        popUp.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        popUp.showAsDropDown(addEvent)
    }

    fun showEvents(view: View) {
        readDate()
        if (dateFromCalendar == DEFAULT_DATE) {
            Toast.makeText(this,getString(R.string.date_notification), Toast.LENGTH_LONG).show()
        } else {
            val elementList = GetEventUseCase(EventDb(this), this).getEvents(dateFromCalendar)
            if (elementList.isNotEmpty()) {
                val viewPopUp = layoutInflater.inflate(R.layout.popup_show_events, null)
                initPopUp(viewPopUp)
                val adapter = SimpleAdapter(
                    this, elementList, R.layout.pattern_show_events,
                    arrayOf(Event.EVENT_TYPE, Event.COMMENT), intArrayOf(R.id.event_title, R.id.comment)
                )
                dddAdapter(viewPopUp, adapter)
                popUp.update()
                popUp.showAsDropDown(addEvent, -340, 0, Gravity.END)
            } else {
                Toast.makeText(this, getString(R.string.empty_events_list_this_day), Toast.LENGTH_LONG).show()
            }
        }
    }

    fun cancelClicked(view: View) {
        returnToDefaultDateAndDismissEventPopUp()
        EventDb(this).deleteAll()
    }


    fun saveClicked(view: View) {
        val event = readEventFromScreen()
        val elementAdded = SaveEventUseCase(EventDb(this)).execute(event)
        if (elementAdded) {
            returnToDefaultDateAndDismissEventPopUp()
        }
    }

    private fun dddAdapter(viewPopUp: View, adapter: SimpleAdapter){
        val listOfElements = viewPopUp.findViewById<ListView>(R.id.viewEventsFromPopUp)
        listOfElements.adapter = adapter
    }

    private fun initPopUp(viewPopUp : View){
        popUp = PopupWindow(this)
        popUp.contentView = viewPopUp
        popUp.isFocusable = true
    }

    private fun returnToDefaultDateAndDismissEventPopUp() {
        dateFromCalendar = DEFAULT_DATE
        popUp.dismiss()
    }

    private fun readDate() {
        calendarView.setOnDateChangeListener { _, i, i1, i2 ->
            dateFromCalendar = LocalDate.of(i, (i1 + 1), i2)
        }
    }

    private fun readEventFromScreen(): Event? {
        if (dateFromCalendar == DEFAULT_DATE) {
            Toast.makeText(this, getString(R.string.date_notification), Toast.LENGTH_LONG).show()
            popUp.dismiss()
            return null
        }

        return Event(
            EventType.getEventTypeById(popUp.contentView.eventType.selectedItemId.toInt()),
            dateFromCalendar,
            popUp.contentView.comments.text.toString(),
            EventRegularity.getRegularityById(popUp.contentView.regularity.selectedItemId.toInt()),
            DEFAULT_DATE
        )
    }
}