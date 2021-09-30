package `in`.oilab.chatkotlin

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter


class MainActivity : AppCompatActivity() {

    lateinit var mSocket: Socket
    lateinit var messageTextView: TextView
    val USERNAME = "9636579852"
    val SERVER_URL = "http://192.168.1.48:3000"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSocket()

        initViews()

        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on("msg",onMessageRecevied)

        mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.d("fail", "EVENT_CONNECT_ERROR ${args[0]}")
        }

    }

    private fun initViews() {
        messageTextView = findViewById(R.id.message)
    }

    private fun initSocket() {
        //Let's connect to our Chat room! :D
        try {
            mSocket = IO.socket(SERVER_URL)
            //Log.d("success", mSocket.id())

        } catch (e: Exception) {
            //e.printStackTrace()
            Log.d("fail", "Failed to connect ${e.message}")
        }
    }


    private var onConnect = Emitter.Listener {
        mSocket.emit("msg", "Hello from Android")
        Log.d("success", "Connected")
    }

    private var onMessageRecevied = Emitter.Listener {
        runOnUiThread {
            val text = messageTextView.text
            messageTextView.text = "$text \n ${it[0].toString()}"
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mSocket.disconnect()
    }
}