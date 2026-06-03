package com.example.pertemuan14.api

import com.example.pertemuan14.data.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "us",
        @Query("apiKey") apiKey: String
    ): NewsResponse

    @GET("everything")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("sortBy") sortBy: String = "relevancy",
        @Query("pageSize") pageSize: Int = 50,
        @Query("apiKey") apiKey: String
    ): NewsResponse
}
