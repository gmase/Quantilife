package com.firstry.gmase.quantilife

import android.app.Application
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

/**
 * Created by Guille2 on 02/11/2016
 * Have fun
 */
@ReportsCrashes(
        formUri = "https://gmase.cloudant.com/acra-lifeindex/_design/acra-storage/_update/report",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        formUriBasicAuthLogin = "dsilikedisterverenteress",
        formUriBasicAuthPassword = "30965bbb5aec1227f10bb268e2b8ed1967fb092b",
        // Your usual ACRA configuration
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ACRA.init(this)
    }

}