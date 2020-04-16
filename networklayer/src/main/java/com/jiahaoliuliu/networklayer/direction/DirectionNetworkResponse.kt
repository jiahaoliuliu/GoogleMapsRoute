package com.jiahaoliuliu.networklayer.direction

import com.google.gson.annotations.SerializedName

//{
//    "geocoded_waypoints": [
//    {
//        "geocoder_status": "OK",
//        "place_id": "ChIJa0BZuI5CXz4RqmNfLs2XluU",
//        "types": [
//        "establishment",
//        "food",
//        "grocery_or_supermarket",
//        "point_of_interest",
//        "store",
//        "supermarket"
//        ]
//    },
//    {
//        "geocoder_status": "OK",
//        "place_id": "ChIJPW8kEmZdXz4RZovTO3wFLcY",
//        "types": [
//        "establishment",
//        "point_of_interest"
//        ]
//    }
//    ],
//    "routes": [
//    {
//        "bounds": {
//        "northeast": {
//        "lat": 25.2744139,
//        "lng": 55.3735327
//    },
//        "southwest": {
//        "lat": 25.2097167,
//        "lng": 55.2746782
//    }
//    },
//        "copyrights": "Map data ©2020",
//        "legs": [
//        {
//            "distance": {
//            "text": "17.3 km",
//            "value": 17332
//        },
//            "duration": {
//            "text": "17 mins",
//            "value": 1027
//        },
//            "end_address": "ATC TOWER, Dubai Airport - Dubai International Airport - Dubai - United Arab Emirates",
//            "end_location": {
//            "lat": 25.2591389,
//            "lng": 55.3685585
//        },
//            "start_address": "Sheikh Zayed Road - Next To Financial Centre Metro Station - دبي - United Arab Emirates",
//            "start_location": {
//            "lat": 25.2113512,
//            "lng": 55.2760184
//        },
//            "steps": [
//            {
//                "distance": {
//                "text": "0.2 km",
//                "value": 212
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 40
//            },
//                "end_location": {
//                "lat": 25.2097167,
//                "lng": 55.27493810000001
//            },
//                "html_instructions": "Head <b>southwest</b>",
//                "polyline": {
//                "points": "}a{xCcbkpIZR^R`BbAlCzAXN"
//            },
//                "start_location": {
//                "lat": 25.2113512,
//                "lng": 55.2760184
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "0.1 km",
//                "value": 121
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 14
//            },
//                "end_location": {
//                "lat": 25.2106392,
//                "lng": 55.2749038
//            },
//                "html_instructions": "Turn <b>right</b> onto the <b>E11 N</b> ramp",
//                "maneuver": "ramp-right",
//                "polyline": {
//                "points": "wwzxCk{jpIITMNEDEBA@A?G@A?C?A?CAAACAOIA?SOMGIEOAG?_@B"
//            },
//                "start_location": {
//                "lat": 25.2097167,
//                "lng": 55.27493810000001
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "12.6 km",
//                "value": 12575
//            },
//                "duration": {
//                "text": "8 mins",
//                "value": 503
//            },
//                "end_location": {
//                "lat": 25.2700132,
//                "lng": 55.34380280000001
//            },
//                "html_instructions": "Merge onto <b>E11</b><div style=\"font-size:0.9em\">Toll road</div>",
//                "maneuver": "merge",
//                "polyline": {
//                "points": "o}zxCc{jpIeEmC_GoD{D_CoEiCoBkAgEiCsBkAmG_E]S_BaAQK_CsAyD_CkCeBm@_@sBmA{FkDyHoEy@c@UMGEIEo@]gBeA{A_AcAk@[Se@]MIg@_@_@]WW[[m@u@[c@_@o@OUu@aBM_@ACMc@Mc@KYAIKe@ACGa@Ie@Ie@Ig@Ie@Ie@Ie@Ge@Ie@Ia@?CG_@Ie@Ie@Ig@Ie@Ie@Ie@Ge@AEG_@Ie@Ig@Ie@Ge@Ie@[sAQiAe@}CWcB}AcKc@sCAG_@}B_A_GAKIe@UwAUyAIq@I_AIeACg@Ac@EqA?cAAwB?oA?oB?aAAwA?qA@oA?i@@a@@s@@Y@cABk@Fm@Dc@BOFo@Ny@Lo@Rw@H]\\aARk@N_@z@oB|AwDTc@\\m@`@{@`@}@bCcGN]tAgDRe@JSFOfCcG`@}@hAqCPg@|@yB~AmDb@gATi@Xs@n@kBTs@j@{BTgAPaARkATcCJaA@k@HoABi@Bs@@uAAoBIyBEq@OmBMuAe@gCi@wBy@kCa@eAaAiBUi@[o@w@wAkAiBiAaBEGg@s@W_@U]MQm@u@_AgAY[q@y@CCmAsA_AaAuDuDUS[W[Y[W[Y[YWUCA[W[Y[WkEoDq@c@q@e@_CkBKKYUc@]_Am@gB{@eAa@y@WwA]kAUm@KG?i@Gc@Eo@EoA?wCDaBLiB^yBf@u@ZmAb@eCx@sC|@uAf@eDjA_A`@qE`BqBt@gEvAcCt@{Bh@g@LeAT{C`@_AHm@B}@Bu@BgA@sBE}BKoAMwBWw@Mc@Gm@KyAYmAWoAYsAWg@Iu@Im@G{@Kq@Ei@Ak@?_A@s@BK?gADiAJmFl@gCXy@NqAR{@Le@Fo@F[@O@g@Ai@Cc@G]G[GWGSGSK[K[O_@Wa@[UUkAsAsAeBcAoAo@u@eCsCwBiCgBsBw@aAe@k@eC{CeBuBaCmC"
//            },
//                "start_location": {
//                "lat": 25.2106392,
//                "lng": 55.2749038
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "0.7 km",
//                "value": 656
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 54
//            },
//                "end_location": {
//                "lat": 25.2741429,
//                "lng": 55.3484549
//            },
//                "html_instructions": "Take exit <b>62</b> toward <b>Hor Al Anz East</b>/<wbr/><b>Al Wuheida</b>/<wbr/><b>D91</b>/<wbr/><b>Al Twar</b>/<wbr/><b>DXB Airport Terminal 2</b>",
//                "maneuver": "ramp-right",
//                "polyline": {
//                "points": "qpfyCwixpIa@q@u@cA]a@y@cA}@iAcDqD{B_CyCmDqC{C"
//            },
//                "start_location": {
//                "lat": 25.2700132,
//                "lng": 55.34380280000001
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "2.9 km",
//                "value": 2946
//            },
//                "duration": {
//                "text": "4 mins",
//                "value": 249
//            },
//                "end_location": {
//                "lat": 25.2624564,
//                "lng": 55.37320039999999
//            },
//                "html_instructions": "Merge onto <b>Al Quds St</b>/<wbr/><b>D91</b> via the ramp to <b>Al Twar</b>/<wbr/><b>DXB Airport Terminal 2</b>",
//                "maneuver": "ramp-right",
//                "polyline": {
//                "points": "kjgyCyfypIESACGI]a@ACCE?E?AAI?C?KBI@CBKVq@~B}BrE{ETUxCoCjBiBjAiAxByBnBmB^_@JMZ_@HKd@o@PYf@_AHORg@Ti@J[Pg@Po@Nm@F[F]D_@Hi@Dg@Dk@Dk@@c@@{@J{CFqC@UBiB@iBCsA?eABy@Dy@Ds@F}@J_AHq@D]Hc@j@}Cj@yBX_Ah@eBd@kAp@uAp@oAdIyPR_@dBqD|CmGvA{C"
//            },
//                "start_location": {
//                "lat": 25.2741429,
//                "lng": 55.3484549
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "0.4 km",
//                "value": 430
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 79
//            },
//                "end_location": {
//                "lat": 25.2591713,
//                "lng": 55.37170260000001
//            },
//                "html_instructions": "Turn <b>right</b>",
//                "maneuver": "turn-right",
//                "polyline": {
//                "points": "kaeyCoa~pIXMHG@ABGFGNQDCBAFAH?D?^@tBrAb@Vh@ZZRdBfAlAn@RJl@`@v@h@"
//            },
//                "start_location": {
//                "lat": 25.2624564,
//                "lng": 55.37320039999999
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "0.1 km",
//                "value": 139
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 21
//            },
//                "end_location": {
//                "lat": 25.2598893,
//                "lng": 55.37072790000001
//            },
//                "html_instructions": "At the roundabout, take the <b>1st</b> exit",
//                "maneuver": "roundabout-right",
//                "polyline": {
//                "points": "yldyCcx}pI?H?B@B@D?BALAHAJCJCLEFA?KLIHQPOJUNSJ[L"
//            },
//                "start_location": {
//                "lat": 25.2591713,
//                "lng": 55.37170260000001
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "0.1 km",
//                "value": 113
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 32
//            },
//                "end_location": {
//                "lat": 25.2594675,
//                "lng": 55.36970119999999
//            },
//                "html_instructions": "Turn <b>left</b>",
//                "maneuver": "turn-left",
//                "polyline": {
//                "points": "iqdyCar}pIp@tBRp@Ld@"
//            },
//                "start_location": {
//                "lat": 25.2598893,
//                "lng": 55.37072790000001
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "0.1 km",
//                "value": 113
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 26
//            },
//                "end_location": {
//                "lat": 25.2593446,
//                "lng": 55.3687018
//            },
//                "html_instructions": "Turn <b>right</b>",
//                "maneuver": "turn-right",
//                "polyline": {
//                "points": "undyCsk}pIOHC@ADA@?B?DBLPj@JZHVDT?P?B?L?@"
//            },
//                "start_location": {
//                "lat": 25.2594675,
//                "lng": 55.36970119999999
//            },
//                "travel_mode": "DRIVING"
//            },
//            {
//                "distance": {
//                "text": "27 m",
//                "value": 27
//            },
//                "duration": {
//                "text": "1 min",
//                "value": 9
//            },
//                "end_location": {
//                "lat": 25.2591389,
//                "lng": 55.3685585
//            },
//                "html_instructions": "Turn <b>left</b>",
//                "maneuver": "turn-left",
//                "polyline": {
//                "points": "{mdyCke}pIf@Z"
//            },
//                "start_location": {
//                "lat": 25.2593446,
//                "lng": 55.3687018
//            },
//                "travel_mode": "DRIVING"
//            }
//            ],
//            "traffic_speed_entry": [],
//            "via_waypoint": []
//        }
//        ],
//        "overview_polyline": {
//        "points": "}a{xCcbkpIz@f@nF~CXNITSTQFKAWMa@WYGg@BeEmC_GoDkKiGwHuEsBkAmG_E}BuAqC_BeIeFaDmB{FkDyHoEoAq@QKwHoEwB{Aw@u@iAqA{@sAeAwBk@kB[mAkAmHgCqOQkAm@}C_EyWcBqK_@}B_@kCSeCEkAEuCAgEA{IDoEB}AJyA`@}C`@gBf@_Bb@kAxCgHr@qAbAyB|FoN|DeJzAyD|CgHx@qBhA_D`AoDf@iCRkATcCLmBLyBDiCAoBIyBU_DMuAe@gCi@wBy@kCa@eAaAiBq@yAw@wAkAiBoAiBcBcCyDsEqAwAuFwFiB}AsJeIcBiAkCwB}@s@_Am@gB{@_Cy@cDs@cCY_CEwCDaBLcFfAcC~@yGvB{FrBqGbCyHlCcCt@{Bh@mBb@{C`@_AHkBF}BDqFQgEe@{AUgCe@}Cq@{Ba@cBQmBQuAAgEJwHx@aEh@cFp@k@BqAEaAOs@O_Bo@aAs@aBiBwCuDuDiE_F}F}AmBkFqGaCmCa@q@sAeBwBmCcDqD{B_CyCmDqC{CGWe@k@EQAMBUDOVq@~B}BhFqFdGyFtIqIvAiBx@yA\\w@`@eAb@wAViAL}@NqAJwAVmKD_CA}DB_CJmBR}BNoAt@aEdAyDh@eBd@kAbBeD|Qy_@vA{CXMJIJOTUJCN?^@tBrAlAr@bFvCdBjA?LBLIn@ITML[Ze@Zo@XdAfDLd@OHEFADBR\\fANl@?d@f@Z"
//    },
//        "summary": "E11",
//        "warnings": [],
//        "waypoint_order": []
//    }
//    ],
//    "status": "OK"
//}

