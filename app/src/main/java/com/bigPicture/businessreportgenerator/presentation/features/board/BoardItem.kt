package com.example.app.features.board

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BoardItem(post: Board, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = CardDefaults.elevatedShape,
        elevation = CardDefaults.elevatedCardElevation(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(post.content, maxLines = 2)
        }
    }
}
