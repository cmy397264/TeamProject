package com.bigPicture.businessreportgenerator.presentation.features.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bigPicture.businessreportgenerator.data.remote.model.BoardDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentDTO

@Composable
fun BoardPostCard(
    post: BoardDTO,
    onClick: () -> Unit,
    onAddComment: (Long, String, String) -> Unit,
    onEditPost: (Long, String, String, String) -> Unit,
    onDeletePost: (Long, String) -> Unit,
    onEditComment: (Long, String, String, Long) -> Unit,
    onDeleteComment: (Long, String, Long) -> Unit
){
    var commentText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(post.title) }
    var editContent by remember { mutableStateOf(post.contents) }
    var editPassword by remember { mutableStateOf("") }
    var deletePassword by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 4.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 22.dp, vertical = 16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        post.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 21.sp,
                            color = Color(0xFF222222)
                        ),
                        maxLines = 1
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        post.contents,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFF4B4B4B),
                            fontSize = 15.sp
                        ),
                        maxLines = 5
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    TextButton(onClick = { showEditDialog = true }, content = {
                        Text("수정", color = Color(0xFF636366), fontSize = 14.sp)
                    })
                    TextButton(onClick = { showDeleteDialog = true }, content = {
                        Text("삭제", color = Color(0xFFE04A4A), fontSize = 14.sp)
                    })
                }
            }
            Spacer(Modifier.height(14.dp))
            Divider(thickness = 1.dp, color = Color(0xFFF2F2F7), modifier = Modifier.padding(vertical = 2.dp))

            // 댓글 영역
            Column(Modifier.fillMaxWidth()) {
                if (post.comments.isEmpty()) {
                    Text(
                        "댓글이 아직 없습니다.",
                        color = Color(0xFFB0B3B8),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                } else {
                    post.comments.forEach { comment ->
                        CommentItem(
                            comment = comment,
                            onEditComment = { commentIdx, newComment, password ->
                                onEditComment(commentIdx, newComment, password, post.boardIdx)
                            },
                            onDeleteComment = { commentIdx, password ->
                                onDeleteComment(commentIdx, password, post.boardIdx)
                            }
                        )
                    }
                }
                // 댓글 입력
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier.weight(2.2f).padding(end = 6.dp),
                        placeholder = { Text("댓글을 입력하세요", fontSize = 14.sp, color = Color(0xFFB0B3B8)) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD0D0D0),
                            unfocusedBorderColor = Color(0xFFE5E5EA)
                        )
                    )
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        modifier = Modifier.weight(1f).padding(end = 4.dp),
                        placeholder = { Text("비밀번호", fontSize = 14.sp, color = Color(0xFFB0B3B8)) },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD0D0D0),
                            unfocusedBorderColor = Color(0xFFE5E5EA)
                        )
                    )
                    TextButton(
                        onClick = {
                            if (commentText.isNotBlank() && passwordText.isNotBlank()) {
                                onAddComment(post.boardIdx, commentText.trim(), passwordText.trim())
                                commentText = ""
                                passwordText = ""
                            }
                        },
                        enabled = commentText.isNotBlank() && passwordText.isNotBlank(),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text("등록", color = Color(0xFF007AFF), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
        // 수정/삭제 다이얼로그 (기존대로)
        if (showEditDialog) {
            AlertDialog(
                onDismissRequest = { showEditDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEditPost(post.boardIdx, editTitle, editContent, editPassword)
                            showEditDialog = false
                            editPassword = ""
                        }
                    ) { Text("수정") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) { Text("취소") }
                },
                title = { Text("글 수정") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("제목") }, singleLine = true)
                        OutlinedTextField(value = editContent, onValueChange = { editContent = it }, label = { Text("내용") })
                        OutlinedTextField(value = editPassword, onValueChange = { editPassword = it }, label = { Text("비밀번호") }, singleLine = true)
                    }
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeletePost(post.boardIdx, deletePassword)
                            showDeleteDialog = false
                            deletePassword = ""
                        }
                    ) { Text("삭제", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
                },
                title = { Text("글 삭제") },
                text = {
                    OutlinedTextField(value = deletePassword, onValueChange = { deletePassword = it }, label = { Text("비밀번호") }, singleLine = true)
                },
                shape = RoundedCornerShape(16.dp),
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentDTO,
    onEditComment: (Long, String, String) -> Unit,
    onDeleteComment: (Long, String) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(comment.comment) }
    var editPassword by remember { mutableStateOf("") }
    var deletePassword by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        // 댓글 아이콘 (프로필)
        Box(
            Modifier
                .size(22.dp)
                .background(Color(0xFFE5E5EA), shape = RoundedCornerShape(11.dp))
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "유저",
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(16.dp),
                tint = Color(0xFFB0B3B8)
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            comment.comment,
            fontSize = 15.sp,
            color = Color(0xFF222222),
            modifier = Modifier.weight(1f)
        )
        // 오른쪽 끝 수정/삭제 버튼 (Apple 스타일: flat & gray)
        TextButton(
            onClick = { showEditDialog = true },
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
        ) {
            Text("수정", color = Color(0xFF636366), fontSize = 13.sp)
        }
        TextButton(
            onClick = { showDeleteDialog = true },
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
        ) {
            Text("삭제", color = Color(0xFFE04A4A), fontSize = 13.sp)
        }
    }

    // -------- 수정 다이얼로그 --------
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("댓글 수정", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editText,
                        onValueChange = { editText = it },
                        label = { Text("댓글 내용") },
                        shape = RoundedCornerShape(14.dp)
                    )
                    OutlinedTextField(
                        value = editPassword,
                        onValueChange = { editPassword = it },
                        label = { Text("비밀번호") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editText.isNotBlank() && editPassword.isNotBlank()) {
                            onEditComment(comment.commentIdx, editText, editPassword)
                            showEditDialog = false
                        }
                    }
                ) { Text("수정", color = Color(0xFF007AFF), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("취소", color = Color.Gray) }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    // -------- 삭제 다이얼로그 --------
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("댓글 삭제", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = deletePassword,
                    onValueChange = { deletePassword = it },
                    label = { Text("비밀번호") },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (deletePassword.isNotBlank()) {
                            onDeleteComment(comment.commentIdx, deletePassword)
                            showDeleteDialog = false
                        }
                    }
                ) { Text("삭제", color = Color.Red, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소", color = Color.Gray) }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}
