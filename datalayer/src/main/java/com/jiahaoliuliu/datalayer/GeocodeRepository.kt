package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.networklayer.geocode.GoogleGeocodeAPIService
import io.reactivex.Single

class GeocodeRepository(private val googleGeocodeAPIService: GoogleGeocodeAPIService) {

    companion object {
        private const val COORDINATOR_SEPARATOR = ","
    }

    fun retrieveAddress(coordinate: Coordinate): Single<String> {
        return googleGeocodeAPIService.getAddressBasedOnLocation(
            location = coordinate.toStringWithSeparator(COORDINATOR_SEPARATOR))
    }
}