data class DirectionNetworkResponse(
    @SerializedName("geocoded_waypoints")
    val wayPoints: List<WayPoint>,
    val routes: List<Route>,
    val status: String)

data class WayPoint(
    @SerializedName("geocoder_status")
    val status: String,
    @SerializedName("place_id")
    val placeId: String,
    val types: List<String>)

data class Route(
    val bounds: Bounds,
    val copyrights: String,
    val legs: List<Leg>,
    @SerializedName("overview_polyline")
    val polyline: Polyline,
    val summary: String
)

data class Bounds (
    val northeast: Coordinate,
    val southwest: Coordinate
)

data class Leg (
    val distance: TextValue,
    val duration: TextValue,
    @SerializedName("start_address")
    val startAddress: String,
    @SerializedName("start_location")
    val startLocation: Coordinate,
    @SerializedName("end_address")
    val endAddress: String,
    @SerializedName("end_location")
    val endLocation: Coordinate,
    val steps: List<Step>
)

data class TextValue(val text: String, val value: Int)

data class Step(
    val distance: TextValue,
    val duration: TextValue,
    @SerializedName("end_location")
    val endLocation: Coordinate,
    @SerializedName("html_instructions")
    val htmlInstructions: String,
    val polyline: Polyline,
    @SerializedName("start_location")
    val startLocation: Coordinate,
    @SerializedName("travel_mode")
    val travelMode: String,
    val maneuver: String
)

data class Polyline(val points: String)

data class Coordinate(
    @SerializedName("lat")val latitude: Double,
    @SerializedName("lng")val longitude: Double)