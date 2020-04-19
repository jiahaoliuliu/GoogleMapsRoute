package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.networklayer.geocoding.GoogleGeocodingAPIService
import io.reactivex.Single

class GeocodingRepository(private val googleGeocodingAPIService: GoogleGeocodingAPIService) {

    fun retrieveLocation(address: String): Single<String> {
        return googleGeocodingAPIService.getLocation(address = address)
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