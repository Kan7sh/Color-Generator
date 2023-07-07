package com.main.colorgenerator.ViewModel

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import com.main.colorgenerator.Model.ColorModel
import kotlin.random.Random

class ColorViewModel {

    companion object{
        fun generateRandomColor(): String {
            val random = Random.Default

            val red = random.nextInt(256)
            val green = random.nextInt(256)
            val blue = random.nextInt(256)

            val color = String.format("#%02x%02x%02x", red, green, blue)
            return color
        }

        fun isInternetAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val networkCapabilities = connectivityManager.activeNetwork ?: return false
                val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

                return when {
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                    else -> false
                }
            } else {
                val networkInfo = connectivityManager.activeNetworkInfo
                return networkInfo != null && networkInfo.isConnected
            }
        }

        fun countUnSyncedColors(colorList: List<ColorModel>): Int {
            var count = 0
            for (colorModel in colorList) {
                if (!colorModel.synced) {
                    count++
                }
            }
            return count
        }
    }


    fun syncDataToFirebase() {
    }


}
