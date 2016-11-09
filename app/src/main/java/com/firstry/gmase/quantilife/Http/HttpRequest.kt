package com.firstry.gmase.quantilife.Http

import android.content.Context
import android.util.Base64
import ch.acra.acra.BuildConfig
import org.acra.ACRA
import org.acra.ACRA.LOG_TAG
import org.acra.config.ACRAConfiguration
import org.acra.security.KeyStoreHelper
import org.acra.util.IOUtils
import java.io.BufferedOutputStream
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.security.GeneralSecurityException
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

/**
 * Created by Guille2 on 07/11/2016
 * Have fun
 */
class HttpRequest {

    val debugging = true
    private val UTF8 = "UTF-8"
    private var config: ACRAConfiguration? = null
    private var login: String? = null
    private var password: String? = null
    private var connectionTimeOut = 3000
    private var socketTimeOut = 3000
    private var headers: Map<String, String>? = null

    constructor(config: ACRAConfiguration) {
        this.config = config
    }

    fun setLogin(login: String) {
        this.login = login
    }

    fun setPassword(password: String) {
        this.password = password
    }

    fun setConnectionTimeOut(connectionTimeOut: Int) {
        this.connectionTimeOut = connectionTimeOut
    }

    fun setSocketTimeOut(socketTimeOut: Int) {
        this.socketTimeOut = socketTimeOut
    }

    fun setHeaders(headers: Map<String, String>) {
        this.headers = headers
    }


    /**
     * Posts to a URL.

     * @param url     URL to which to post.
     * *
     * @param content Map of parameters to post to a URL.
     * *
     * @throws IOException if the data cannot be posted.
     */
    @Throws(IOException::class)
    fun send(context: Context, url: URL, method: HttpSender.Method, content: String, type: HttpSender.Type) {

        val urlConnection = url.openConnection() as HttpURLConnection

        // Configure SSL
        if (urlConnection is HttpsURLConnection) {
            try {
                val httpsUrlConnection = urlConnection

                val algorithm = TrustManagerFactory.getDefaultAlgorithm()
                val tmf = TrustManagerFactory.getInstance(algorithm)
                val keyStore = KeyStoreHelper.getKeyStore(context, config!!)

                tmf.init(keyStore)

                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, tmf.trustManagers, null)

                httpsUrlConnection.sslSocketFactory = sslContext.socketFactory
            } catch (e: GeneralSecurityException) {
                ACRA.log.e("LOG_TAG", "Could not configure SSL for ACRA request to " + url, e)
            }

        }

        // Set Credentials
        if (login != null && password != null) {
            val credentials = login + ':' + password
            val encoded = String(Base64.encode(credentials.toByteArray(charset(UTF8)), Base64.NO_WRAP))
            urlConnection.setRequestProperty("Authorization", "Basic " + encoded)
        }

        urlConnection.connectTimeout = connectionTimeOut
        urlConnection.readTimeout = socketTimeOut

        // Set Headers
        urlConnection.setRequestProperty("User-Agent", String.format("Android ACRA %1\$s", BuildConfig.VERSION_NAME)) //sent ACRA version to server
        urlConnection.setRequestProperty("Accept",
                "text/html,application/xml,application/json,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5")
        urlConnection.setRequestProperty("Content-Type", type.contentType)

        if (headers != null) {
            for ((key, value) in headers!!) {
                urlConnection.setRequestProperty(key, value)
            }
        }

        //todo
        val contentAsBytes = content.toByteArray(charset(UTF8))

        // write output - see http://developer.android.com/reference/java/net/HttpURLConnection.html
        urlConnection.requestMethod = method.name
        urlConnection.doOutput = true
        urlConnection.setFixedLengthStreamingMode(contentAsBytes.size)

        // Disable ConnectionPooling because otherwise OkHttp ConnectionPool will try to start a Thread on #connect
        System.setProperty("http.keepAlive", "false")

        urlConnection.connect()

        val outputStream = BufferedOutputStream(urlConnection.outputStream)
        try {
            outputStream.write(contentAsBytes)
            outputStream.flush()
        } finally {
            IOUtils.safeClose(outputStream)
        }

        if (debugging) ACRA.log.d(LOG_TAG, "Sending request to " + url)
        if (debugging) ACRA.log.d(LOG_TAG, "Http " + method.name + " content : ")
        if (debugging) ACRA.log.d(LOG_TAG, content)

        val responseCode = urlConnection.responseCode
        if (debugging) ACRA.log.d(LOG_TAG, "Request response : " + responseCode + " : " + urlConnection.responseMessage)
        if (responseCode >= HttpURLConnection.HTTP_OK && responseCode < HttpURLConnection.HTTP_MULT_CHOICE) {
            // All is good
            ACRA.log.i(LOG_TAG, "Request received by server")
        } else if (responseCode == HttpURLConnection.HTTP_CLIENT_TIMEOUT || responseCode >= HttpURLConnection.HTTP_INTERNAL_ERROR) {
            //timeout or server error. Repeat the request later.
            ACRA.log.w(LOG_TAG, "Could not send ACRA Post responseCode=" + responseCode + " message=" + urlConnection.responseMessage)
            throw IOException("Host returned error code " + responseCode)
        } else if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST && responseCode < HttpURLConnection.HTTP_INTERNAL_ERROR) {
            // Client error. The request must not be repeated. Discard it.
            ACRA.log.w(LOG_TAG, responseCode.toString() + ": Client error - request will be discarded")
        } else {
            ACRA.log.w(LOG_TAG, "Could not send ACRA Post - request will be discarded. responseCode=" + responseCode + " message=" + urlConnection.responseMessage)
        }

        urlConnection.disconnect()
    }

    /**
     * Converts a Map of parameters into a URL encoded Sting.

     * @param parameters Map of parameters to convert.
     * *
     * @return URL encoded String representing the parameters.
     * *
     * @throws UnsupportedEncodingException if one of the parameters couldn't be converted to UTF-8.
     */

    @Throws(UnsupportedEncodingException::class)
    fun getParamsAsFormString(parameters: Map<*, *>): String {

        val dataBfr = StringBuilder()
        for (entry in parameters.entries) {
            if (dataBfr.length != 0) {
                dataBfr.append('&')
            }
            val preliminaryValue = entry.value
            val value = preliminaryValue
            dataBfr.append(URLEncoder.encode(entry.key.toString(), UTF8))
            dataBfr.append('=')
            dataBfr.append(URLEncoder.encode(value.toString(), UTF8))
        }

        return dataBfr.toString()
    }
}