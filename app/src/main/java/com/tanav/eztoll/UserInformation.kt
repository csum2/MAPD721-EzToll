package com.tanav.eztoll

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.preference.PreferenceManager
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.tanav.eztoll.models.User
import kotlinx.android.synthetic.main.activity_user_information.*

class UserInformation : AppCompatActivity() {

    private lateinit var fname_et: EditText
    private lateinit var lname_et: EditText
    private lateinit var gender_et: EditText
    private lateinit var street_et: EditText
    private lateinit var country_et: EditText
    private lateinit var city_et: EditText
    private lateinit var postalCode_et: EditText

    private lateinit var fname: String
    private lateinit var lname: String
    private lateinit var gender: String
    private lateinit var street: String
    private lateinit var country: String
    private lateinit var city: String
    private lateinit var postalCode: String
    private lateinit var url: String

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_information)

        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val userUniqueID = pref.getString(AppConst.PREF_UID, "unknown")
        Log.d("sch", "UserInformation, onCreate(), userUniqueID:$userUniqueID")

        fname_et = findViewById(R.id.fistName)
        lname_et = findViewById(R.id.lastName)
        gender_et = findViewById(R.id.gender)
        street_et = findViewById(R.id.addressStreet)
        country_et = findViewById(R.id.addressCountry)
        city_et = findViewById(R.id.addressCity)
        postalCode_et = findViewById(R.id.addressPostalCode)

        url = "no pic"

        database = Firebase.database.getReference("Users")

        submitBtn.setOnClickListener {
            if(fname_et.text.toString().isEmpty()){
                fname_et.error = "Please enter first name"
            }else{
                fname = fname_et.text.toString()
            }

            if(lname_et.text.toString().isEmpty()){
                lname_et.error = "Please enter last name"
            }else{
                lname = lname_et.text.toString()
            }

            if(gender_et.text.toString().isEmpty()){
                gender_et.error = "Please enter a gender"
            }else{
                gender = gender_et.text.toString()
            }

            if(street_et.text.toString().isEmpty()){
                street_et.error = "Please enter you street number and name"
            }else{
                street = street_et.text.toString()
            }

            if(country_et.text.toString().isEmpty()){
                country_et.error = "Please enter a country"
            }else{
                country = country_et.text.toString()
            }

            if(city_et.text.toString().isEmpty()){
                city_et.error = "PLease enter a city"
            }else{
                city = city_et.text.toString()
            }

            if(postalCode_et.text.toString().isEmpty()){
                postalCode_et.error = "Please enter a postal code"
            }else{
                postalCode = postalCode_et.text.toString()
            }
            val userValues = User(fname,lname,gender,street,country,city,postalCode,url)
            database.child(userUniqueID!!).setValue(userValues)
            //val intent = Intent(this, UserInterface::class.java)
            //startActivity(intent)
            finish()
        }
    }
}