package pet.docs.dogs.domain.animals

import android.content.Context
import pet.docs.dogs.R

class Dog(val name: String?, val birthDay: String?, private val gender:Boolean,
          var breed:String? = null,
          var color: String? = null,
          var cardNumber: String? = null,
          var brandNumber: String? = null) {

    //TODO если мы редактируем событие до предыдущее мы сохраняем до даты. и создаем новое с даты

    //private Uri photo;
    //event хранить в отдельной таблице и сюда их не добавлять, т.к. а зачем?
    //private List<Event> info = new ArrayList<>();

    constructor():this("test", "01-01-1990", false)
    constructor(lineArray : Array<String?>) : this(lineArray[0], lineArray[1], lineArray[2].toBoolean(),
        lineArray[3],
        lineArray[4],
        lineArray[5],
        lineArray[6])


    fun getGender(context : Context): String {
        return if (gender) context.getString(R.string.male) else context.getString(R.string.female)
    }

    override fun toString(): String {
        return name +
                "," +
                birthDay +
                "," +
                gender
    }

    fun additionalParams(): String {
        val result = StringBuilder()
        result.append(",").takeIf{breed != null}?.append(breed)
        result.append(",").takeIf{color != null}?.append(color)
        result.append(",").takeIf{cardNumber != null}?.append(cardNumber)
        result.append(",").takeIf{brandNumber != null}?.append(brandNumber)
        return result.toString()
    }

    fun getParamNames(): String {
        return "name,birthDay,gender,breed,color,cardNumber,brandNumber"
    }
}