package app.bettermetesttask.movies.sections.compose

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.featurecommon.injection.utils.Injectable
import app.bettermetesttask.featurecommon.injection.viewmodel.SimpleViewModelProviderFactory
import app.bettermetesttask.movies.sections.MovieDetailsState
import app.bettermetesttask.movies.sections.MovieDetailsViewModel
import app.bettermetesttask.movies.sections.MoviesState
import app.bettermetesttask.movies.sections.MoviesViewModel
import coil3.compose.AsyncImage
import javax.inject.Inject
import javax.inject.Provider

class MoviesComposeFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelProvider: Provider<MoviesViewModel>

    @Inject
    lateinit var movieDetailsViewModelFactory: MovieDetailsViewModel.Factory

    private val viewModel by viewModels<MoviesViewModel> {
        SimpleViewModelProviderFactory(
            viewModelProvider
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            setContent {
                val navController = rememberNavController()
                NavHost(navController, MovieList)  {
                    composable<MovieList> {
                        val viewState by viewModel.moviesStateFlow.collectAsStateWithLifecycle()
                        MoviesComposeScreen(viewState, likeMovie = { movie ->
                            viewModel.likeMovie(movie)
                        }, pickMovie = { movie ->
                            navController.navigate(MovieDetails(movie.id))
                        }, onRefresh = {
                            viewModel.loadMovies()
                        })
                    }
                    composable<MovieDetails> { navBackStackEntry ->
                        val movieId = navBackStackEntry.toRoute<MovieDetails>().movieId
                        val viewModel by viewModels<MovieDetailsViewModel>(
                            ownerProducer = { navBackStackEntry }
                        ) {
                            object : ViewModelProvider.Factory {
                                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                                    return movieDetailsViewModelFactory.build(movieId) as T
                                }
                            }
                        }
                        val viewState by viewModel.movieDetailsState.collectAsStateWithLifecycle()
                        MovieDetailsScreen(viewState)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoviesComposeScreen(
    moviesState: MoviesState,
    likeMovie: (Movie) -> Unit,
    pickMovie: (Movie) -> Unit,
    onRefresh: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .pullToRefresh(
                isRefreshing = false,
                onRefresh = onRefresh,
                state = rememberPullToRefreshState()
            )
    ) {
        when (moviesState) {
            MoviesState.Error -> {
                Text("Error")
            }

            is MoviesState.Loaded -> {
                LazyColumn {
                    items(moviesState.movies) { item ->
                        MovieItem(item, onLikeClicked = {
                            likeMovie(item)
                        }, onPicked = {
                            pickMovie(item)
                        })
                    }
                }
            }

            MoviesState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun MovieItem(
    movie: Movie,
    onLikeClicked: (Int) -> Unit,
    onPicked: () -> Unit,
) {
    Card(
        modifier = Modifier
            .clickable(onClick = onPicked)
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterPath,
                contentDescription = "Movie Poster",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = movie.title, fontSize = 18.sp, color = Color.Black)
                Text(text = movie.description, fontSize = 14.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = { onLikeClicked(movie.id) }) {
                Icon(
                    imageVector = if (movie.liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like Button",
                    tint = if (movie.liked) Color.Red else Color.Gray
                )
            }
        }
    }
}

@Composable
private fun MovieDetailsScreen(viewState: MovieDetailsState) {
    Surface {
        when (viewState) {
            MovieDetailsState.Loading -> {
                CircularProgressIndicator()
            }

            is MovieDetailsState.Loaded -> {
                val movie = viewState.movie
                AsyncImage(
                    model = movie.posterPath,
                    contentDescription = null,
                )
            }

            is MovieDetailsState.Error -> {
                Text("Error")
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PreviewMoviesComposeScreen() {
    MoviesComposeScreen(
        MoviesState.Loaded(
            List(20) { index ->
                Movie(
                    index,
                    "Title $index",
                    "Overview $index",
                    null,
                    liked = index % 2 == 0,
                )
            }
        ),
        likeMovie = {},
        pickMovie = {},
        onRefresh = {},
    )
}

@Preview
@Composable
private fun PreviewMovieDetailsScreen() {
    MovieDetailsScreen(
        MovieDetailsState.Loaded(
            Movie(
                id = 1,
                title = "Movie",
                description = "",
                posterPath = null,
                liked = true,
            ),
        )
    )
}
