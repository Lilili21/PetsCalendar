package pet.docs.dogs.domain.animals

class SaveDogInfoUseCases(private val fileWithDogInfo : DogInformationStorage) {

    fun saveDogInfo(dogInfo: Dog) : Boolean{
        return fileWithDogInfo.safeInfo(dogInfo, (fileWithDogInfo.readDogQuantity() + 1))
    }

    fun saveDogPhoto(imgUrl: String) : Boolean{
        return fileWithDogInfo.safeImg(imgUrl)
    }

}