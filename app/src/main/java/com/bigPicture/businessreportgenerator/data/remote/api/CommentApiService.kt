
import com.bigPicture.businessreportgenerator.data.remote.ApiResponse
import com.bigPicture.businessreportgenerator.data.remote.model.CommentCreateDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface CommentApiService {
    @GET("comments")
    suspend fun getCommentsByBoard(@Query("boardIdx") boardIdx: Long): ApiResponse<List<CommentDTO>>

    @POST("comments")
    suspend fun createComment(@Body comment: CommentCreateDTO): ApiResponse<CommentDTO>

    @PUT("/api/v1/comments/{commentIdx}")
    suspend fun updateComment(
        @Path("commentIdx") commentIdx: Long,
        @Body request: Map<String, String>
    ): ApiResponse<Any>


    @HTTP(method = "DELETE", path = "/api/v1/comments/{commentIdx}", hasBody = true)
    suspend fun deleteComment(
        @Path("commentIdx") commentIdx: Long,
        @Body request: Map<String, String>
    ): ApiResponse<Any>

}