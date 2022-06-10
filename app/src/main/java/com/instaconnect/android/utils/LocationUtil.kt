package com.instaconnect.android.utils

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import java.io.IOException
import java.lang.Exception
import java.util.*

object LocationUtil {
    fun showLocationOnMap(context: Context, latitude: String, longitude: String) {
        val i = Intent(
            Intent.ACTION_VIEW, Uri.parse(
                "http://maps.google.com/maps?q=loc:$latitude,$longitude"
            )
        )
        val mapsPackageName = "com.google.android.apps.maps"
        try {
            i.setClassName(mapsPackageName, "com.google.android.maps.MapsActivity")
            i.setPackage(mapsPackageName)
            context.startActivity(i)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCountryName(context: Context): String {
        var country_name: String? = null
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val geocoder = Geocoder(context)
        try {
            for (provider in lm.allProviders) {
                val location = lm.getLastKnownLocation(provider!!)
                if (location != null) {
                    try {
                        val addresses =
                            geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        if (addresses != null && addresses.size > 0) {
                            country_name = addresses[0].countryName
                            break
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (country_name == null) country_name = "india"
        return country_name
    }

    fun getCountryName(context: Context, latitude: Double, longitude: Double): String? {
        var country_name: String? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1)
            var result: Address
            if (addresses != null && !addresses.isEmpty()) {
                country_name = addresses[0].countryName
            }
        } catch (ignored: IOException) {
            //do something
        }
        if (country_name == null) country_name =
            context.resources.configuration.locale.displayCountry
        return country_name
    }

    fun getCountryName(context: Context?, location: Location?): String? {
        if (location == null) return null
        var country_name: String? = null
        val geocoder = Geocoder(context, Locale.getDefault())
        var addresses: List<Address>? = null
        try {
            addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            var result: Address
            if (addresses != null && !addresses.isEmpty()) {
                country_name = addresses[0].countryName
            }
        } catch (ignored: IOException) {
            //do something
        }
        return country_name
    }
}