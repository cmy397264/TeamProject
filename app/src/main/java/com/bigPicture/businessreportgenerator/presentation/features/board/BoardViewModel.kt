import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigPicture.businessreportgenerator.data.remote.repository.BoardRepository
import com.example.app.features.board.BoardUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BoardViewModel(
    private val boardRepository: BoardRepository
) : ViewModel() {
    companion object {
        private const val TAG = "BigPicture"
    }

    private val _uiState = MutableStateFlow(BoardUiState())
    val uiState: StateFlow<BoardUiState> = _uiState.asStateFlow()

    fun fetchBoards() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val boards = boardRepository.getBoards()
                _uiState.update { it.copy(posts = boards, isLoading = false) }
                Log.d(TAG, "게시글 목록 불러오기 성공: ${boards.size}건")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e(TAG, "게시글 목록 불러오기 실패: ${e.message}", e)
            }
        }
    }

    fun fetchBoardDetail(boardIdx: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val post = boardRepository.getBoardDetail(boardIdx)
                val comments = boardRepository.getComments(boardIdx)
                _uiState.update { it.copy(selectedPost = post, comments = comments, isLoading = false) }
                Log.d(TAG, "게시글/댓글 상세 불러오기 성공: 게시글ID=$boardIdx")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e(TAG, "게시글/댓글 상세 불러오기 실패: 게시글ID=$boardIdx, ${e.message}", e)
            }
        }
    }

    fun createBoard(title: String, content: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                boardRepository.createBoard(title, content, password)
                fetchBoards()
                _uiState.update { it.copy(isLoading = false) }
                Log.d(TAG, "게시글 등록 성공")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e(TAG, "게시글 등록 실패: ${e.message}", e)
            }
        }
    }

    fun updateBoard(boardIdx: Long, title: String?, contents: String?, password: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                boardRepository.updateBoard(boardIdx, title, contents, password)
                fetchBoardDetail(boardIdx)
                _uiState.update { it.copy(isLoading = false) }
                Log.d(TAG, "게시글 수정 성공: 게시글ID=$boardIdx")
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e(TAG, "게시글 수정 실패: 게시글ID=$boardIdx, ${e.message}", e)
            }
        }
    }

    fun deleteBoard(boardIdx: Long, password: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                boardRepository.deleteBoard(boardIdx, password)
                fetchBoards()
                _uiState.update { it.copy(selectedPost = null, isLoading = false) }
                Log.d(TAG, "게시글 삭제 성공: 게시글ID=$boardIdx")
                onSuccess?.invoke()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                Log.e(TAG, "게시글 삭제 실패: 게시글ID=$boardIdx, ${e.message}", e)
            }
        }
    }

    fun fetchComments(boardIdx: Long) {
        viewModelScope.launch {
            try {
                val comments = boardRepository.getComments(boardIdx)
                _uiState.update { it.copy(comments = comments) }
                Log.d(TAG, "댓글 목록 불러오기 성공: 게시글ID=$boardIdx, ${comments.size}건")
            } catch (e: Exception) {
                Log.e(TAG, "댓글 목록 불러오기 실패: 게시글ID=$boardIdx, ${e.message}", e)
            }
        }
    }

    fun addComment(boardIdx: Long, comment: String, password: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                boardRepository.createComment(boardIdx, comment, password)
                // 댓글 등록 후 전체 게시글 목록 새로 불러오기!
                fetchBoards()
                Log.d(TAG, "댓글 등록 성공: 게시글ID=$boardIdx")
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "댓글 등록 실패: 게시글ID=$boardIdx, ${e.message}", e)
            }
        }
    }



    fun updateComment(commentIdx: Long, comment: String, password: String, boardIdx: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                boardRepository.updateComment(commentIdx, comment, password)
                fetchComments(boardIdx)
                Log.d(TAG, "댓글 수정 성공: 댓글ID=$commentIdx")
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "댓글 수정 실패: 댓글ID=$commentIdx, ${e.message}", e)
            }
        }
    }

    fun deleteComment(commentIdx: Long, password: String, boardIdx: Long, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            try {
                boardRepository.deleteComment(commentIdx, password)
                fetchComments(boardIdx)
                Log.d(TAG, "댓글 삭제 성공: 댓글ID=$commentIdx")
                onSuccess?.invoke()
            } catch (e: Exception) {
                Log.e(TAG, "댓글 삭제 실패: 댓글ID=$commentIdx, ${e.message}", e)
            }
        }
    }
}
