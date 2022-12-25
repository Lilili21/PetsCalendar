package pet.docs.dogs.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import pet.docs.dogs.R
import pet.docs.dogs.data.animalInfo.DogInformationStorageImpl
import pet.docs.dogs.domain.animals.IsDogExistUseCase
import pet.docs.dogs.domain.events.Event
import pet.docs.dogs.domain.events.EventRegularity
import pet.docs.dogs.domain.events.EventType
import java.time.LocalDate

private const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {

    private var year : Int = 1
    private var month : Int = 1
    private var day : Int = 1

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    private val userRepository by lazy (LazyThreadSafetyMode.NONE){ DogInformationStorageImpl(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "app created")
        setContentView(R.layout.activity_main)
        calendarView.setOnDateChangeListener { _, i, i1, i2 ->
            year = i
            month = i1
            day = i2
        }
        var day2 = LocalDate.of(year, month, day)

        drawerLayout = findViewById(R.id.mainActivityLayout)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

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
        val popUp = PopupWindow(this)
        val viewPopUp = layoutInflater.inflate(R.layout.popup_add_event, null)

        popUp.contentView = viewPopUp

        val eventType = viewPopUp.findViewById<Spinner>(R.id.event_type)
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

        viewPopUp.findViewById<Button>(R.id.saveEvent).setOnClickListener {
            println("event_type = " + eventType.selectedItem)
            println("regularityID = " + regularity.selectedItemId + "regularity = " + regularity.selectedItem)
            println("comments3 = " + commentBox.text.toString())
            popUp.dismiss()
        }
        commentBox.setOnFocusChangeListener { v, hasFocus ->
            println(
                "commentBox.isPressed = " + commentBox.isPressed + ", hasFocus = " + hasFocus
                        + "popUp.isShowing = " + popUp.isShowing
            )
            if (hasFocus) {
                println("commentBox isPressed")
                findViewById<Button>(R.id.showEvent).visibility = View.GONE
                findViewById<Button>(R.id.addEvent).visibility = View.GONE
            } else {
                println("commentBox isUnPressed")
                findViewById<Button>(R.id.showEvent).visibility = View.VISIBLE
                findViewById<Button>(R.id.addEvent).visibility = View.VISIBLE
            }
        }

        viewPopUp.findViewById<Button>(R.id.cancel).setOnClickListener {
            popUp.dismiss()
        }
        popUp.isOutsideTouchable = isRestricted
        println("isOutsideTouchable = " + popUp.isOutsideTouchable)
        if (popUp.isOutsideTouchable) {
            println("isOutsideTouchable")
        }
        popUp.isFocusable = true
        popUp.update()
        popUp.showAsDropDown(addEvent)
    }

    fun showEvents(view: View) {
        val popUp = PopupWindow(this)
        val viewPopUp = layoutInflater.inflate(R.layout.popup_show_events, null)

        popUp.contentView = viewPopUp

        val adapter = SimpleAdapter(this, convertToShowList(eventsList()),R.layout.pattern_show_events,
            arrayOf("eventType","comment"), intArrayOf(R.id.event_title, R.id.comment))

        val te = viewPopUp.findViewById<ListView>(R.id.ListView)
        te.adapter = adapter
        popUp.isFocusable = true
        popUp.update()
        popUp.showAsDropDown(addEvent, -340, 0, Gravity.END)
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
}