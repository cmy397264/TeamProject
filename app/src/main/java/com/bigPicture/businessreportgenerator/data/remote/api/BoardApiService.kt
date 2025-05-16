package com.bigPicture.businessreportgenerator.data.remote.api

import com.bigPicture.businessreportgenerator.data.remote.ApiResponse
import com.bigPicture.businessreportgenerator.data.remote.model.BoardCreateDTO
import com.bigPicture.businessreportgenerator.data.remote.model.BoardDTO
import com.bigPicture.businessreportgenerator.data.remote.model.BoardUpdateDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface BoardApiService {
    @GET("board")
    suspend fun getBoards(): ApiResponse<List<BoardDTO>>

    @GET("board/{boardIdx}")
    suspend fun getBoardDetail(@Path("boardIdx") boardIdx: Long): ApiResponse<BoardDTO>

    @POST("board")
    suspend fun createBoard(@Body board: BoardCreateDTO): ApiResponse<BoardDTO>

    @PUT("board/{boardIdx}")
    suspend fun updateBoard(
        @Path("boardIdx") boardIdx: Long,
        @Body request: BoardUpdateDTO
    ): ApiResponse<BoardDTO>

    @HTTP(method = "DELETE", path = "board/{boardIdx}", hasBody = true)
    suspend fun deleteBoard(
        @Path("boardIdx") boardIdx: Long,
        @Body password: String
    ): ApiResponse<Unit>
}
