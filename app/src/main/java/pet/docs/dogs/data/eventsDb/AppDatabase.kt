package pet.docs.dogs.data.eventsDb

import androidx.room.Database
import androidx.room.RoomDatabase
import pet.docs.dogs.domain.events.EventElement

@Database(entities=[EventElement::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
}