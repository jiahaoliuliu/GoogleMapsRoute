package com.jiahaoliuliu.entity

data class Distance(val originName: String, val originCoordinate: Coordinate,
                    val destinationName: String, val destinationCoordinate: Coordinate,
                    val distance: String, val duration: String)