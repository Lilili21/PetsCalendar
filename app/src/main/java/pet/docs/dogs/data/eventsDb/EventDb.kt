package pet.docs.dogs.data.eventsDb

import android.content.Context
import androidx.room.Room
import pet.docs.dogs.domain.events.EventElement

class EventDb(val context: Context) {

    fun addEvent(event: EventElement) {
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "events_list"
        ).fallbackToDestructiveMigration().build()
        val t = Thread{
            db.eventDao().insert(event)
        }
        t.start()
        t.join()
        db.close()
    }

    fun getAll() : List<EventElement>{
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "events_list"
        ).fallbackToDestructiveMigration().build()
        var allEvents: List<EventElement> = emptyList()
        val t = Thread{
            allEvents = db.eventDao().getAll()
        }
        t.start()
        t.join()
        db.close()

        return allEvents
    }

    fun getEventsWithRegularity(eRegularity: Int) : List<EventElement>{
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "events_list"
        ).fallbackToDestructiveMigration().build()
        var allEvents: List<EventElement> = emptyList()
        val t = Thread{
            allEvents = db.eventDao().getEventsWithRegularity(eRegularity)
        }
        t.start()
        t.join()
        db.close()

        return allEvents
    }

    fun getEventsWithType(eType: Int) : List<EventElement>{
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "events_list"
        ).fallbackToDestructiveMigration().build()
        var allEvents: List<EventElement> = emptyList()
        val t = Thread{
            allEvents = db.eventDao().getEventsWithType(eType)
        }
        t.start()
        t.join()
        db.close()

        return allEvents
    }


    fun deleteEvent(event: EventElement){
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "events_list"
        ).fallbackToDestructiveMigration().build()
        val t = Thread{
            db.eventDao().delete(event)
        }
        t.start()
        t.join()
        db.close()
    }

    fun deleteAll(){
        val db = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "events_list"
        ).fallbackToDestructiveMigration().build()
        val t = Thread{
            db.eventDao().deleteAll()
        }
        t.start()
        t.join()
        db.close()
    }
}