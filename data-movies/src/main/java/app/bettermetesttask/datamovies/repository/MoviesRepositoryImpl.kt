package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.WallClock
import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeoutException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoviesRepositoryImpl @Inject constructor(
    private val localStore: MoviesLocalStore,
    private val mapper: MoviesMapper,
    private val restStore: MoviesRestStore,
    private val wallClock: WallClock
) : MoviesRepository {

    private var lastTimeUpdated = 0L

    override suspend fun getMovies(): Result<List<Movie>> {
        try {
            val movies = restStore.getMovies()
            localStore.putMovies(movies.map(mapper.mapToLocal))
            lastTimeUpdated = wallClock.uptimeMillis()
        } catch (e: Exception) {
            if (wallClock.uptimeMillis() - lastTimeUpdated > MOVIES_UPDATE_THRESHOLD_MILLIS) {
                return Result.of { throw TimeoutException() }
            }
        }
        return Result.of { localStore.getMovies().map(mapper.mapFromLocal) }
    }

    override suspend fun getMovie(id: Int): Result<Movie> {
        return Result.of { mapper.mapFromLocal(localStore.getMovie(id)) }
    }

    override fun observeLikedMovieIds(): Flow<List<Int>> {
        return localStore.observeLikedMoviesIds()
    }

    override suspend fun addMovieToFavorites(movieId: Int) {
        localStore.likeMovie(movieId)
    }

    override suspend fun removeMovieFromFavorites(movieId: Int) {
        localStore.dislikeMovie(movieId)
    }

    companion object {
        const val MOVIES_UPDATE_THRESHOLD_MILLIS = 10_000
    }
}
