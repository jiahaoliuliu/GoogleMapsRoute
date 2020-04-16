package com.jiahaoliuliu.entity

data class Coordinate(val latitude: Double, val longitude: Double) {
    fun toStringWithSeparator(separator: String): String {
        return "$latitude$separator$longitude"
    }
}