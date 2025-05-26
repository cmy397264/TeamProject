package com.example.app.features.board

import BoardViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bigPicture.businessreportgenerator.data.remote.model.BoardDTO
import com.bigPicture.businessreportgenerator.presentation.features.board.BoardPostCard

@Composable
fun BoardScreen(
    uiState: BoardUiState,
    onClickItem: (BoardDTO) -> Unit,
    onAddPost: (String, String, String) -> Unit,
    onAddComment: (Long, String, String) -> Unit,
    onEditPost: (Long, String, String, String) -> Unit,
    onDeletePost: (Long, String) -> Unit,
    viewModel: BoardViewModel,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.fetchBoards()
    }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFE5E5EA),
                contentColor = Color(0xFF007AFF),
                shape = RoundedCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "글쓰기", modifier = Modifier.size(28.dp))
            }
        },
        containerColor = Color(0xFFF2F2F7),
        modifier = modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF2F2F7))
        ) {
            Text(
                "Board",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF18191C),
                    fontSize = 32.sp,
                    letterSpacing = 0.5.sp
                ),
                modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .weight(1f)
            ) {
                items(uiState.posts) { post ->
                    BoardPostCard(
                        post = post,
                        onClick = { onClickItem(post) },
                        onAddComment = onAddComment,
                        onEditPost = onEditPost,
                        onDeletePost = onDeletePost,
                        onEditComment = { commentIdx, newComment, password, boardIdx ->
                            viewModel.updateComment(commentIdx, newComment, password, boardIdx)
                        },
                        onDeleteComment = { commentIdx, password, boardIdx ->
                            viewModel.deleteComment(commentIdx, password, boardIdx)
                        }
                    )
                }
            }
        }

        // 글쓰기 다이얼로그
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank() && password.isNotBlank()) {
                                onAddPost(title.trim(), content.trim(), password.trim())
                                title = ""
                                content = ""
                                password = ""
                                showDialog = false
                            }
                        }
                    ) {
                        Text("등록", fontWeight = FontWeight.Bold, color = Color(0xFF007AFF))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("취소", color = Color(0xFF8E8E93))
                    }
                },
                title = {
                    Text("새 게시글 작성", fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("제목", color = Color(0xFF8E8E93)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("내용", color = Color(0xFF8E8E93)) },
                            shape = RoundedCornerShape(16.dp),
                            minLines = 5
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("비밀번호", color = Color(0xFF8E8E93)) },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(22.dp),
                containerColor = Color.White
            )
        }
    }
}