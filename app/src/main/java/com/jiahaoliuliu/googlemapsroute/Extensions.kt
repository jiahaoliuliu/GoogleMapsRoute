package com.jiahaoliuliu.googlemapsroute

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.jiahaoliuliu.entity.Coordinate

fun LatLng.toCoordinate() = Coordinate(latitude, longitude)

fun Coordinate.toLatLng() = LatLng(latitude, longitude)

fun Location.toCoordinate() = Coordinate(latitude, longitude)