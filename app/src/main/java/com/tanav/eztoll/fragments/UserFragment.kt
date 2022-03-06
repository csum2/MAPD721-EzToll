package com.tanav.eztoll.fragments
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.tanav.eztoll.Interfaces.ImageListener
import com.tanav.eztoll.Interfaces.ImageUploadListener
import com.tanav.eztoll.Models.User
import com.tanav.eztoll.R
import com.tanav.eztoll.Util

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class UserFragment : Fragment() {
    private lateinit var myContext: Context
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var nameOfUser_TV: TextView
    private lateinit var profilePic_IV: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var first_Name : String
    private lateinit var last_Name : String
    private lateinit var gender_ : String
    private lateinit var address_Street: String
    private lateinit var address_Country : String
    private lateinit var address_City : String
    private lateinit var address_postalCode : String

    var imgeUri: Uri?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myContext = requireActivity().applicationContext
        val myView = inflater.inflate(R.layout.fragment_user, container, false)

        nameOfUser_TV = myView.findViewById(R.id.NameOfUser)
        profilePic_IV = myView.findViewById(R.id.profile_image)

        auth = Firebase.auth

        database = Firebase.database.getReference("Users")
        val userUniqueID = auth.currentUser!!.uid
        Log.d("TAG", "message" + userUniqueID)

        database.child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val user = snapshot.getValue(User::class.java)
                    nameOfUser_TV.text = user!!.firstName
                    val profileImageURl = user.image
                    if(profileImageURl.equals("no pic")){
                        Toast.makeText(myContext, "No Profile Picture", Toast.LENGTH_SHORT).show()
                    }else{
                        Picasso.get().load(profileImageURl).into(profilePic_IV)
                    }


                    first_Name = user!!.firstName
                    last_Name = user!!.lastName
                    gender_ = user!!.gender
                    address_Street = user!!.streetName
                    address_Country = user!!.streetCountry
                    address_City = user!!.streetCity
                    address_postalCode = user!!.postalCode


                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        profilePic_IV.setOnClickListener {
            Util.readImageFromGallery(activity, object : ImageListener {
                override fun onImageLoaded(error: Boolean, uri: Uri?, bitmap: Bitmap?) {

                    if (!error) {

                        imgeUri = uri!!
                        profilePic_IV.setImageURI(uri)

                        Util.uploadImage(imgeUri,object: ImageUploadListener {
                            override fun onUpload(error: Boolean, Message: String?, url: String?) {
                                val userValues = User(first_Name, last_Name, gender_, address_Street, address_Country, address_City, address_postalCode,url)
                                database.child(userUniqueID!!).setValue(userValues)
                            }

                        })

                    } else {

                    }
                }
            })


        }

        return myView
    }
}