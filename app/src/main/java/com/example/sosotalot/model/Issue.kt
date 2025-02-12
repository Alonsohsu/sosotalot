package com.example.sosotalot.model

data class Issue(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    var likes: Int = 0
)
