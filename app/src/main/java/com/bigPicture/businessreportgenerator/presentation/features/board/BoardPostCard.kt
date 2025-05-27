package com.bigPicture.businessreportgenerator.presentation.features.board

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bigPicture.businessreportgenerator.data.remote.model.BoardDTO
import com.bigPicture.businessreportgenerator.data.remote.model.CommentDTO

// 색상 팔레트 정의
object BoardColors {
    val Primary = Color(0xFF007AFF)
    val Secondary = Color(0xFF5856D6)
    val Success = Color(0xFF34C759)
    val Warning = Color(0xFFFF9500)
    val Error = Color(0xFFFF3B30)
    val Background = Color(0xFFF2F2F7)
    val Surface = Color.White
    val OnSurface = Color(0xFF1C1C1E)
    val OnSurfaceVariant = Color(0xFF8E8E93)
    val Outline = Color(0xFFE5E5EA)
    val OutlineVariant = Color(0xFFF2F2F7)
}

@Composable
fun BoardPostCard(
    post: BoardDTO,
    onClick: () -> Unit,
    onAddComment: (Long, String, String) -> Unit,
    onEditPost: (Long, String, String, String) -> Unit,
    onDeletePost: (Long, String) -> Unit,
    onEditComment: (Long, String, String, Long) -> Unit,
    onDeleteComment: (Long, String, Long) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(post.title) }
    var editContent by remember { mutableStateOf(post.contents) }
    var editPassword by remember { mutableStateOf("") }
    var deletePassword by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                isExpanded = !isExpanded
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BoardColors.Surface),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp
        )
    ) {
        Column {
            // 메인 게시글 헤더
            PostHeader(
                post = post,
                onEditClick = { showEditDialog = true },
                onDeleteClick = { showDeleteDialog = true }
            )

            // 게시글 내용
            PostContent(post = post)

            // 댓글 영역 (애니메이션과 함께 확장)
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = BoardColors.OutlineVariant,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    CommentsSection(
                        comments = post.comments,
                        commentText = commentText,
                        passwordText = passwordText,
                        onCommentTextChange = { commentText = it },
                        onPasswordTextChange = { passwordText = it },
                        onAddComment = {
                            onAddComment(post.boardIdx, commentText.trim(), passwordText.trim())
                            commentText = ""
                            passwordText = ""
                        },
                        onEditComment = { commentIdx, newComment, password ->
                            onEditComment(commentIdx, newComment, password, post.boardIdx)
                        },
                        onDeleteComment = { commentIdx, password ->
                            onDeleteComment(commentIdx, password, post.boardIdx)
                        }
                    )
                }
            }

            // 댓글 수 표시 (접힌 상태에서)
            if (!isExpanded) {
                CommentCountIndicator(commentCount = post.comments.size)
            }
        }
    }

    // 다이얼로그들
    EditPostDialog(
        show = showEditDialog,
        title = editTitle,
        content = editContent,
        password = editPassword,
        onTitleChange = { editTitle = it },
        onContentChange = { editContent = it },
        onPasswordChange = { editPassword = it },
        onConfirm = {
            onEditPost(post.boardIdx, editTitle, editContent, editPassword)
            showEditDialog = false
            editPassword = ""
        },
        onDismiss = { showEditDialog = false }
    )

    DeletePostDialog(
        show = showDeleteDialog,
        password = deletePassword,
        onPasswordChange = { deletePassword = it },
        onConfirm = {
            onDeletePost(post.boardIdx, deletePassword)
            showDeleteDialog = false
            deletePassword = ""
        },
        onDismiss = { showDeleteDialog = false }
    )
}

