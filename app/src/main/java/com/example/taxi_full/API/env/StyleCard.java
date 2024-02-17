package com.example.taxi_full.API.env;

import com.yandex.mapkit.mapview.MapView;

public final class StyleCard {

    public static final String style =  "[" +
            "        {" +
            "            \"types\": \"polyline\"," +
            "            \"tags\": {" +
            "                \"all\": [" +
            "                    \"road\"" +
            "                ]" +
            "            }," +
            "            \"stylers\": {" +
            "                \"color\": \"#DADADA\"" +
            "            }" +
            "        }," +
            "        {" +
            "            \"types\": \"polyline\"," +
            "            \"elements\": \"label.text.fill\"," +
            "            \"stylers\": {" +
            "                \"color\": \"#4D4D4D\"" +
            "            }" +
            "        }" +
            "    ]";

    public static final String styleLand =  "[" +
            "        {" +
            "            \"tags\": {" +
            "                \"all\": [" +
            "                    \"land\"" +
            "                ]" +
            "            }," +
            "            \"stylers\": {" +
            "                \"color\": \"#E6E7E8\"" +
            "            }" +
            "        }" +
            "    ]";

    public static void setMapStyle(MapView mapView){
        mapView.getMap().setMapStyle(style);
        //mapView.getMap().setLayerStyle(styleLand);
    }

}
