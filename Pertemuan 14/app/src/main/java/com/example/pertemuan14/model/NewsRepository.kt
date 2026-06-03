package com.example.pertemuan14.model

import com.example.pertemuan14.api.RetrofitInstance
import com.example.pertemuan14.data.Article

class NewsRepository {
    private val apiKey = "d900e6bd60954e4aa84ffdc5314bfc30"

    suspend fun getTopHeadlines(): List<Article> {
        return try {
            val response = RetrofitInstance.newsApiService.getTopHeadlines(
                country = "us",
                apiKey = apiKey
            )
            response.articles
        } catch (e: Exception) {
            throw Exception("Gagal memuat berita: ${e.message}")
        }
    }

    suspend fun searchNews(query: String): List<Article> {
        return try {
            val response = RetrofitInstance.newsApiService.searchNews(
                query = query,
                apiKey = apiKey
            )
            response.articles
        } catch (e: Exception) {
            throw Exception("Gagal mencari berita: ${e.message}")
        }
    }
}
