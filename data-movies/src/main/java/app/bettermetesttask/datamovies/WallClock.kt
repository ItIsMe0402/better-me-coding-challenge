package app.bettermetesttask.datamovies

import android.os.SystemClock
import javax.inject.Inject

class WallClock @Inject constructor() {
    fun uptimeMillis() = SystemClock.uptimeMillis()
}
