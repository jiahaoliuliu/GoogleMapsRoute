package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.networklayer.direction.GoogleDirectionAPIService
import io.reactivex.Single
import timber.log.Timber

class DirectionRepository(private val googleDirectionAPIService: GoogleDirectionAPIService) {

    companion object {
        private const val COORDINATOR_SEPARATOR = ","
    }

    fun calculateDirection(origin: Coordinate, destination: Coordinate): Single<String> {
        return googleDirectionAPIService.getDirection(
            origin = origin.toStringWithSeparator(COORDINATOR_SEPARATOR),
            destinations = destination.toStringWithSeparator(COORDINATOR_SEPARATOR))
            .map {
                Timber.v("Direction from network $it")
                it.toString()
            }
//            .map {mapNetworkDirectionResponseToInternalDirectionResponse(it)}
    }

//    private fun mapNetworkDirectionResponseToInternalDirectionResponse(
//        directionNetworkResponse: DirectionNetworkResponse): Direction {
//        return Direction()
//    }

}