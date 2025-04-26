package com.demo.demoproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.demo.demoproject.api.RetrofitClient
import com.demo.demoproject.factory.GenericViewModelFactory
import com.demo.demoproject.localdb.DemoDatabase
import com.demo.demoproject.repo.MainRepository
import com.demo.demoproject.ui.theme.DemoProjectTheme
import com.demo.demoproject.viewmodel.SplashViewmodel

class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: SplashViewmodel by viewModels {
            GenericViewModelFactory {
                val db = DemoDatabase.getDatabase(applicationContext)
                val repository =
                    MainRepository(RetrofitClient.apiService, db.dao())
                SplashViewmodel(repository)
            }
        }

        setContent {
            DemoProjectTheme {
                val isLoading = viewModel.isLoading.collectAsStateWithLifecycle().value
                val context = LocalContext.current
                LaunchedEffect(isLoading) {
                    if (isLoading.not()) {
                        val intent = Intent(context, HomeActivity::class.java)
                        context.startActivity(intent)
                        finish()
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "App Logo",
                        modifier = Modifier.align(Alignment.Center)
                    )
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.BottomCenter),
                            color = Color.Blue,
                        )
                    }
                }
            }
        }

    }
}