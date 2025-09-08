package com.example.githubuser.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun UserDetailScreen(
    viewModel: UserViewModel = koinViewModel(),
    username: String
) {
    // Collect userDetail state
    val state by viewModel.userDetail.collectAsState()

    // Fetch user detail on first composition or when username changes
    LaunchedEffect(username) { viewModel.getUserDetail(username) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        when (state) {
            is UserUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
            is UserUiState.Success -> {
                val user = (state as UserUiState.Success<User>).data
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = "${user.username} avatar",
                    modifier = Modifier
                        .size(128.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.username,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                user.name?.let {
                    Text(
                        text = it,
                        fontSize = 20.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                user.bio?.let {
                    Text(
                        text = it,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
            is UserUiState.Error -> {
                Text(
                    text = (state as UserUiState.Error).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
