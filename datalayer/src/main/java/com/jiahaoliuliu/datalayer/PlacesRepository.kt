package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.networklayer.places.GooglePlacesAPIService
import io.reactivex.Single

class PlacesRepository(private val googlePlacesAPIService: GooglePlacesAPIService) {

    fun retrieveLocation(address: String): Single<String> {
        return googlePlacesAPIService.getLocation(address = address)
    }

    fun retrievePredictions(input: String): Single<String> {
        return googlePlacesAPIService.getPredictions(input = input)
    }

//    private fun mapNetworkDirectionResponseToInternalDirectionResponse(
//        directionNetworkResponse: DirectionNetworkResponse
//    ): Direction {
//        val route = directionNetworkResponse.routes[0]
//        val leg = route.legs[0]
//        return Direction(
//            mapNetworkBoundsToInternalBounds(route.bounds), leg.distance.text, leg.duration.text,
//            leg.startAddress, mapNetworkCoordinateToInternalCoordinate(leg.startLocation),
//            leg.endAddress, mapNetworkCoordinateToInternalCoordinate(leg.endLocation),
//            route.polyline.points)
//    }
//
//    private fun mapNetworkBoundsToInternalBounds(bounds: com.jiahaoliuliu.networklayer.direction.Bounds): Bounds {
//        return Bounds(
//            mapNetworkCoordinateToInternalCoordinate(bounds.northeast),
//            mapNetworkCoordinateToInternalCoordinate(bounds.southwest))
//    }
//
//    private fun mapNetworkCoordinateToInternalCoordinate(
//        coordinate: com.jiahaoliuliu.networklayer.direction.Coordinate): Coordinate {
//        return Coordinate(coordinate.latitude, coordinate.longitude)
//    }

}