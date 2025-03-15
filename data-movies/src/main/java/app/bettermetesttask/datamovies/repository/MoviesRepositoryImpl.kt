package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import app.bettermetesttask.domainmovies.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val localStore: MoviesLocalStore,
    private val mapper: MoviesMapper,
    private val restStore: MoviesRestStore,
) : MoviesRepository {

    override suspend fun getMovies(): Result<List<Movie>> {
        // TODO: Replace with a call to the local DB
        return Result.of {
            restStore.getMovies()
        }
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
}
