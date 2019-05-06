package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;

/**
 * Created by jukyungyoo on 2016-06-12
 */
public class Alarm_DTO {
    int no;
    int MtoS;
    String alarm_time;
    boolean is_AM;
    int bell_type;
    int ready_time;
    String arrive_place;
    String arrive_lat;
    String arrive_lon;
    String start_place;
    String start_lat;
    String start_lon ;

    String result_time;

    public String getResult_time() {
        return result_time;
    }

    public void setResult_time(String result_time) {
        this.result_time = result_time;
    }

    public String getAlarm_time() {
        return alarm_time;
    }

    public void setAlarm_time(String alarm_time) {
        this.alarm_time = alarm_time;
    }

    public String getArrive_lat() {
        return arrive_lat;
    }

    public void setArrive_lat(String arrive_lat) {
        this.arrive_lat = arrive_lat;
    }

    public String getArrive_lon() {
        return arrive_lon;
    }

    public void setArrive_lon(String arrive_lon) {
        this.arrive_lon = arrive_lon;
    }

    public String getArrive_place() {
        return arrive_place;
    }

    public void setArrive_place(String arrive_place) {
        this.arrive_place = arrive_place;
    }

    public int getBell_type() {
        return bell_type;
    }

    public void setBell_type(int bell_type) {
        this.bell_type = bell_type;
    }

    public boolean getis_AM() {
        return is_AM;
    }

    public void setIs_AM(boolean is_AM) {
        this.is_AM = is_AM;
    }

    public void setIs_AM(int is_AM) {
        if(is_AM ==1){
            this.is_AM = true;
        }else{
            this.is_AM = false;
        }

    }
    public int getMtoS() {
        return MtoS;
    }

    public void setMtoS(int mtoS) {
        MtoS = mtoS;
    }

    public int getReady_time() {
        return ready_time;
    }

    public void setReady_time(int ready_time) {
        this.ready_time = ready_time;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getStart_lon() {
        return start_lon;
    }

    public void setStart_lon(String start_lon) {
        this.start_lon = start_lon;
    }

    public String getStart_lat() {
        return start_lat;
    }

    public void setStart_lat(String start_lat) {
        this.start_lat = start_lat;
    }

    public String getStart_place() {
        return start_place;
    }

    public void setStart_place(String start_place) {
        this.start_place = start_place;
    }
    public int getIs_AMInt( ) {
        if(this.is_AM){
            return 1;
        }else{
            return 0;
        }
    }



}
