package com.example.pertemuan14.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertemuan14.data.Article
import com.example.pertemuan14.model.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val articles: List<Article>) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
}

class NewsViewModel : ViewModel() {
    private val repository = NewsRepository()

    private val _uiState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val searchResults: StateFlow<NewsUiState> = _searchResults.asStateFlow()

    init {
        loadTopHeadlines()
    }

    fun loadTopHeadlines() {
        viewModelScope.launch {
            _uiState.value = NewsUiState.Loading
            try {
                val articles = repository.getTopHeadlines()
                _uiState.value = NewsUiState.Success(articles)
            } catch (e: Exception) {
                _uiState.value = NewsUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun searchNews(query: String) {
        viewModelScope.launch {
            _searchResults.value = NewsUiState.Loading
            try {
                if (query.isEmpty()) {
                    val articles = repository.getTopHeadlines()
                    _searchResults.value = NewsUiState.Success(articles)
                } else {
                    val articles = repository.searchNews(query)
                    _searchResults.value = NewsUiState.Success(articles)
                }
            } catch (e: Exception) {
                _searchResults.value = NewsUiState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    fun retryLoadNews() {
        loadTopHeadlines()
    }
}
