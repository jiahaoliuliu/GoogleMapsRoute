package com.jiahaoliuliu.networklayer

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
data class Distance(private val status: String)