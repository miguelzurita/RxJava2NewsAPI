package com.example.rxjava2newsapi.services

import android.content.Context
import com.example.rxjava2newsapi.R
import com.example.rxjava2newsapi.news.api.TopHeadlinesEndpoint
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient(val context: Context) {

    private val ENDPOINT_URL by lazy { "https://newsapi.org/v2/" }
    lateinit var topHeadlinesEndpoint: TopHeadlinesEndpoint
    lateinit var newsApiConfig: String

    init {
        val retrofit: Retrofit = generateRetrofitBuilder()
        topHeadlinesEndpoint = retrofit.create(TopHeadlinesEndpoint::class.java)
        newsApiConfig = context.resources.getString(R.string.api_key)
    }

    private fun generateRetrofitBuilder(): Retrofit {

        return Retrofit.Builder()
            .baseUrl(ENDPOINT_URL)
            .addConverterFactory(GsonConverterFactory.create())
            //Add RxJava2CallAdapterFactory as a Call adapter when building your Retrofit instance
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }
}