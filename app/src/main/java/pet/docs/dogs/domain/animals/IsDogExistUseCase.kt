package pet.docs.dogs.domain.animals

class IsDogExistUseCase(private val fileWithDogInfo : DogInformationStorage) {

    fun execute() : Boolean {
        return fileWithDogInfo.ifFileExist()
    }
}