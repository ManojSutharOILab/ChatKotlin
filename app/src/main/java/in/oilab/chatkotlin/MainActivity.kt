package `in`.oilab.chatkotlin

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    lateinit var mSocket: Socket
    lateinit var messageTextView: TextView
    lateinit var edtChat: EditText
    lateinit var sendButton: Button
    lateinit var id: String

    val USERNAME = "Manoj"
   // val SERVER_URL = "http://192.168.1.48:3000"
    val SERVER_URL = "https://oilab.herokuapp.com"

 /**
  * io.sockets.in('user1@example.com').emit('new_msg', {msg: 'hello'});
  * put this on server side
  *
  * */


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSocket()

        initViews()

        mSocket.connect()
        mSocket.on(Socket.EVENT_CONNECT, onConnect)
        mSocket.on("msg", onMessageRecevied)

        mSocket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.d("fail", "EVENT_CONNECT_ERROR ${args[0]}")
        }

        sendButton.setOnClickListener {
            edtChat.setText("")

            val jsonObject = JSONObject()
            jsonObject.put("email", USERNAME)
            jsonObject.put("message", edtChat.text.toString().trim())

            mSocket.emit("join",jsonObject)

            //mSocket.emit("msg", edtChat.text.toString().trim())
        }


    }

    private fun initViews() {
        id = intent.getStringExtra("id").toString()
        messageTextView = findViewById(R.id.message)
        edtChat = findViewById(R.id.edtChat)
        sendButton = findViewById(R.id.sendButton)
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
        //mSocket.emit("msg", "Hello, I am $USERNAME from android app")
        Log.d("success", "Connected")

        val jsonObject = JSONObject()
        jsonObject.put("email", USERNAME)
        jsonObject.put("message", "")

        //Join by unique id
        mSocket.emit("join", jsonObject)

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