package com.example.app.features.board

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardDetailScreen(
    post: Board,
    onBack: () -> Unit,
    onUpdate: (String, String) -> Unit,
    onDelete: () -> Unit
) {
    var editing by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf(post.title) }
    var content by remember { mutableStateOf(post.content) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("게시글 상세") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                }
            })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (editing) {
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("제목") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = content, onValueChange = { content = it },
                    label = { Text("내용") }, minLines = 5, modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                Row {
                    Button(onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            onUpdate(title, content)
                            editing = false
                        }
                    }) { Text("수정 완료") }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { editing = false }) { Text("취소") }
                }
            } else {
                Text(title, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                Text(content, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(16.dp))
                Row {
                    Button(onClick = { editing = true }) { Text("수정") }
                    Spacer(Modifier.width(8.dp))
                    OutlinedButton(onClick = { onDelete() }) { Text("삭제") }
                }
            }
        }
    }
}
