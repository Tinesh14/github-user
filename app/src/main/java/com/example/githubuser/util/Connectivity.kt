package com.example.githubuser.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

@Composable
fun networkStatusFlow(context: Context = LocalContext.current): State<Boolean> {
    val isConnected = remember { mutableStateOf(false) }

    val connectivityManager = remember {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    LaunchedEffect(connectivityManager) {
        val flow = callbackFlow<Boolean> {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    isConnected.value = true
                }
                override fun onLost(network: Network) {
                    isConnected.value = false
                }
            }

            val request = NetworkRequest.Builder().build()
            connectivityManager.registerNetworkCallback(request, callback)

            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        }
        flow.collect { status ->
            isConnected.value = status
        }

    }
    return isConnected
}

