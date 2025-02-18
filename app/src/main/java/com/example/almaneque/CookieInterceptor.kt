package com.example.almaneque

import okhttp3.Interceptor
import okhttp3.Response

class CookieInterceptor : Interceptor {
    private var cookies: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()


        cookies?.let {
            builder.addHeader("Cookie", it)
        }

        val response = chain.proceed(builder.build())


        response.headers("Set-Cookie").forEach {
            if (it.startsWith("PHPSESSID")) {
                cookies = it
            }
        }

        return response
    }
}
