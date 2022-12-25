package pet.docs.dogs.presentation

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup_add_event.view.*
import pet.docs.dogs.R
import pet.docs.dogs.data.animalInfo.DogInformationStorageImpl
import pet.docs.dogs.domain.animals.IsDogExistUseCase
import pet.docs.dogs.domain.events.Event
import pet.docs.dogs.domain.events.EventRegularity
import pet.docs.dogs.domain.events.EventType
import java.time.LocalDate

private const val TAG = "MAIN_ACTIVITY"

class MainActivity : AppCompatActivity() {

    private lateinit var vm: MainActivityViewModel
    private var year : Int = 1
    private var month : Int = 1
    private var day : Int = 1

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView
    private lateinit var popUp : PopupWindow
    private val userRepository by lazy (LazyThreadSafetyMode.NONE){ DogInformationStorageImpl(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "app created")
        setContentView(R.layout.activity_main)
        vm = ViewModelProvider(this)[MainActivityViewModel::class.java]

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
       // val
        popUp = PopupWindow(this)
        val viewPopUp = layoutInflater.inflate(R.layout.popup_add_event, null)

        popUp.contentView = viewPopUp

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
        popUp.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        popUp.showAsDropDown(addEvent)
    }

    fun showEvents(view: View) {
        val popUp = PopupWindow(this)
        val viewPopUp = layoutInflater.inflate(R.layout.popup_show_events, null)

        popUp.contentView = viewPopUp

        val adapter = SimpleAdapter(this, convertToShowList(eventsList()),R.layout.pattern_show_events,
            arrayOf("eventType","comment"), intArrayOf(R.id.event_title, R.id.comment))

        val te = viewPopUp.findViewById<ListView>(R.id.viewEventsFromPopUp)
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

    fun cancelClicked(view: View) {
        popUp.dismiss()
    }

    fun saveClicked(view: View) {
        println("event_type = " + popUp.contentView.eventType.selectedItem)
        println("regularityID = " + popUp.contentView.regularity.selectedItemId + "regularity = " + popUp.contentView.regularity.selectedItem)
        println("comments3 = " + popUp.contentView.comments.text)
        popUp.dismiss()
    }
}