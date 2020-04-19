package com.jiahaoliuliu.datalayer

import com.jiahaoliuliu.entity.Place
import com.jiahaoliuliu.networklayer.places.GooglePlacesAPIService
import io.reactivex.Observable
import io.reactivex.Single

class PlacesRepository(private val googlePlacesAPIService: GooglePlacesAPIService) {

    fun retrieveLocation(address: String): Single<String> {
        return googlePlacesAPIService.getLocation(address = address)
    }

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
}