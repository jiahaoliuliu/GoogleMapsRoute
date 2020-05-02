package com.jiahaoliuliu.entity

data class Direction (
    val bounds: Bounds,
    val distance: String,
    val duration: String,
    val startAddress: String,
    val startLocation: Coordinate,
    val endAddress: String,
    val endLocation: Coordinate,
    val polyline: String
)

data class Bounds (val northeast: Coordinate, val southwest: Coordinate)