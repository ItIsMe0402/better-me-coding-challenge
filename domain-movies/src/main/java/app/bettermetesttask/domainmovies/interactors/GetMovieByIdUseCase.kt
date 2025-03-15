package app.bettermetesttask.domainmovies.interactors

import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import javax.inject.Inject

class GetMovieByIdUseCase @Inject constructor(
    private val repository: MoviesRepository,
) {
    suspend operator fun invoke(movieId: Int): Result<Movie> {
        return repository.getMovie(movieId)
    }
}
