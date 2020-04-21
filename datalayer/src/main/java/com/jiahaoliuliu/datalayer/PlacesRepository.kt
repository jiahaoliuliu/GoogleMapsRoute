package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.entity.Coordinate
import com.jiahaoliuliu.entity.Place
import com.jiahaoliuliu.entity.PlaceDetails
import com.jiahaoliuliu.networklayer.places.GooglePlacesAPIService
import io.reactivex.Observable
import io.reactivex.Single

class PlacesRepository(private val googlePlacesAPIService: GooglePlacesAPIService) {

    fun retrievePredictions(input: String): Single<List<Place>> {
        return googlePlacesAPIService.getPredictions(input = input)
            .flatMap { predictionsNetworkResponse ->
                Observable.fromIterable(predictionsNetworkResponse.predictions)
                    .map{ prediction ->
                        Place(prediction.placeId, prediction.structuredFormatting.mainText,
                        prediction.structuredFormatting.secondaryText) }
                    .toList()
            }
    }

    fun retrievePlaceDetails(placeId: String): Single<PlaceDetails> {
        return googlePlacesAPIService.getPlaceDetails(placeId = placeId)
            .map{placeDetailsNetworkResponse ->
                PlaceDetails(placeDetailsNetworkResponse.result.placeId, placeDetailsNetworkResponse.result.name,
                placeDetailsNetworkResponse.result.formattedAddress,
                    Coordinate(placeDetailsNetworkResponse.result.geometry.location.latitude,
                        placeDetailsNetworkResponse.result.geometry.location.longitude))
            }
    }
}