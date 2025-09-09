package com.example.githubuser.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.domain.model.User
import com.example.githubuser.ui.state.UserUiState
import com.example.githubuser.ui.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDetailScreen(
    viewModel: UserViewModel = koinViewModel(),
    username: String,
    onBack: () -> Unit = {}
) {
    val state by viewModel.userDetail.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()
    // Fetch user detail
    LaunchedEffect(username) {
        viewModel.getUserDetail(username)
        viewModel.observeFavoriteStatus(username)
    }

    // Extract user jika Success
    val user: User? = (state as? UserUiState.Success<User>)?.data

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Profile", style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    user?.let {
                        IconButton(onClick = {
                            if (isFavorite) {
                                viewModel.removeFromFavorite(it)
                            } else {
                                viewModel.addToFavorite(it)
                            }
                        }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isFavorite) Color.Red else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when (state) {
                is UserUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is UserUiState.Error -> {
                    Text(
                        text = (state as UserUiState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is UserUiState.Success -> {
                    user?.let {
                        // Avatar
                        AsyncImage(
                            model = it.avatarUrl,
                            contentDescription = "${it.username} avatar",
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Name
                        Text(
                            text = it.name ?: it.username,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Text(
                            text = "@${it.username}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Bio
                        Text(
                            text = it.bio ?: "-",
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatCard(count = it.followers ?: 0, label = "Followers")
                            StatCard(count = it.following ?: 0, label = "Following")
                            StatCard(count = it.publicRepos ?: 0, label = "Repositories")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(count: Int, label: String) {
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (count >= 1000) "${count / 1000}k" else count.toString(),
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSecondary // Themed text color
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSecondary // Themed text color
        )
    }
}
