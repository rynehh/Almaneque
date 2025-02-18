package com.example.almaneque

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RestEngine private constructor() {
    companion object {
        private val cookieStore: MutableMap<String, List<Cookie>> = mutableMapOf()

        fun getRestEngine(): Retrofit {

            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)


            val cookieJar = object : CookieJar {
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host].orEmpty()
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .cookieJar(cookieJar)
                .build()

            // Configuramos Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl("http://almaneque.atwebpages.com/Public/") // Tu URL base
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit
        }
    }
}
