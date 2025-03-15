package app.bettermetesttask.datamovies.repository

import app.bettermetesttask.datamovies.WallClock
import app.bettermetesttask.datamovies.database.entities.MovieEntity
import app.bettermetesttask.datamovies.repository.stores.MoviesLocalStore
import app.bettermetesttask.datamovies.repository.stores.MoviesMapper
import app.bettermetesttask.datamovies.repository.stores.MoviesRestStore
import app.bettermetesttask.domaincore.utils.Result
import app.bettermetesttask.domainmovies.entries.Movie
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class MoviesRepositoryTest {

    private val localStore: MoviesLocalStore = mockk()
    private val mapper: MoviesMapper = MoviesMapper()
    private val restStore: MoviesRestStore = mockk()
    private val wallClock: WallClock = mockk()

    private lateinit var sut: MoviesRepositoryImpl

    @BeforeEach
    fun setUp() {
        sut = MoviesRepositoryImpl(
            localStore,
            mapper,
            restStore,
            wallClock
        )
    }

    @Test
    fun `getMovies returns error when failed to load from the network & cached result is obsolete`() = runTest {
        coEvery { restStore.getMovies() } throws Exception()
        every { wallClock.uptimeMillis() } returns 1_000_000

        val result = sut.getMovies()

        Assertions.assertTrue(result is Result.Error)
    }

    @Test
    fun `getMovies caches the network result & returns cached`() = runTest {
        coEvery { restStore.getMovies() } returns listOf(
            Movie(
                id = 1,
                title = "Movie",
                description = "Some movie",
                posterPath = null,
                liked = false,
            ),
        )
        every { wallClock.uptimeMillis() } returns 0
        coJustRun { localStore.putMovies(any()) }
        coEvery { localStore.getMovies() } returns listOf(
            MovieEntity(
                id = 1,
                title = "Movie from DB",
                description = "Some movie from DB",
                posterPath = null,
            )
        )

        val result = sut.getMovies()

        Assertions.assertEquals(
            Result.Success(
                listOf(
                    Movie(
                        id = 1,
                        title = "Movie from DB",
                        description = "Some movie from DB",
                        posterPath = null,
                    ),
                ),
            ),
            result,
        )
    }
}
