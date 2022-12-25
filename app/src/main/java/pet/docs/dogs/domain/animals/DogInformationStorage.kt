package pet.docs.dogs.domain.animals


interface DogInformationStorage {

    fun ifFileExist(): Boolean

    fun readDogInfo(numDog: Int) : String

    fun readDogQuantity(): Int

    fun safeInfo(dogInfo: Dog, numDog: Int) : Boolean

    fun safeImg(dogImg: String) : Boolean
}