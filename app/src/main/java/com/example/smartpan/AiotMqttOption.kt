package com.example.smartpan

import java.math.BigInteger
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal class AiotMqttOption {
    var username = ""
        private set
    var password = ""
        private set
    var clientId = ""
        private set

    /**
     * èŽ·å–Mqttå»ºè¿žé€‰é¡¹å¯¹è±¡Ø
     * @param productKey äº§å“ç§˜é’¥
     * @param deviceName è®¾å¤‡åç§°
     * @param deviceSecret è®¾å¤‡æœºå¯†
     * @return AiotMqttOptionå¯¹è±¡æˆ–è€…NULL
     */
    fun getMqttOption(
        productKey: String?,
        deviceName: String?,
        deviceSecret: String?
    ): AiotMqttOption? {
        if (productKey == null || deviceName == null || deviceSecret == null) {
            return null
        }
        try {
            val timestamp = java.lang.Long.toString(System.currentTimeMillis())

            // clientId
            clientId = productKey + "." + deviceName + "|timestamp=" + timestamp +
                    ",_v=paho-android-1.0.0,securemode=2,signmethod=hmacsha256|"

            // userName
            username = "$deviceName&$productKey"

            // password
            val macSrc = "clientId" + productKey + "." + deviceName + "deviceName" +
                    deviceName + "productKey" + productKey + "timestamp" + timestamp
            val algorithm = "HmacSHA256"
            val mac: Mac = Mac.getInstance(algorithm)
            val secretKeySpec = SecretKeySpec(deviceSecret.toByteArray(), algorithm)
            mac.init(secretKeySpec)
            val macRes: ByteArray = mac.doFinal(macSrc.toByteArray())
            password = java.lang.String.format("%064x", BigInteger(1, macRes))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return this
    }

//    val PRODUCTKEY = "a11xsrW****"
//    val DEVICENAME = "paho_android"
//    val DEVICESECRET = "tLMT9QWD36U2SArglGqcHCDK9rK9****"
//
//    /* Obtain the MQTT connection information clientId, username, and password. */
//    val aiotMqttOption = AiotMqttOption().getMqttOption(PRODUCTKEY, DEVICENAME, DEVICESECRET)
//    if (aiotMqttOption == null) {
//        Log.d(SmartPanControlActivity.TAG, "device info error")
//    } else {
////            clientId = aiotMqttOption.getClientId()
////            userName = aiotMqttOption.getUsername()
////            passWord = aiotMqttOption.getPassword()
//    }
//
//    /* Create an MqttConnectOptions object and configure the username and password. */
//    val mqttConnectOptions = MqttConnectOptions()
////        mqttConnectOptions.userName = userName
////        mqttConnectOptions.password = passWord.toCharArray()
}