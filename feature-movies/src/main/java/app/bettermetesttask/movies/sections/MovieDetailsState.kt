package app.bettermetesttask.movies.sections

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MovieDetailsState {
    data object Loading : MovieDetailsState()

    data class Loaded(
        val movie: Movie,
    ) : MovieDetailsState()

    data object Error : MovieDetailsState()
}
