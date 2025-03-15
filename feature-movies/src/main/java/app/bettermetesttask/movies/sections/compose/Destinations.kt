package app.bettermetesttask.movies.sections.compose

import kotlinx.serialization.Serializable

@Serializable
data object MovieList

@Serializable
data class MovieDetails(val movieId: Int)
