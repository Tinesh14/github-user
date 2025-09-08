package com.example.githubuser.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.User
import com.example.githubuser.ui.state.UserUiState
import com.example.githubuser.ui.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllUsersScreen(viewModel: UserViewModel = koinViewModel(), onUserClick: (String) -> Unit) {
    var query by remember { mutableStateOf("") }

    // Collect StateFlows
    val allUsersState by viewModel.allUsers.collectAsState()
    val searchResultsState by viewModel.searchResults.collectAsState()

    // Fetch all users when screen loads
    LaunchedEffect(Unit) { viewModel.getAllUsers() }

    Column (modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Search Input
        OutlinedTextField(
            value = query,
            onValueChange = {
                query = it
                if (it.isNotEmpty()) viewModel.searchUsers(it)
            },
            label = { Text("Search Users") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Decide which state to show
        val displayState = if (query.isEmpty()) allUsersState else searchResultsState

        when (displayState) {
            is UserUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is UserUiState.Success -> {
                val users = displayState.data
                LazyColumn {
                    items(users, key = { it.id }) { user ->
                        UserItem(user) { onUserClick(user.username) }
                        HorizontalDivider()
                    }
                }
            }
            is UserUiState.Error -> {
                Text(
                    text = displayState.message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}


@Composable
fun UserItem(user: User, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatarUrl,
            contentDescription = "${user.username} avatar",
            modifier = Modifier.size(48.dp).clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(user.username, fontWeight = FontWeight.Bold)
            user.name?.let { Text(it, fontSize = 12.sp) }
        }
    }
}
