package com.bigPicture.businessreportgenerator.data.remote.model

data class CommentDTO(
    val commentIdx: Long,
    val comment: String
)

data class CommentCreateDTO(
    val boardIdx: Long,
    val comment: String,
    val commentPassword: String
)

data class CommentUpdateDTO(
    val comment: String,
    val commentPassword: String
)