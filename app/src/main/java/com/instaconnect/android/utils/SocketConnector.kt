package com.instaconnect.android.utils

import android.content.Context
import android.util.Log
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URISyntaxException

class SocketConnector {
    companion object {
        private val TAG = "SocketConnector"
        private var instance: SocketConnector? = null
        private var socket: Socket? = null

        fun initSocket(context: Context?) {
            instance = SocketConnector()
            val options = IO.Options()
//            options.query = "token=" + getPreferences(context!!, Constants.API_TOKEN)
            try {
                socket = IO.socket("http://15.222.88.69:5005", options)
                socket!!.connect()
                imChat()
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
        }

        fun disconnect(context: Context?) {
            socket!!.disconnect()
        }

        fun getInstance(): SocketConnector? {
            return instance
        }

        fun getSocket(): Socket? {
            return socket
        }

        private fun imChat() {
            socket!!.on(Socket.EVENT_CONNECT) {
                try {
                    Log.e(TAG, "Server connected.")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.on(Socket.EVENT_DISCONNECT) {
                try {
                    Log.e(TAG, "Server Disconnected.")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            Log.e(TAG, "Connecting... ...")
        }
    }

}