package com.instaconnect.android.utils

import android.util.DisplayMetrics
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.util.Base64
import java.lang.Exception
import java.nio.charset.Charset
import java.util.*

class Util {

    companion object {

        fun milesToKM(miles: Int): Int {
            return (miles * 1.60).toInt()
        }

        fun formateMilliSeccond(milliseconds: Long): String {
            var finalTimerString = ""
            var secondsString = ""

            // Convert total duration into time
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()

            // Add hours if there
            if (hours > 0) {
                finalTimerString = "$hours:"
            }

            // Prepending 0 to seconds if it is one digit
            secondsString = if (seconds < 10) {
                "0$seconds"
            } else {
                "" + seconds
            }
            finalTimerString = "$finalTimerString$minutes:$secondsString"

            //      return  String.format("%02d Min, %02d Sec",
            //                TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            //                TimeUnit.MILLISECONDS.toSeconds(milliseconds) -
            //                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

            // return timer string
            return finalTimerString
        }

        fun blur(context: Context?, image: Bitmap): Bitmap {
            val width = Math.round(image.width * 0.4f)
            val height = Math.round(image.height * 0.4f)
            var inputBitmap = Bitmap.createScaledBitmap(image, width, height, true)
            try {
                inputBitmap = RGB565toARGB888(inputBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val outputBitmap = Bitmap.createBitmap(inputBitmap)
            val rs = RenderScript.create(context)
            val theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
            val tmpIn = Allocation.createFromBitmap(rs, inputBitmap)
            val tmpOut = Allocation.createFromBitmap(rs, outputBitmap)
            theIntrinsic.setRadius(25f)
            theIntrinsic.setInput(tmpIn)
            theIntrinsic.forEach(tmpOut)
            tmpOut.copyTo(outputBitmap)
            return outputBitmap
        }

        fun RGB565toARGB888(img: Bitmap): Bitmap {
            val numPixels = img.width * img.height
            val pixels = IntArray(numPixels)

            //Get JPEG pixels.  Each int is the color values for one pixel.
            img.getPixels(pixels, 0, img.width, 0, 0, img.width, img.height)

            //Create a Bitmap of the appropriate format.
            val result = Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)

            //Set RGB pixels.
            result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
            return result
        }

        fun startEndPosition(sentence: String, word: String): IntArray {
            val startingPosition = sentence.indexOf(word)
            val endingPosition = startingPosition + word.length
            val values = IntArray(2)
            values[0] = startingPosition
            values[1] = endingPosition
            return values
        }
    }


    internal fun getDeviceHeight(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        return height
    }

    internal fun getDeviceWidth(context: Context): Int {
        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        return displayMetrics.widthPixels
    }

    fun createUniqueCode(): String {
        val min = 100
        val max = 1000000000
        val r = Random()
        val random = r.nextInt(max - min + 1) + min
        val time = System.currentTimeMillis()
        return (random + time).toString()
    }

    fun getGooglePlaceImage(lat: String, longitude: String): String {
        return ("http://maps.google.com/maps/api/staticmap?center=" + lat + "," + longitude +
                "&zoom=15&scale=2&size=600x300&maptype=roadmap&markers=color:red%7C%7C"
                + lat + "," + longitude)
    }

    fun encodeBase64(inputData: ByteArray?): String {
        return Base64.encodeToString(
            inputData,
            Base64.NO_WRAP
        )
    }

    fun encodeBase64(inputData: String): String {
        return Base64.encodeToString(
            getByte(inputData),
            Base64.NO_WRAP
        )
    }

    fun getByte(inputData: String): ByteArray? {
        return try {
            inputData.toByteArray(charset("UTF-8"))
        } catch (e: Exception) {
            null
        }
    }

    fun decodeBase64(name: String?): String {
        return try {
            val data = Base64.decode(name, Base64.NO_WRAP)
            String(data, Charset.forName("UTF-8"))
        } catch (e: Exception) {
            e.printStackTrace()
            "empty"
        }
    }


}