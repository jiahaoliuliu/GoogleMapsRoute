package com.jiahaoliuliu.networklayer.distance

import com.google.gson.annotations.SerializedName

//{
//    "destination_addresses":  [
//    "ATC TOWER, Dubai Airport - Dubai International Airport - Dubai - United Arab Emirates",
//    "Abu Hiraybah - Abu Dhabi - United Arab Emirates",
//    "Unnamed Road - أبو ظبي - United Arab Emirates"
//    ],
//    "origin_addresses":  [
//    "107 Sheikh Zayed Rd - Dubai - United Arab Emirates"
//    ],
//    "rows":  [
//    {
//        "elements":  [
//        {
//            "distance":  {
//            "text":  "17.3 km",
//            "value":  17340
//        },
//            "duration":  {
//            "text":  "17 mins",
//            "value":  1029
//        },
//            "status":  "OK"
//        },
//        {
//            "distance":  {
//            "text":  "216 km",
//            "value":  216097
//        },
//            "duration":  {
//            "text":  "2 hours 33 mins",
//            "value":  9187
//        },
//            "status":  "OK"
//        },
//        {
//            "distance":  {
//            "text":  "143 km",
//            "value":  143443
//        },
//            "duration":  {
//            "text":  "1 hour 26 mins",
//            "value":  5133
//        },
//            "status":  "OK"
//        }
//        ]
//    }
//    ],
//    "status":  "OK"
//}
data class DistanceNetworkResponse(
    @SerializedName("destination_addresses")
    val destinationAddresses: List<String>,

    @SerializedName("origin_addresses")
    val originAddresses: List<String>,

    @SerializedName("rows")
    val distances: List<Distance>,

    val status: String)

data class Distance(@SerializedName("elements") val distanceElementsList: List<DistanceElement>)

data class DistanceElement(
    val distance: DistanceValue,
    val duration: DistanceValue,
    val status: String
)

data class DistanceValue(val text: String, val value: Int)