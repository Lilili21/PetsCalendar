package pet.docs.dogs.data.animalInfo

import android.content.Context
import android.util.Log
import pet.docs.dogs.domain.animals.Dog
import pet.docs.dogs.domain.animals.DogInformationStorage

private const val SHARED_PREFS_NAME = "dog_info"
private const val DOG_DATA = "dog_info"
private const val DOG_QUANTITY = "dog_quantity"

class DogInformationStorageImpl(context:Context) : DogInformationStorage {

    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE)

    override fun ifFileExist(): Boolean {
        return sharedPreferences.getInt(DOG_QUANTITY, 0) != 0
    }

    override fun readDogQuantity(): Int {
        return sharedPreferences.getInt(DOG_QUANTITY, 0)
    }

    override fun readDogInfo(numDog: Int) : String {
        return sharedPreferences.getString(DOG_DATA + numDog, "defaultName,01.01.1999,true")!!
    }

    override fun safeInfo(dogInfo: Dog, numDog: Int): Boolean {
        sharedPreferences.edit().putInt(DOG_QUANTITY, numDog).apply()
        sharedPreferences.edit().putString(DOG_DATA + numDog, dogInfo.toString() + dogInfo.additionalParams()).apply()
        return true
    }

    override fun safeImg(dogImg: String): Boolean  {
        TODO("Not yet implemented")
    }

}