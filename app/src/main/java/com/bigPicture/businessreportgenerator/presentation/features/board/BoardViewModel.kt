package com.example.app.features.board

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class BoardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        BoardUiState(
            posts = listOf(
                Board(1, "테스트 글 제목", "테스트 글 내용입니다."),
                Board(2, "두번째 글", "여기에 내용이 들어갑니다.")
            )
        )
    )
    val uiState: StateFlow<BoardUiState> = _uiState

    fun addPost(title: String, content: String) {
        val newId = (_uiState.value.posts.maxOfOrNull { it.id } ?: 0L) + 1
        val newPost = Board(newId, title, content)
        _uiState.value = _uiState.value.copy(
            posts = listOf(newPost) + _uiState.value.posts
        )
    }
}
