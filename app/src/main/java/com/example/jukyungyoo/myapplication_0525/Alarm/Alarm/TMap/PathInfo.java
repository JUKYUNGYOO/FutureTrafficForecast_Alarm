package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm.TMap;

/**
 * Created by jukyungyoo on 2016-06-07.
 */
public class PathInfo {
    String place_name;
    String place_lat;//위도
    String place_lon;//경도
    public PathInfo(String place_name, String place_lat, String place_lon){
        this.place_name = place_name;
        this.place_lat = place_lat;
        this.place_lon = place_lon;
    }
    public String getPlace_name() {
        return place_name;
    }

    public void setPlace_name(String place_name) {
        this.place_name = place_name;
    }

    public String getPlace_lat() {
        return place_lat;
    }

    public void setPlace_lat(String place_lat) {
        this.place_lat = place_lat;
    }

    public String getPlace_lon() {
        return place_lon;
    }

    public void setPlace_lon(String place_lon) {
        this.place_lon = place_lon;
    }
}
