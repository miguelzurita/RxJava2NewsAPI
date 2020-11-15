package com.example.rxjava2newsapi.news.model

data class TopHeadlines(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>
)