@Composable
private fun PostHeader(
    post: BoardDTO,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 아이콘
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(BoardColors.Primary, BoardColors.Secondary)
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Author",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = post.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = BoardColors.OnSurface
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "익명",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BoardColors.OnSurfaceVariant
                )
            )
        }

        // 액션 버튼들
        Row {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = BoardColors.OnSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = BoardColors.Error,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun PostContent(post: BoardDTO) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 0.dp)
    ) {
        Text(
            text = post.contents,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = BoardColors.OnSurface,
                lineHeight = 22.sp
            ),
            maxLines = if (post.contents.length > 100) 3 else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CommentCountIndicator(commentCount: Int) {
    if (commentCount > 0) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            color = BoardColors.OutlineVariant,
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Comments",
                    tint = BoardColors.OnSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "댓글 ${commentCount}개",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BoardColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "탭하여 보기",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BoardColors.Primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    } else {
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun CommentsSection(
    comments: List<CommentDTO>,
    commentText: String,
    passwordText: String,
    onCommentTextChange: (String) -> Unit,
    onPasswordTextChange: (String) -> Unit,
    onAddComment: () -> Unit,
    onEditComment: (Long, String, String) -> Unit,
    onDeleteComment: (Long, String) -> Unit
) {
    Column(
        modifier = Modifier.padding(20.dp)
    ) {
        // 댓글 목록
        if (comments.isEmpty()) {
            EmptyCommentsPlaceholder()
        } else {
            comments.forEach { comment ->
                CommentItem(
                    comment = comment,
                    onEditComment = onEditComment,
                    onDeleteComment = onDeleteComment
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 댓글 입력 영역
        CommentInputSection(
            commentText = commentText,
            passwordText = passwordText,
            onCommentTextChange = onCommentTextChange,
            onPasswordTextChange = onPasswordTextChange,
            onAddComment = onAddComment
        )
    }
}

@Composable
private fun EmptyCommentsPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "No comments",
                tint = BoardColors.OnSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "첫 번째 댓글을 남겨보세요!",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BoardColors.OnSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun CommentInputSection(
    commentText: String,
    passwordText: String,
    onCommentTextChange: (String) -> Unit,
    onPasswordTextChange: (String) -> Unit,
    onAddComment: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BoardColors.OutlineVariant,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 댓글 입력 필드
            OutlinedTextField(
                value = commentText,
                onValueChange = onCommentTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        "댓글을 입력하세요...",
                        color = BoardColors.OnSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BoardColors.Primary,
                    unfocusedBorderColor = BoardColors.Outline,
                    focusedContainerColor = BoardColors.Surface,
                    unfocusedContainerColor = BoardColors.Surface
                ),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 비밀번호와 등록 버튼
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = passwordText,
                    onValueChange = onPasswordTextChange,
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            "비밀번호",
                            color = BoardColors.OnSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BoardColors.Primary,
                        unfocusedBorderColor = BoardColors.Outline,
                        focusedContainerColor = BoardColors.Surface,
                        unfocusedContainerColor = BoardColors.Surface
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.width(12.dp))

                Surface(
                    onClick = onAddComment,
                    enabled = commentText.isNotBlank() && passwordText.isNotBlank(),
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = if (commentText.isNotBlank() && passwordText.isNotBlank())
                        BoardColors.Primary else BoardColors.Outline
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = if (commentText.isNotBlank() && passwordText.isNotBlank())
                                Color.White else BoardColors.OnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
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

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = BoardColors.Surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // 댓글 작성자 아이콘
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(BoardColors.OutlineVariant, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Commenter",
                    tint = BoardColors.OnSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "익명",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BoardColors.OnSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.comment,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BoardColors.OnSurface,
                        lineHeight = 20.sp
                    )
                )
            }

            // 댓글 액션 버튼들
            Row {
                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit comment",
                        tint = BoardColors.OnSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete comment",
                        tint = BoardColors.Error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // 댓글 수정/삭제 다이얼로그
    EditCommentDialog(
        show = showEditDialog,
        text = editText,
        password = editPassword,
        onTextChange = { editText = it },
        onPasswordChange = { editPassword = it },
        onConfirm = {
            if (editText.isNotBlank() && editPassword.isNotBlank()) {
                onEditComment(comment.commentIdx, editText, editPassword)
                showEditDialog = false
                editPassword = ""
            }
        },
        onDismiss = { showEditDialog = false }
    )

    DeleteCommentDialog(
        show = showDeleteDialog,
        password = deletePassword,
        onPasswordChange = { deletePassword = it },
        onConfirm = {
            if (deletePassword.isNotBlank()) {
                onDeleteComment(comment.commentIdx, deletePassword)
                showDeleteDialog = false
                deletePassword = ""
            }
        },
        onDismiss = { showDeleteDialog = false }
    )
}

// 다이얼로그 컴포넌트들
@Composable
private fun EditPostDialog(
    show: Boolean,
    title: String,
    content: String,
    password: String,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("수정", color = BoardColors.Primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소", color = BoardColors.OnSurfaceVariant)
                }
            },
            title = {
                Text(
                    "게시글 수정",
                    fontWeight = FontWeight.Bold,
                    color = BoardColors.OnSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = onTitleChange,
                        label = { Text("제목") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = onContentChange,
                        label = { Text("내용") },
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("비밀번호") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = BoardColors.Surface
        )
    }
}

@Composable
private fun DeletePostDialog(
    show: Boolean,
    password: String,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("삭제", color = BoardColors.Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소", color = BoardColors.OnSurfaceVariant)
                }
            },
            title = {
                Text(
                    "게시글 삭제",
                    fontWeight = FontWeight.Bold,
                    color = BoardColors.OnSurface
                )
            },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("비밀번호를 입력하세요") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = BoardColors.Surface
        )
    }
}

@Composable
private fun EditCommentDialog(
    show: Boolean,
    text: String,
    password: String,
    onTextChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("수정", color = BoardColors.Primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소", color = BoardColors.OnSurfaceVariant)
                }
            },
            title = {
                Text(
                    "댓글 수정",
                    fontWeight = FontWeight.Bold,
                    color = BoardColors.OnSurface
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = onTextChange,
                        label = { Text("댓글 내용") },
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = { Text("비밀번호") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = BoardColors.Surface
        )
    }
}

@Composable
private fun DeleteCommentDialog(
    show: Boolean,
    password: String,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (show) {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("삭제", color = BoardColors.Error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("취소", color = BoardColors.OnSurfaceVariant)
                }
            },
            title = {
                Text(
                    "댓글 삭제",
                    fontWeight = FontWeight.Bold,
                    color = BoardColors.OnSurface
                )
            },
            text = {
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("비밀번호를 입력하세요") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = BoardColors.Surface
        )
    }
}