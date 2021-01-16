package com.example.smartpan

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.example.smartpan.databinding.ActivitySmartPanControlBinding
import kotlinx.android.synthetic.main.activity_smart_pan_control.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.json.JSONObject


class SmartPanControlActivity : AppCompatActivity(), MqttCallbackExtended {

    private lateinit var binding: ActivitySmartPanControlBinding
    private lateinit var mqttClient: MqttClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmartPanControlBinding.inflate(layoutInflater)
        setContentView(binding.root)
        runCatching {
            mqttClient = MqttClient(MQTT_URL, "AndroidThingSub", MemoryPersistence())
            mqttClient.setCallback(this)
            mqttClient.connect()
            mqttClient.subscribe(PUB_TOPIC)
        }.onFailure {
            Log.d(TAG, it.message.toString())
        }

        binding.btnSetTempreture.setOnClickListener {
            publishMessage(binding.sbTemperature.progress.toFloat(), 2)
        }
        binding.sbTemperature.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                val `val` = progress * (seekBar!!.width - 2 * seekBar.thumbOffset) / seekBar.max
                settedTemperature.text = "Setted temp: $progress"
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    override fun connectionLost(cause: Throwable?) {
        Log.d(TAG, "Connection lost ${cause?.message}")
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (message != null) {
            runOnUiThread {
                getSmartPanData(String(message.payload))
            }
        }
        Log.d(TAG, "Message arrived: ${message?.payload?.let { String(it) }}")
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        Log.d(TAG, "Delivery complete: ${token?.message}")
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        Log.d(TAG, "Connect complete: $serverURI")
    }

    private fun getSmartPanData(jsonString: String) {
        val jsonObject = JSONObject(jsonString)
        val data = SmartPanData(
            jsonObject.getString("current_temperature").toFloat(),
            jsonObject.getString("set_temperature").toFloat(),
            jsonObject.getString("current_pwm").toInt(),
            WorkMode.NO_SET
        )
        binding.tvCurrentTempreture.text = "${data.current_temperature} °"
    }

    private fun publishMessage(setTemperature: Float, workMode: Int) {
        try {
            val message = MqttMessage()
            val jsonMessage = JSONObject()
            jsonMessage.put("set_temperature", setTemperature)
            jsonMessage.put("work_mode", workMode)
            message.payload = jsonMessage.toString().toByteArray()
            message.qos = 0
            mqttClient.publish(SUB_TOPIC, message)
        } catch (e: MqttException) {
            Log.e(TAG, e.toString())
        }
    }

    companion object {
        const val TAG = "MQTT"
        const val MQTT_URL = "tcp://test.mosquitto.org:1883"
        const val PUB_TOPIC = "/SMART_PAN/DEVICE"
        const val SUB_TOPIC = "/SMART_PAN/DEVICE/SET_CONFIG"
        const val DEVICE_ID = "DEVICE_ID"

    }
}

// Android Mqtt Client version implementation
//        val clientId = MqttClient.generateClientId()
//        mqttClient = MqttAndroidClient(applicationContext, MQTT_URL, clientId, MemoryPersistence())
//        mqttClient.setCallback(this)
//        val mqttConnectOptions = MqttConnectOptions()
//        mqttConnectOptions.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
//        mqttConnectOptions.setAutomaticReconnect(true);
//        mqttConnectOptions.setCleanSession(false)
//        mqttConnectOptions.userName = ""
//        mqttConnectOptions.password = "".toCharArray()
/* Establish an MQTT connection */
//        try {
//            mqttClient.connect()
//            mqttClient.subscribe(SUB_TOPIC)
//            mqttClient.connect(mqttConnectOptions, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    Log.d(TAG, "connect succeed")
//                    subscribeTopic(SUB_TOPIC)
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    Log.d(TAG, "connect failed")
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }

//    private fun subscribeTopic(topic: String?) {
//        try {
//            mqttClient.subscribe(topic, 0, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    Log.d(TAG, "subscribed succeed")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    Log.d(TAG, "subscribed failed")
//                }
//            })
//        } catch (e: MqttException) {
//            e.printStackTrace()
//        }
//    }

//    fun publishMessage(payload: String) {
//        try {
//            if (mqttClient.isConnected) {
//                mqttClient.connect()
//            }
//            val message = MqttMessage()
//            message.payload = payload.toByteArray()
//            message.qos = 0
//            mqttClient.publish(PUB_TOPIC, message, null, object : IMqttActionListener {
//                override fun onSuccess(asyncActionToken: IMqttToken) {
//                    Log.d(TAG, "publish succeed! ")
//                }
//
//                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
//                    Log.d(TAG, "publish failed!")
//                }
//            })
//        } catch (e: MqttException) {
//            Log.e(TAG, e.toString())
//            e.printStackTrace()
//        }
//    }