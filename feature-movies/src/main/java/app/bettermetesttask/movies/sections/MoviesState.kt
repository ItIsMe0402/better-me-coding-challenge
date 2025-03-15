package app.bettermetesttask.movies.sections

import app.bettermetesttask.domainmovies.entries.Movie

sealed class MoviesState {
    object Loading : MoviesState()

    data class Loaded(val movies: List<Movie>) : MoviesState()

    data object Error : MoviesState()
}
