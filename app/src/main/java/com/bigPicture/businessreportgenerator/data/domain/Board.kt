package com.example.app.features.board

import com.bigPicture.businessreportgenerator.data.remote.model.BoardDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentDTO

data class Board(
    val id: Long,
    val title: String,
    val content: String,
    val comments: List<String> = emptyList()
)

data class BoardUiState(
    val posts: List<BoardDTO> = emptyList(),
    val selectedPost: BoardDTO? = null,
    val comments: List<CommentDTO> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null
)


