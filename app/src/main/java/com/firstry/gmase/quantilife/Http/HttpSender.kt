package com.firstry.gmase.quantilife.Http

import android.content.Context
import android.net.Uri
import org.acra.ACRA
import org.acra.ACRA.LOG_TAG
import org.acra.ACRAConstants
import org.acra.ReportField
import org.acra.config.ACRAConfiguration
import org.acra.sender.ReportSenderException
import org.acra.util.JSONReportBuilder
import java.io.IOException
import java.net.URL
import java.util.*

/**
 * Created by Guille2 on 07/11/2016
 * Have fun
 */
class HttpSender {

    /**
     * Available HTTP methods to send data. Only POST and PUT are currently
     * supported.
     */
    enum class Method {
        POST, PUT
    }

    /**
     * Type of report data encoding, currently supports Html Form encoding and
     * JSON.
     */
    enum class Type {
        /**
         * Send data as a www form encoded list of key/values.
         * @see [Form content types](http://www.w3.org/TR/html401/interact/forms.html.h-17.13.4)
         */
        FORM {
            override val contentType: String
                get() = "application/x-www-form-urlencoded"
        },
        /**
         * Send data as a structured JSON tree.
         */
        JSON {
            override val contentType: String
                get() = "application/json"
        };

        abstract val contentType: String
    }

    private var config: ACRAConfiguration? = null
    private var mFormUri: Uri? = null
    private var mMapping: Map<ReportField, String>? = null
    private var mMethod: Method? = null
    private var mType: Type? = null
    private var mUsername: String? = null
    private var mPassword: String? = null

    /**
     *
     *
     * Create a new HttpSender instance with its destination taken from the supplied config.
     *

     * @param config    AcraConfig declaring the
     * *
     * @param method
     * *            HTTP [Method] to be used to send data. Currently only
     * *            [Method.POST] and [Method.PUT] are available. If
     * *            [Method.PUT] is used, the [ReportField.REPORT_ID]
     * *            is appended to the formUri to be compliant with RESTful APIs.
     * *
     * *
     * @param type
     * *            [Type] of encoding used to send the report body.
     * *            [Type.FORM] is a simple Key/Value pairs list as defined
     * *            by the application/x-www-form-urlencoded mime type.
     * *
     * *
     * @param mapping
     * *            Applies only to [Method.POST] method parameter. If null,
     * *            POST parameters will be named with [ReportField] values
     * *            converted to String with .toString(). If not null, POST
     * *            parameters will be named with the result of
     * *            mapping.get(ReportField.SOME_FIELD);
     */
    constructor(config: ACRAConfiguration, method: Method, type: Type, mapping: Map<ReportField, String>?) {
        this.config = config
        this.mMethod = method
        this.mType = type
        this.mMapping = mapping
    }

    /**
     *
     *
     * Create a new HttpPostSender instance with a fixed destination provided as
     * a parameter. Configuration changes to the formUri are not applied.
     *

     * @param config    AcraConfig declaring the
     * *
     * @param method
     * *            HTTP [Method] to be used to send data. Currently only
     * *            [Method.POST] and [Method.PUT] are available. If
     * *            [Method.PUT] is used, the [ReportField.REPORT_ID]
     * *            is appended to the formUri to be compliant with RESTful APIs.
     * *
     * *
     * @param type
     * *            [Type] of encoding used to send the report body.
     * *            [Type.FORM] is a simple Key/Value pairs list as defined
     * *            by the application/x-www-form-urlencoded mime type.
     * *
     * @param formUri
     * *            The URL of your server-side crash report collection script.
     * *
     * @param mapping
     * *            Applies only to [Method.POST] method parameter. If null,
     * *            POST parameters will be named with [ReportField] values
     * *            converted to String with .toString(). If not null, POST
     * *            parameters will be named with the result of
     * *            mapping.get(ReportField.SOME_FIELD);
     */
    constructor(config: ACRAConfiguration, method: Method, type: Type, formUri: String?, mapping: Map<ReportField, String>?) {
        this.config = config
        mMethod = method
        mFormUri = if (formUri == null) null else Uri.parse(formUri)
        mMapping = mapping
        mType = type
        mUsername = null
        mPassword = null
    }

    /**
     *
     *
     * Set credentials for this HttpSender that override (if present) the ones
     * set globally.
     *

     * @param username
     * *            The username to set for HTTP Basic Auth.
     * *
     * @param password
     * *            The password to set for HTTP Basic Auth.
     */
    @SuppressWarnings("unused")
    fun setBasicAuth(username: String, password: String) {
        mUsername = username
        mPassword = password
    }

    @Throws(ReportSenderException::class)
    fun send(context: Context, content: HttpContent) {

        try {
            var reportUrl = if (mFormUri == null) URL(config!!.formUri()) else URL(mFormUri!!.toString())
            if (ACRA.DEV_LOGGING) ACRA.log.d(LOG_TAG, "Connect to " + reportUrl.toString())

            val login = if (mUsername != null) mUsername else if (isNull(config!!.formUriBasicAuthLogin())) null else config!!.formUriBasicAuthLogin()
            val password = if (mPassword != null) mPassword else if (isNull(config!!.formUriBasicAuthPassword())) null else config!!.formUriBasicAuthPassword()

            val request = HttpRequest(config!!)
            request.setConnectionTimeOut(config!!.connectionTimeout())
            request.setSocketTimeOut(config!!.socketTimeout())
            request.setLogin(login!!)
            request.setPassword(password!!)
            request.setHeaders(config!!.httpHeaders)

            // Generate report body depending on requested type
            val reportAsString: String
            reportAsString = content.content


            // Adjust URL depending on method
            when (mMethod) {
                Method.POST -> {
                }
                Method.PUT -> reportUrl = URL(reportUrl.toString() + '/' + content.contentId)
                else -> throw UnsupportedOperationException("Unknown method: " + mMethod!!.name)
            }
            request.send(context, reportUrl, mMethod!!, reportAsString, mType!!)

        } catch (e: IOException) {
            throw ReportSenderException("Error while sending " + config!!.reportType()
                    + " report via Http " + mMethod!!.name, e)
        } catch (e: JSONReportBuilder.JSONReportException) {
            throw ReportSenderException("Error while sending " + config!!.reportType()
                    + " report via Http " + mMethod!!.name, e)
        }

    }


    private fun remap(report: Map<ReportField, String>): Map<String, String> {

        var fields = config!!.reportFields
//        if (fields.isEmpty()) {
//            fields = ImmutableSet<ReportField>(ACRAConstants.DEFAULT_REPORT_FIELDS)
//        }

        val finalReport = HashMap<String, String>(report.size)
        for (field in fields) {
            if (mMapping == null || mMapping!![field] == null) {
                finalReport.put(field.toString(), report[field].toString())
            } else {
                finalReport.put(mMapping!![field]!!, report[field].toString())
            }
        }
        return finalReport
    }

    private fun isNull(aString: String?): Boolean {
        return aString == null || ACRAConstants.NULL_VALUE == aString
    }
}