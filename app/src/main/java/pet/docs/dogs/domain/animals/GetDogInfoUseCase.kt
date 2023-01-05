package pet.docs.dogs.domain.animals

class GetDogInfoUseCase(private val fileWithDogInfo : DogInformationStorage) {

    fun getDog() : Dog{
        val dogDataFromFile = fileWithDogInfo.readDogInfo(fileWithDogInfo.readDogQuantity())
        return Dog(dogDataFromFile.split(",").toTypedArray())
    }

    fun getDogName() : String{
        val ourDog = getDog()
        if (!ourDog.name.isNullOrEmpty()){
           return ourDog.name
        }
        return ""
    }
}