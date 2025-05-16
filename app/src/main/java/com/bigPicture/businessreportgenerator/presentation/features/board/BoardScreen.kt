package com.example.app.features.board

import BoardViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
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
                containerColor = Color(0xFFF8F8F8),
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "글쓰기", tint = Color(0xFF007AFF))
            }
        },
        containerColor = Color(0xFFF5F5F7),
        modifier = modifier
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .fillMaxSize()
            ) {
                Text(
                    "게시판",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF222222)
                    ),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                if (uiState.posts.isEmpty()) {
                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(top = 80.dp),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        Text("아직 게시글이 없습니다.", color = Color(0xFFB0B3B8), fontSize = 16.sp)
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        items(uiState.posts) { post ->
                            BoardPostCard(
                                post = post,
                                onClick = { onClickItem(post) },
                                onAddComment = onAddComment,
                                onEditPost = { id, title, content, pw -> viewModel.updateBoard(id, title, content, pw) },
                                onDeletePost = { id, pw -> viewModel.deleteBoard(id, pw)}
                            )
                        }
                    }
                }
            }

            // 1. 로딩 인디케이터 (맨 위에 오버레이)
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // 2. 에러 메시지 (맨 위에 오버레이)
            uiState.error?.let { errorMsg ->
                Box(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = errorMsg,
                        color = Color.Red,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(12.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
                    )
                }
            }

            // 3. 성공 메시지 (맨 위에 오버레이)
            uiState.successMessage?.let { msg ->
                Box(
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = msg,
                        color = Color(0xFF007AFF),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(12.dp)
                            .background(Color.White, RoundedCornerShape(8.dp))
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
                        Text("취소", color = Color(0xFF888888))
                    }
                },
                title = {
                    Text("새 게시글 작성", fontWeight = FontWeight.SemiBold)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("제목") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                        OutlinedTextField(
                            value = content,
                            onValueChange = { content = it },
                            label = { Text("내용") },
                            shape = RoundedCornerShape(14.dp),
                            minLines = 4
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("비밀번호") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp)
                        )
                    }
                },
                shape = RoundedCornerShape(18.dp)
            )
        }
    }
}