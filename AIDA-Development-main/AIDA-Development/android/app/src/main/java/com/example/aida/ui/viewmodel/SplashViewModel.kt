package com.example.aida.ui.viewmodel

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aida.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 * Simple ViewModel used to display the AIDA logo during launch
 */
class SplashViewModel: ViewModel() {
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            delay(2000)
            _isLoading.value = false
        }
    }
}

/**
 * Create the splash screen composable
 */
@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel = viewModel(),
    navigateToMain: () -> Unit
)
    {
    val isLoading by splashViewModel.isLoading.observeAsState(true)
    LaunchedEffect(isLoading) {
        if (!isLoading) { //When finished -> Go to main screen
            navigateToMain()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter =  painterResource(id = R.drawable.aida_logo_new),
            contentDescription = "Splash Image",
            modifier = Modifier.size(200.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MaterialTheme {
        SplashScreen(navigateToMain = {}) //Go to main screen after delay
    }
}