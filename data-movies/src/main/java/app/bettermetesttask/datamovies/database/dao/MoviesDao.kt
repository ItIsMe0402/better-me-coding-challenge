package app.bettermetesttask.datamovies.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import app.bettermetesttask.datamovies.database.entities.LikedMovieEntity
import app.bettermetesttask.datamovies.database.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {

    @Query("SELECT * FROM MoviesTable")
    suspend fun selectMovies(): List<MovieEntity>

    @Query("SELECT * FROM MoviesTable WHERE id = :id")
    suspend fun selectMovieById(id: Int): MovieEntity

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMovie(movie: MovieEntity)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("SELECT * FROM LikedMovieEntry")
    fun selectLikedEntries(): Flow<List<LikedMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedEntry(entry: LikedMovieEntity)

    @Query("DELETE FROM LikedMovieEntry WHERE movie_id = :movieId")
    suspend fun removeLikedEntry(movieId: Int)

    @Transaction
    suspend fun setMovies(movies: List<MovieEntity>) {
        deleteMovies()
        insertMovies(movies)
    }

    @Insert
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Query("DELETE FROM MoviesTable")
    suspend fun deleteMovies()
}
