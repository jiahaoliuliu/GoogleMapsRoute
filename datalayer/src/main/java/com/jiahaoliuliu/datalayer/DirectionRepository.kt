package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.entity.Bounds
import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.entity.Direction
import com.jiahaoliuliu.networklayer.direction.DirectionNetworkResponse
import com.jiahaoliuliu.networklayer.direction.GoogleDirectionAPIService
import io.reactivex.Single

class DirectionRepository(private val googleDirectionAPIService: GoogleDirectionAPIService) {

    companion object {
        private const val COORDINATOR_SEPARATOR = ","
    }

    fun calculateDirection(origin: Coordinate, destination: Coordinate): Single<Direction> {
        return googleDirectionAPIService.getDirection(
            origin = origin.toStringWithSeparator(COORDINATOR_SEPARATOR),
            destinations = destination.toStringWithSeparator(COORDINATOR_SEPARATOR))
            .map {mapNetworkDirectionResponseToInternalDirectionResponse(it)}
    }

    private fun mapNetworkDirectionResponseToInternalDirectionResponse(
        directionNetworkResponse: DirectionNetworkResponse
    ): Direction {
        val route = directionNetworkResponse.routes[0]
        val leg = route.legs[0]
        return Direction(
            mapNetworkBoundsToInternalBounds(route.bounds), leg.distance.text, leg.duration.text,
            leg.startAddress, mapNetworkCoordinateToInternalCoordinate(leg.startLocation),
            leg.endAddress, mapNetworkCoordinateToInternalCoordinate(leg.endLocation),
            route.polyline.points)
    }

    private fun mapNetworkBoundsToInternalBounds(bounds: com.jiahaoliuliu.networklayer.direction.Bounds): Bounds {
        return Bounds(
            mapNetworkCoordinateToInternalCoordinate(bounds.northeast),
            mapNetworkCoordinateToInternalCoordinate(bounds.southwest))
    }

    private fun mapNetworkCoordinateToInternalCoordinate(
        coordinate: com.jiahaoliuliu.networklayer.direction.Coordinate): Coordinate {
        return Coordinate(coordinate.latitude, coordinate.longitude)
    }

}