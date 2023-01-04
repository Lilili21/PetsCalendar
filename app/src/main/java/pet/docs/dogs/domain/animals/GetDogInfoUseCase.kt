package pet.docs.dogs.domain.animals

class GetDogInfoUseCase(private val fileWithDogInfo : DogInformationStorage) {

    fun execute() : Dog{
        val dogDataFromFile = fileWithDogInfo.readDogInfo(fileWithDogInfo.readDogQuantity())
        return Dog(dogDataFromFile.split(",").toTypedArray())
    }

    fun getDogName() : String{
        val ourDog = execute()
        if (!ourDog.name.isNullOrEmpty()){
           return ourDog.name
        }
        return ""
    }
}