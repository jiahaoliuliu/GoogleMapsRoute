package com.jiahaoliuliu.entity

data class Coordinate(val latitude: Double, val longitude: Double) {
    override fun toString(): String {
        return "$latitude+$longitude}"
    }
}