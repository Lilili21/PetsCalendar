package pet.docs.dogs.domain.animals

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

    fun getGender(): String {
        return if (gender) "Кобелёк" else "Сучка"
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
        result.append(",")
        if (breed != null) result.append(breed)
        result.append(",")
        if (color != null) result.append(color)
        result.append(",")
        if (cardNumber != null) result.append(cardNumber)
        result.append(",")
        if (brandNumber != null) result.append(brandNumber)
        return result.toString()
    }

    fun getParamNames(): String {
        return "name,birthDay,gender,breed,color,cardNumber,brandNumber"
    }
}