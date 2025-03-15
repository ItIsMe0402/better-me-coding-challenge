package app.bettermetesttask.movies.sections

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.interactors.GetMovieByIdUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn

class MovieDetailsViewModel @AssistedInject constructor(
    private val getMovieUseCase: GetMovieByIdUseCase,
    @Assisted
    movieId: Int,
) : ViewModel() {

    val movieDetailsState: StateFlow<MovieDetailsState> = flow<MovieDetailsState> {
        val state = when (val result = getMovieUseCase(movieId)) {
            is Result.Success -> {
                MovieDetailsState.Loaded(result.data)
            }

            is Result.Error -> {
                MovieDetailsState.Error
            }
        }
        emit(state)
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        MovieDetailsState.Loading,
    )

    @AssistedFactory
    interface Factory {
        fun build(movieId: Int): MovieDetailsViewModel
    }
}
