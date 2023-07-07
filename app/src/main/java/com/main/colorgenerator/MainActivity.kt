package com.main.colorgenerator

import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.colorgenerator.data.ColorDao
import com.example.colorgenerator.data.ColorDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.main.colorgenerator.Adapter.ColorAdapter
import com.main.colorgenerator.Model.ColorModel
import com.main.colorgenerator.ViewModel.ColorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var textView: TextView
    private lateinit var colorAdapter: ColorAdapter
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var colorList = mutableListOf<ColorModel>()
    private lateinit var colorDao: ColorDao
    lateinit var colorDatabase: ColorDatabase
    private lateinit var relativeLayout: RelativeLayout
    private lateinit var relativeLayoutSync: RelativeLayout
    private lateinit var db:FirebaseFirestore
    private val USER_ID_PREF_KEY = "user_id"
    private val USERS_COLLECTION = "users"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        colorDatabase = Room.databaseBuilder(this, ColorDatabase::class.java, "color-db").build()
        colorDao = colorDatabase.colorDao()
        db = FirebaseFirestore.getInstance()

        textView = findViewById(R.id.unSynced)
        recyclerView = findViewById(R.id.recyclerView)
        relativeLayout = findViewById(R.id.relativeLayoutAddColor)
        relativeLayoutSync = findViewById(R.id.relativeLayout)

        layoutManager = GridLayoutManager(this, 2)
        recyclerView.layoutManager = layoutManager
        colorAdapter = ColorAdapter()
        recyclerView.adapter = colorAdapter

        colorDao.getAllColors().observe(this, Observer { colorModels ->
            colorAdapter.setColorList(colorModels)
            colorList = colorModels.toMutableList()
            val unSynced = ColorViewModel.countUnSyncedColors(colorList)
            textView.text = unSynced.toString()
        })
        relativeLayoutSync.setOnClickListener {
            if(ColorViewModel.isInternetAvailable(this)) {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                val userId = sharedPreferences.getString(USER_ID_PREF_KEY, null)

                if (userId == null) {
                    val newUserId = db.collection(USERS_COLLECTION).document().id
                    sharedPreferences.edit().putString(USER_ID_PREF_KEY, newUserId).apply()
                    performSync(newUserId)
                } else {
                    performSync(userId)
                }
            }else{
                Toast.makeText(this,"No Internet",Toast.LENGTH_SHORT).show()
            }

        }


        relativeLayout.setOnClickListener {
            val randomColor = ColorViewModel.generateRandomColor()
            val currentTime = System.currentTimeMillis()
            val colorModel = ColorModel(Color.parseColor(randomColor),currentTime,randomColor,false)
            colorList.add(colorModel)
            GlobalScope.launch(Dispatchers.IO) {
                colorDao.insertColor(colorModel)
            }
        }

    }


    private fun performSync(userId:String){
        val colorsToSync = colorList.filter { !it.synced }
        val colorRef = db.collection("color").document(userId)

        colorRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    colorRef.update("randomColors", FieldValue.arrayUnion(*colorsToSync.toTypedArray()))
                        .addOnSuccessListener {
                            colorsToSync.forEach { colorModel ->
                                colorModel.synced = true
                            }
                            GlobalScope.launch(Dispatchers.IO) {
                                colorDao.updateColors(colorsToSync)
                            }
                            colorAdapter.notifyDataSetChanged()


                        }
                        .addOnFailureListener { e ->
                        }
                } else {
                    val data = hashMapOf("randomColors" to colorsToSync)
                    colorRef.set(data)
                        .addOnSuccessListener {

                        }
                        .addOnFailureListener { e ->
                        }
                }
            }
    }

}