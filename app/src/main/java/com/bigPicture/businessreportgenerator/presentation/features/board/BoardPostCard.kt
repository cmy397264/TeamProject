package com.bigPicture.businessreportgenerator.presentation.features.board

import BoardViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigPicture.businessreportgenerator.data.remote.model.BoardDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentDTO

@Composable
fun BoardPostCard(
    post: BoardDTO,
    onClick: () -> Unit,
    onAddComment: (Long, String, String) -> Unit,
    onEditPost: ((Long, String, String, String) -> Unit)? = null,
    onDeletePost: ((Long, String) -> Unit)? = null,

    ) {
    var commentText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(post.title) }
    var editContent by remember { mutableStateOf(post.contents) }
    var editPassword by remember { mutableStateOf("") }
    var deletePassword by remember { mutableStateOf("") }
    val viewModel: BoardViewModel = viewModel();


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    post.title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1C1C1E),
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    onClick = { showEditDialog = true },
                    content = { Text("수정", color = Color.Gray, fontSize = 14.sp) }
                )
                TextButton(
                    onClick = { showDeleteDialog = true },
                    content = { Text("삭제", color = Color.Red, fontSize = 14.sp) }
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                post.contents,
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF444444)
            )
            Spacer(Modifier.height(12.dp))
            Divider(thickness = 1.dp, color = Color(0xFFF2F2F7))
            // 댓글 UI
            Column(modifier = Modifier.padding(top = 8.dp)) {
                TextButton(onClick = { /* showEditDialog = true */ }) {
                    Text("수정", color = Color.Gray, fontSize = 14.sp)
                }
                TextButton(onClick = { /* showDeleteDialog = true */ }) {
                    Text("삭제", color = Color.Red, fontSize = 14.sp)
                }
                if (post.comments.isEmpty()) {
                    Text(
                        "아직 댓글이 없습니다.",
                        color = Color(0xFFB0B3B8),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                } else {
                    post.comments.forEach { comment ->
                        CommentItem(
                            comment = comment,
                            onEditComment = { commentIdx, newComment, password ->
                                // ViewModel 함수 호출 예시
                                viewModel.updateComment(commentIdx, newComment, password, post.boardIdx)
                            },
                            onDeleteComment = { commentIdx, password ->
                                viewModel.deleteComment(commentIdx, password, post.boardIdx)
                            }
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                }
                // 댓글 입력창
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        modifier = Modifier
                            .weight(2f) // comment에 더 많은 공간 배분
                            .padding(end = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("댓글을 입력하세요") },
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = true, // 한 줄로, 높이 자동
                        maxLines = 1
                    )
                    OutlinedTextField(
                        value = passwordText,
                        onValueChange = { passwordText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        placeholder = { Text("비밀번호") },
                        textStyle = MaterialTheme.typography.bodySmall,
                        singleLine = true
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
                    ) {
                        Text("등록", color = Color(0xFF007AFF), fontSize = 14.sp)
                    }
                }
            }
        }
        if (showEditDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("글 수정") },
                text = {
                    Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("제목") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editContent,
                            onValueChange = { editContent = it },
                            label = { Text("내용") }
                        )
                        OutlinedTextField(
                            value = editPassword,
                            onValueChange = { editPassword = it },
                            label = { Text("비밀번호") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEditPost?.invoke(post.boardIdx, editTitle, editContent, editPassword)
                            showEditDialog = false
                            editPassword = ""
                        }
                    ) { Text("수정") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) { Text("취소") }
                }
            )
        }
        if (showDeleteDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("글 삭제") },
                text = {
                    OutlinedTextField(
                        value = deletePassword,
                        onValueChange = { deletePassword = it },
                        label = { Text("비밀번호") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeletePost?.invoke(post.boardIdx, deletePassword)
                            showDeleteDialog = false
                            deletePassword = ""
                        }
                    ) { Text("삭제", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
                }
            )
        }
        if (showEditDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showEditDialog = false },
                title = { Text("글 수정") },
                text = {
                    Column(verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("제목") },
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = editContent,
                            onValueChange = { editContent = it },
                            label = { Text("내용") }
                        )
                        OutlinedTextField(
                            value = editPassword,
                            onValueChange = { editPassword = it },
                            label = { Text("비밀번호") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onEditPost?.invoke(post.boardIdx, editTitle, editContent, editPassword)
                            showEditDialog = false
                            editPassword = ""
                        }
                    ) { Text("수정") }
                },
                dismissButton = {
                    TextButton(onClick = { showEditDialog = false }) { Text("취소") }
                }
            )
        }
        if (showDeleteDialog) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("글 삭제") },
                text = {
                    OutlinedTextField(
                        value = deletePassword,
                        onValueChange = { deletePassword = it },
                        label = { Text("비밀번호") },
                        singleLine = true
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeletePost?.invoke(post.boardIdx, deletePassword)
                            showDeleteDialog = false
                            deletePassword = ""
                        }
                    ) { Text("삭제", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
                }
            )
        }

    }
}

@Composable
fun CommentItem(
    comment: CommentDTO,
    onEditComment: (Long, String, String) -> Unit, // commentIdx, newComment, password
    onDeleteComment: (Long, String) -> Unit        // commentIdx, password
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "유저",
            modifier = Modifier.size(18.dp),
            tint = Color(0xFFB0B3B8)
        )
        Spacer(Modifier.width(6.dp))
        Text(comment.comment, fontSize = 14.sp, color = Color(0xFF333333)) // comment.comment
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editText by remember { mutableStateOf(comment.comment) }
    var editPassword by remember { mutableStateOf("") }
    var deletePassword by remember { mutableStateOf("") }

    Row {
        Text(comment.comment)
        TextButton(onClick = { showEditDialog = true }) { Text("수정") }
        TextButton(onClick = { showDeleteDialog = true }) { Text("삭제") }
    }
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("댓글 수정") },
            text = {
                Column {
                    OutlinedTextField(value = editText, onValueChange = { editText = it }, label = { Text("댓글 내용") })
                    OutlinedTextField(value = editPassword, onValueChange = { editPassword = it }, label = { Text("비밀번호") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onEditComment(comment.commentIdx, editText, editPassword)
                    showEditDialog = false
                }) { Text("수정") }
            },
            dismissButton = { TextButton(onClick = { showEditDialog = false }) { Text("취소") } }
        )
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("댓글 삭제") },
            text = {
                OutlinedTextField(value = deletePassword, onValueChange = { deletePassword = it }, label = { Text("비밀번호") }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteComment(comment.commentIdx, deletePassword)
                    showDeleteDialog = false
                }) { Text("삭제") }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("취소") } }
        )
    }
}
