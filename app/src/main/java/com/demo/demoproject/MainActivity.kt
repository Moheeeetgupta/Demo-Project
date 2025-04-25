package com.demo.demoproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.demo.demoproject.api.RetrofitClient
import com.demo.demoproject.datastore.DataStoreManager
import com.demo.demoproject.factory.GenericViewModelFactory
import com.demo.demoproject.localdb.DemoDatabase
import com.demo.demoproject.repo.MainRepository
import com.demo.demoproject.ui.theme.DemoProjectTheme
import com.demo.demoproject.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(applicationContext)
        val viewModel: MainViewModel by viewModels {
            GenericViewModelFactory {
                val db = DemoDatabase.getDatabase(applicationContext)
                val repository = MainRepository(RetrofitClient.apiService, db.dao(), dataStoreManager)
                MainViewModel(repository)
            }
        }
        setContent {
            DemoProjectTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DemoProjectTheme {
        Greeting("Android")
    }
}