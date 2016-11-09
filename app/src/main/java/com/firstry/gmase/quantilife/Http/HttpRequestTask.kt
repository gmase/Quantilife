package com.firstry.gmase.quantilife.Http

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import com.firstry.gmase.quantilife.model.AppDay
import com.firstry.gmase.quantilife.model.Question
import org.acra.ACRA
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate

/**
 * Created by Guille2 on 06/11/2016
 * Have fun
 */
class HttpRequestTask constructor() : AsyncTask<Void, Void, HttpResponse>() {
    var context: Context? = null
    var question: Question? = null
    var userId: String? = null

    constructor(context: Context, q: Question, userId: String) : this() {
        this.context = context
        question = q
        this.userId = userId
    }

    override fun doInBackground(vararg params: Void): HttpResponse? {
        Log.e("HttpRequest", "background")
        var url = "http://rest-service.guides.spring.io/greeting"
        try {
            //todo estas 3 lineas sobran
            val restTemplate = RestTemplate()
            restTemplate.messageConverters.add(MappingJackson2HttpMessageConverter())
            val HttpResponse = restTemplate.getForObject(url, HttpResponse::class.java)


            val config = ACRA.getConfig()
            url = "https://gmase.cloudant.com/quantilife_results"
            val sender = HttpSender(config, HttpSender.Method.POST, HttpSender.Type.JSON, url, null)
            sender.setBasicAuth("iduallsturicandiveringla", "50287216d17b77cb5ff511c6c011cf02db62036f")
            val content = HttpContent(contentId = userId + question!!.id, content = "{\"_id\":\"" + userId + question!!.id + "\",\"internalResult\":" + question!!.result + ",\"textResult\":\"" + question!!.textResult + "\",\"day\":" + AppDay.today() + "}")
            sender.send(context!!, content)

            return HttpResponse
        } catch (e: Exception) {
            Log.e("HttpRequest", e.message, e)
        }
        return null
    }

    override fun onPostExecute(httpResponse: HttpResponse) {
        Log.e("HttpRequest", httpResponse.getId())
        Log.e("HttpRequest", httpResponse.getContent())
    }
}