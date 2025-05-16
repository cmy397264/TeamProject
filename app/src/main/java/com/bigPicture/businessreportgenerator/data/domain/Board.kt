package com.example.app.features.board

data class Board(
    val id: Long,
    val title: String,
    val content: String
)

data class BoardUiState(
    val posts: List<Board> = emptyList()
)
