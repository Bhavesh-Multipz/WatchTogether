package com.instaconnect.android.utils

import java.util.regex.Pattern

class RegexUtil {

    companion object{
        fun extractDigits(str: String): String {
            return str.replace("[^0-9]".toRegex(), "")
        }
    }


    /*
     * This method is used to verify whether hostName is valid or not.
     * hostName : Given host name being checked.
     * return : true means hostName is valid, false means hostName is not valid.
     * */
    fun isValidHostName(hostName: String?): Boolean {
        var hostName = hostName
        var ret = true
        if (hostName == null) {
            return false
        }
        hostName = hostName.trim { it <= ' ' }
        if ("" == hostName || hostName.indexOf(" ") != -1) {
            ret = false
        } else {
            // Use regular expression to verify host name.
            var p = Pattern.compile("[a-zA-Z0-9\\.\\-\\_]+")
            var m = p.matcher(hostName)
            ret = m.matches()
            if (ret) {
                var tmp = hostName
                if (hostName.length > 15) {
                    tmp = hostName.substring(0, 15)
                }
                // Use another regular expression to verify the first 15 charactor.
                p = Pattern.compile("((.)*[a-zA-Z\\-\\_]+(.)*)+")
                m = p.matcher(tmp)
                ret = m.matches()
            }
        }
        return ret
    }
}