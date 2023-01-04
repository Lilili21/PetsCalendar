package pet.docs.dogs.presentation

import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_passport_create.*
import pet.docs.dogs.R
import pet.docs.dogs.data.animalInfo.DogInformationStorageImpl
import pet.docs.dogs.domain.animals.Dog
import pet.docs.dogs.domain.animals.SaveDogInfoUseCases
import pet.docs.dogs.domain.utils.Utils.Companion.checkBirthdayDate
import pet.docs.dogs.domain.utils.Utils.Companion.checkIfParameterEmpty
import pet.docs.dogs.domain.utils.Utils.Companion.datePattern

private const val TAG = "PASSPORT_CREATE_ACTIVITY"

class PassportCreateActivity :OnBackPressedToMainActivity(){

    private val userRepository by lazy (LazyThreadSafetyMode.NONE){ DogInformationStorageImpl(context = applicationContext) }

    val img: ImageView? = null
    val Pick_image = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passport_create)
        datePattern(birthday, TAG)
        //img = findViewById<View>(R.id.avatar) as ImageView
    }

    //сохранение информации о собаке
    fun saveButtonClicked(view: View){
        var selectedRadioButton: RadioButton? = null
        val selectedRbText: String?

        //считывание параметров о собаке
        if (radioGroup.checkedRadioButtonId != -1) {
            selectedRadioButton = findViewById(radioGroup.checkedRadioButtonId)
        } else {
            Toast.makeText(baseContext, getString(R.string.sex_notification), Toast.LENGTH_SHORT).show()
        }

        if (selectedRadioButton != null && checkInfo(name, birthday)) {
            selectedRbText = selectedRadioButton.text.toString()
            val ourDog = readDogInformation(selectedRbText)
            val saveDogInfo = SaveDogInfoUseCases(userRepository)
            saveDogInfo.saveDogInfo(ourDog)
            val intent = Intent(this@PassportCreateActivity, PassportShowActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun readDogInformation(dogSex: String) : Dog{
        val ourDog = Dog(
            name.text.toString(), birthday.text.toString(),
            dogSex == getString(R.string.male)/*StringConstantsRu.SEX_BOY*/
        )
        if (breed.text.toString().isNotEmpty()) ourDog.breed = breed.text.toString()
        if (color.text.toString().isNotEmpty()) ourDog.color = color.text.toString()
        if (cardNum.text.toString().isNotEmpty()) ourDog.cardNumber =
            cardNum.text.toString()
        if (brandNum.text.toString().isNotEmpty()) ourDog.brandNumber =
            brandNum.text.toString()
        return ourDog
    }

    //проверяем наличие всей информации о питомце и корректность даты рождения
    private fun checkInfo(
        name: EditText,
        birthday: EditText,
    ): Boolean {
        val backToast: Toast
        val correctName = checkIfParameterEmpty(name, getString(R.string.name_notification))
        var correctBirthday = checkIfParameterEmpty(birthday, getString(R.string.birthday_notification))

        //проверка что дата указана корректно
        if (correctBirthday && !checkBirthdayDate(birthday.text.toString())) {
            backToast = Toast.makeText(baseContext, getString(R.string.incorrect_date_notification), Toast.LENGTH_SHORT)
            backToast.show()
            correctBirthday = false
        }
        return correctName && correctBirthday
    }

    //Обрабатываем результат выбора в галерее:
    /* protected fun onActivityResult(requestCode: Int, resultCode: Int, imageReturnedIntent: Intent) {
         super.onActivityResult(requestCode, resultCode, imageReturnedIntent)
         if (resultCode == RESULT_OK) {
             try {
                 //Получаем URI изображения, преобразуем его в Bitmap
                 //объект и отображаем в элементе ImageView нашего интерфейса:
                 val uriAvatar: Uri? = imageReturnedIntent.data
                 val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                 val cursor: Cursor? = contentResolver.query(
                     uriAvatar,
                     filePathColumn, null, null, null
                 )
                 cursor.moveToFirst()
                 val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
                 val picturePath: String = cursor.getString(columnIndex)
                 cursor.close()
                 StaticValues.uriAvatarString = picturePath
                 val imageStream: InputStream? = contentResolver.openInputStream(uriAvatar)
                 val selectedImage = BitmapFactory.decodeStream(imageStream)
                 img.setImageBitmap(selectedImage)
                 StaticValues.dogHasAvatar = true
             } catch (e: FileNotFoundException) {
                 e.printStackTrace()
             }
         }
     }*/
}