package pet.docs.dogs.domain.events

import androidx.room.ColumnInfo
import androidx.room.ColumnInfo.INTEGER
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events_list")
data class EventElement(
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo(name = "type", typeAffinity = INTEGER) val eType : Int,
    @ColumnInfo(name = "date") val eDate : String,
    @ColumnInfo(name = "comments") val eComments : String?,
    @ColumnInfo(name = "regularity") val eRegularity : Int,
    @ColumnInfo(name = "expired_date") val eExpiredDate : String
)
