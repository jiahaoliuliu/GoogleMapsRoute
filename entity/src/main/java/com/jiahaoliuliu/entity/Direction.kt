package com.jiahaoliuliu.entity

data class Direction (
    val bounds: Bounds,
    val distance: String,
    val duration: String,
    val startAddress: String,
    val startLocation: Coordinate,
    val endAddress: String,
    val endLocation: Coordinate,
    val polyline: String,
    val stepsList: List<Step>
)

data class Bounds (val northeast: Coordinate, val southwest: Coordinate)

data class Step (val instruction: String, val distance: String, val duration: String)