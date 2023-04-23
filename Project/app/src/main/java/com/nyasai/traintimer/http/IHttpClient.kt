package com.nyasai.traintimer.http

import com.github.kittinunf.fuel.core.Parameters
import com.github.kittinunf.fuel.core.Response

interface IHttpClient {

    fun httpGet(url: String, parameters: Parameters? = null): Response

    fun httpPost(url: String, parameters: Parameters? = null): Response

    fun httpGetAsync(url: String, parameters: Parameters? = null): Response

    fun httpPostAsync(url: String, parameters: Parameters? = null): Response
}