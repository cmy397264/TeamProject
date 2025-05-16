package com.bigPicture.businessreportgenerator.data.remote.repository

import CommentApiService
import com.bigPicture.businessreportgenerator.data.remote.api.BoardApiService
import com.bigPicture.businessreportgenerator.data.remote.model.BoardCreateDTO
import com.bigPicture.businessreportgenerator.data.remote.model.BoardDTO
import com.bigPicture.businessreportgenerator.data.remote.model.BoardUpdateDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentCreateDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentUpdateDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BoardRepository(
    private val boardApi: BoardApiService,
    private val commentApi: CommentApiService
) {

    // 게시글 목록
    suspend fun getBoards(): List<BoardDTO> = withContext(Dispatchers.IO) {
        val res = boardApi.getBoards()
        return@withContext res.data ?: emptyList()
    }

    // 게시글 상세
    suspend fun getBoardDetail(boardIdx: Long): BoardDTO? = withContext(Dispatchers.IO) {
        boardApi.getBoardDetail(boardIdx).data
    }

    // 게시글 등록
    suspend fun createBoard(title: String, contents: String, password: String): BoardDTO? = withContext(Dispatchers.IO) {
        val dto = BoardCreateDTO(title, contents, password)
        boardApi.createBoard(dto).data
    }

    // 게시글 수정
    suspend fun updateBoard(boardIdx: Long, title: String?, contents: String?, password: String): BoardDTO? = withContext(Dispatchers.IO) {
        val dto = BoardUpdateDTO(title, contents, password)
        boardApi.updateBoard(boardIdx, dto).data
    }

    // 게시글 삭제
    suspend fun deleteBoard(boardIdx: Long, password: String): Boolean = withContext(Dispatchers.IO) {
        val res = boardApi.deleteBoard(boardIdx, "\"$password\"")
        res.status == "OK"
    }

    // 댓글 목록
    suspend fun getComments(boardIdx: Long): List<CommentDTO> = withContext(Dispatchers.IO) {
        commentApi.getCommentsByBoard(boardIdx).data ?: emptyList()
    }

    // 댓글 등록
    suspend fun createComment(boardIdx: Long, comment: String, password: String): CommentDTO? = withContext(Dispatchers.IO) {
        val dto = CommentCreateDTO(boardIdx, comment, password)
        commentApi.createComment(dto).data
    }

    // 댓글 수정
    suspend fun updateComment(commentIdx: Long, comment: String, password: String): CommentDTO? = withContext(Dispatchers.IO) {
        val dto = CommentUpdateDTO(comment, password)
        commentApi.updateComment(commentIdx, dto).data
    }

    // 댓글 삭제
    suspend fun deleteComment(commentIdx: Long, password: String): Boolean = withContext(Dispatchers.IO) {
        val res = commentApi.deleteComment(commentIdx, password)
        res.status == "OK"
    }
}
