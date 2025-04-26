package com.demo.demoproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.demo.demoproject.api.RetrofitClient
import com.demo.demoproject.datastore.DataStoreManager
import com.demo.demoproject.factory.GenericViewModelFactory
import com.demo.demoproject.localdb.DemoDatabase
import com.demo.demoproject.model.Movie
import com.demo.demoproject.repo.MainRepository
import com.demo.demoproject.ui.theme.DemoProjectTheme
import com.demo.demoproject.viewmodel.MainViewModel
import kotlinx.coroutines.launch

class HomeActivity : ComponentActivity() {

    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataStoreManager = DataStoreManager(applicationContext)
        val viewModel: MainViewModel by viewModels {
            GenericViewModelFactory {
                val db = DemoDatabase.getDatabase(applicationContext)
                val repository =
                    MainRepository(RetrofitClient.apiService, db.dao())
                MainViewModel(repository, dataStoreManager)
            }
        }
        setContent {
            DemoProjectTheme {
                val searchQuery = remember { mutableStateOf("") }
                val moviesList = viewModel.moviesListState.collectAsStateWithLifecycle()
                val coroutineScope = rememberCoroutineScope()
                val isFocused = remember { mutableStateOf(false) }
                val isSearchServed = remember { mutableStateOf(false) }
                val filterType = remember { mutableStateOf<FilterType?>(null) }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        val recentSearches =
                            viewModel.recentSearchesState.collectAsStateWithLifecycle()
                        Column {
                            OutlinedTextField(
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .onFocusChanged {
                                        isFocused.value = it.isFocused
                                        coroutineScope.launch {
                                            if (isFocused.value) {
                                                viewModel.getRecentSearches()
                                            }
                                        }
                                    },
                                value = searchQuery.value,
                                onValueChange = {
                                    searchQuery.value = it
                                },
                                label = { Text(stringResource(R.string.type_any_movie_name)) },
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Search,
                                        contentDescription = "",
                                        tint = Color.Unspecified,
                                        modifier = Modifier.clickable {
                                            viewModel.searchMoviesByTitle(searchQuery.value)
                                            isSearchServed.value = true
                                        })
                                }
                            )
                            if (isFocused.value) {
                                recentSearches.value.forEach {
                                    Text(
                                        text = it,
                                        fontWeight = FontWeight.Normal,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 14.sp,
                                        modifier = Modifier
                                            .padding(start = 24.dp)
                                            .clickable {
                                                searchQuery.value = it
                                            })
                                }
                            }
                            if (isFocused.value.not()) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(Color.LightGray)
                                        .padding(6.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row(Modifier.clickable {
                                        filterType.value = FilterType.Language
                                    }) {
                                        Text(stringResource(R.string.language))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "",
                                            tint = Color.Unspecified
                                        )
                                    }
                                    Row(Modifier.clickable {
                                        viewModel.getFavouriteMovies()
                                    }) {
                                        Text(stringResource(R.string.favorite))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "",
                                            tint = Color.Unspecified
                                        )
                                    }
                                    Row(Modifier.clickable {
                                        filterType.value = FilterType.Vote
                                    }) {
                                        Text(stringResource(R.string.vote_average))
                                        Icon(
                                            imageVector = Icons.Default.ArrowDropDown,
                                            contentDescription = "",
                                            tint = Color.Unspecified
                                        )
                                    }
                                }
                                if (filterType.value != null) {
                                    FiltersBottomSheet(
                                        filterType = filterType.value!!,
                                        onDismissRequest = { filterType.value = null }) {
                                        if (filterType.value == FilterType.Language) {
                                            viewModel.filterMoviesByLanguages(it)
                                        } else {
                                            viewModel.filterMoviesByVoteCount(it.map { it.toInt() })
                                        }
                                        filterType.value = null
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    BackHandler(isSearchServed.value) {
                        if (isSearchServed.value) {
                            isSearchServed.value = false
                            searchQuery.value = ""
                            viewModel.getMovies()
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                    ) {
                        if (moviesList.value.isEmpty()) {
                            CircularProgressIndicator(Modifier.align(Alignment.Center))
                        } else {
                            val lazyListState = rememberLazyListState()

                            Row(
                                Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                LazyColumn(
                                    state = lazyListState,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(12.dp)
                                ) {
                                    items(
                                        moviesList.value,
                                        key = { movie -> movie.movieId },
                                        contentType = { }) { movie ->
                                        MoviesItemComposable(movie) {
                                            viewModel.updateMovie(movie.copy(isFavourite = movie.isFavourite.not()))
                                        }
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .weight(0.1f)
                                        .align(Alignment.CenterVertically)
                                        .padding(horizontal = 2.dp)
                                        .border(1.dp, Color.Gray),
                                    verticalArrangement = Arrangement.SpaceEvenly,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    ('A'..'Z').forEach { letter ->
                                        Text(
                                            text = letter.toString(),
                                            modifier = Modifier
                                                .padding(vertical = 1.dp)
                                                .pointerInput(Unit) {
                                                    detectTapGestures(
                                                        onTap = {
                                                            coroutineScope.launch {
                                                                val index =
                                                                    moviesList.value.indexOfFirst { movie ->
                                                                        movie.originalTitle?.startsWith(
                                                                            letter,
                                                                            ignoreCase = true
                                                                        ) == true
                                                                    }
                                                                if (index != -1) {
                                                                    lazyListState.scrollToItem(
                                                                        index
                                                                    )
                                                                }
                                                            }
                                                        }
                                                    )
                                                }
                                        )
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}

enum class FilterType {
    Language,
    Vote
}

@Composable
fun MoviesItemComposable(movie: Movie, onFavouriteClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
        Image(
            painter = rememberAsyncImagePainter(movie.posterPath),
            modifier = Modifier
                .height(100.dp)
                .width(60.dp)
                .padding(4.dp),
            contentDescription = ""
        )
        Column(Modifier.padding(start = 4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                movie.originalTitle?.let {
                    Text(
                        text = it,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(
                    imageVector =
                        if (movie.isFavourite) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                    contentDescription = "Favourite Icon",
                    tint = if (movie.isFavourite) Color.Red else Color.Gray,
                    modifier = Modifier.clickable {
                        onFavouriteClick()
                    }
                )
            }
            Spacer(Modifier.height(4.dp))
            movie.overview?.let { Text(text = it, maxLines = 3, fontSize = 13.sp) }
        }
    }
}