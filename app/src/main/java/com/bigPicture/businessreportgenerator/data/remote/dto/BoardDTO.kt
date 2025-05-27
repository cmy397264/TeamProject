package com.bigPicture.businessreportgenerator.data.remote.model

data class BoardDTO(
    val boardIdx: Long,
    val title: String,
    val contents: String,
    val viewCount: Int,
    val comments: List<CommentDTO> = emptyList()
)

data class BoardCreateDTO(
    val title: String,
    val contents: String,
    val boardPassword: String
)

data class BoardUpdateDTO(
    val title: String?,
    val contents: String?,
    val boardPassword: String
)

