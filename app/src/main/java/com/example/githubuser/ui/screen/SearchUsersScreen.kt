//package com.example.githubuser.ui.screen
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import coil.compose.AsyncImage
//import com.example.domain.model.User
//import com.example.githubuser.ui.state.UserUiState
//import com.example.githubuser.ui.viewmodel.UserViewModel
//
//@Composable
//fun SearchUsersScreen(
//    viewModel: UserViewModel,
//    onUserClick: (String) -> Unit
//) {
//    var query by remember { mutableStateOf("") }
//    val state by viewModel.searchResults.collectAsState()
//
//    Column {
//        TextField(
//            value = query,
//            onValueChange = { query = it; viewModel.searchUsers(it) },
//            placeholder = { Text("Search users") },
//            modifier = Modifier.fillMaxWidth().padding(8.dp)
//        )
//
//        when (state) {
//            is UserUiState.Loading -> CircularProgressIndicator(Modifier.fillMaxSize())
//            is UserUiState.Success -> {
//                LazyColumn {
//                    items((state as UserUiState.Success<List<User>>).data, key = { it.id }) { user ->
//                        Row(
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .clickable { onUserClick(user.username) }
//                                .padding(8.dp)
//                        ) {
//                            AsyncImage(model = user.avatarUrl, contentDescription = null, modifier = Modifier.size(48.dp))
//                            Spacer(Modifier.width(8.dp))
//                            Text(user.username)
//                        }
//                    }
//                }
//            }
//            is UserUiState.Error -> Text((state as UserUiState.Error).message, color = Color.Red)
//        }
//    }
//}
