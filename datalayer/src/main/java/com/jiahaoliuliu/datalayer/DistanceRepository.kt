package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.entity.Distance
import com.jiahaoliuliu.networklayer.GoogleDistanceAPIService
import io.reactivex.Single
import timber.log.Timber

class DistanceRepository(private val googleDistanceAPIService: GoogleDistanceAPIService) {

    fun calculateDistance(origin: Coordinate, destination: Coordinate): Single<Distance> {
        // Dummy data
        val distance = Distance("Sheikh Zayer 100", Coordinate(25.276, 55.296),
                                "Dubai International Airport - Dubai - United Arab Emirates", Coordinate(25.2527777777778, 55.3644444444444),
            "17.3km", "17 minutes"
        )

        return googleDistanceAPIService.getDistance(
                origin = origin.toString(),
                destinations = destination.toString())
            .map { // Do the mapping here
                Timber.v("Distance + $it")
                    distance }
    }
}