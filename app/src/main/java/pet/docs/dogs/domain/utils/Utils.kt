package pet.docs.dogs.domain.utils

import android.util.Log
import android.widget.EditText
import ru.tinkoff.decoro.MaskImpl
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser
import ru.tinkoff.decoro.slots.Slot
import ru.tinkoff.decoro.watchers.FormatWatcher
import ru.tinkoff.decoro.watchers.MaskFormatWatcher

class Utils {

    companion object{
        // проверяем дату по маске
        fun checkBirthdayDate(dateString: String):Boolean{
            val words = dateString.split(".").toTypedArray()
            if (words.size != 3)
                return false
            val day = words[0].toInt()
            val month = words[1].toInt()
            val year = words[2].toInt()
            return if (year in 1980..2025
                && month > 0 && month < 13
                && day > 0 && day < 32
            ) {
                when (day) {
                    31 -> month % 2 == 1 && month < 8 || month % 2 == 0 && month > 7
                    30 -> month != 2
                    29 -> (month != 2) or (year % 4 == 0)
                    else -> true
                }
            } else false
        }

        fun checkIfParameterEmpty(parameter : EditText, errorText : String) : Boolean{
            if (parameter.text.toString().isEmpty()) {
                parameter.error = errorText
                return false
            }
            return true
        }

        fun datePattern(birthday: EditText, tag : String) {
            val DATE_PATTERN = "__.__.____"
            val slots: Array<Slot> = UnderscoreDigitSlotsParser().parseSlots(DATE_PATTERN)
            val formatWatcher: FormatWatcher = MaskFormatWatcher(MaskImpl.createTerminated(slots))
            formatWatcher.installOn(birthday)
            Log.d(tag, "added pattern to birthday date")
        }
    }
}