package pet.docs.dogs.presentation

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import kotlinx.android.synthetic.main.activity_passport_show.*
import pet.docs.dogs.R
import pet.docs.dogs.data.animalInfo.DogInformationStorageImpl
import pet.docs.dogs.domain.animals.Dog
import pet.docs.dogs.domain.animals.GetDogInfoUseCase
import pet.docs.dogs.domain.animals.IsDogExistUseCase

private const val TAG = "PASSPORT_SHOW_ACTIVITY"

class PassportShowActivity : OnBackPressedToMainActivity() {

    private val userRepository by lazy (LazyThreadSafetyMode.NONE){ DogInformationStorageImpl(context = applicationContext) }
    private lateinit var ourDog : Dog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport_show)

        //загрузка данных из файла
        val dogInfoStorage = GetDogInfoUseCase(userRepository)
        val dogExistInStorage = IsDogExistUseCase(userRepository)
        if (dogExistInStorage.execute()) {
            ourDog = dogInfoStorage.execute()
            addDogsFromFile()
        } else {
            Log.e(TAG, "Failed to read dog info in the file")
        }

        //загрузка аватарки по ссылке в галерее
        /*   if (StaticValues.dogHasAvatar) {
               try {
                   loadAvatar(img)
               } catch (e: LoadAvatarException) {
                   Log.e(TAG, "Add permission to use internal storage for with App Please")
               }
           }*/
    }

    private fun addDogsFromFile(){
        nameValue.text = ourDog.name
        birthdayValue.text = ourDog.birthDay
        genderValue.text = ourDog.getGender(this)

        var rowNumber = 1
        if (!TextUtils.isEmpty(ourDog.breed)) {
            addTextView(getString(R.string.breed), ourDog.breed!!, rowNumber)
            rowNumber++
        }
        if (!TextUtils.isEmpty(ourDog.color)) {
            addTextView(getString(R.string.color), ourDog.color!!, rowNumber)
            rowNumber++
        }
        if (!TextUtils.isEmpty(ourDog.cardNumber)) {
            addTextView(getString(R.string.tattoo_number), ourDog.cardNumber!!, rowNumber)
            rowNumber++
        }
        if (!TextUtils.isEmpty(ourDog.brandNumber)) {
            addTextView(getString(R.string.microchip_number), ourDog.brandNumber!!, rowNumber)
        }
    }

    private fun addTextView(title: String, value: String, number: Int) {
        when(number) {
            1 -> {
                first.text = title
                firstValue.text = value
            }
            2 -> {
                second.text = title
                secondValue.text = value
            }
            3 -> {
                third.text = title
                thirdValue.text = value
            }
            4 -> {
                fourth.text = title
                fourthValue.text = value
            }
            else -> Log.e(TAG, "Failed to put info on the screen")
        }
    }


    /*  @Throws(LoadAvatarException::class)
      private fun loadAvatar(img: ImageView) {
          val selectedImage = BitmapFactory.decodeFile(StaticValues.uriAvatarString)
          img.setImageBitmap(selectedImage)
          if (selectedImage == null) throw LoadAvatarException()
      }*/
}