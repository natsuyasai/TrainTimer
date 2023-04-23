package com.nyasai.traintimer.http


import android.util.Log
import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost

class HttpClient : IHttpClient {

    override fun httpGet(url: String, parameters: Parameters?): Response {
        val (request, response, result) = url.httpGet(parameters).responseString()
        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                Log.e("exception", result.getException().message.toString())
            }

            else -> {
            }
        }
        return response
    }

    override fun httpPost(url: String, parameters: Parameters?): Response {
        val (request, response, result) = url.httpPost().responseString()
        when (result) {
            is com.github.kittinunf.result.Result.Failure -> {
                Log.e("exception", result.getException().message.toString())
            }

            else -> {
            }
        }
        return response
    }

    override fun httpGetAsync(url: String, parameters: Parameters?): Response {
        TODO("Not yet implemented")
    }

    override fun httpPostAsync(url: String, parameters: Parameters?): Response {
        TODO("Not yet implemented")
    }
}