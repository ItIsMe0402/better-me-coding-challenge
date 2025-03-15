package app.bettermetesttask.movies.sections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.interactors.AddMovieToFavoritesUseCase
import app.bettermetesttask.domainmovies.interactors.ObserveMoviesUseCase
import app.bettermetesttask.domainmovies.interactors.RemoveMovieFromFavoritesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MoviesViewModel @Inject constructor(
    private val observeMoviesUseCase: ObserveMoviesUseCase,
    private val likeMovieUseCase: AddMovieToFavoritesUseCase,
    private val dislikeMovieUseCase: RemoveMovieFromFavoritesUseCase,
) : ViewModel() {

    private val moviesMutableFlow: MutableStateFlow<MoviesState> = MutableStateFlow(MoviesState.Loading)

    val moviesStateFlow: StateFlow<MoviesState>
        get() = moviesMutableFlow.asStateFlow()

    private var observeMoviesJob: Job? = null

    init {
        loadMovies()
    }

    fun loadMovies() {
        observeMoviesJob?.cancel()
        moviesMutableFlow.value = MoviesState.Loading
        observeMoviesJob = viewModelScope.launch {
            observeMoviesUseCase()
                .collect { result ->
                    val state = when (result) {
                        is Result.Success -> {
                            MoviesState.Loaded(result.data)
                        }

                        is Result.Error -> MoviesState.Error
                    }
                    moviesMutableFlow.value = state
                }
        }
    }

    fun likeMovie(movie: Movie) {
        viewModelScope.launch {
            if (!movie.liked) {
                likeMovieUseCase(movie.id)
            } else {
                dislikeMovieUseCase(movie.id)
            }
        }
    }

    fun openMovieDetails(movie: Movie) {
        // TODO: todo todo todo todo
    }
}
