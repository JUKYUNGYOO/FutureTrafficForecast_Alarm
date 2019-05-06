//TMap totalTIme을 받아와 backgroundservice로 alarm 시간 재 설정
package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;



import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.jukyungyoo.myapplication_0525.Alarm.Alarm.TMap.LogManager;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapView;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by jukyungyoo on 2016-06-12.
 */
public class BackAlarmManagerService  extends Service{
    TMapData tmapdata = new TMapData();

    private TMapView mMapView = null;
    public boolean isEnabled;
    private Context mContext;
    private ArrayList<Bitmap> mOverlayList;
    // public static String mApiKey = "e4e99ef1-e667-3e9a-a3d9-d063695b97fb";//주경
    public static String mApiKey = "8d6f8a0e-7556-327f-8f2e-36afe868c882";
    TMapGpsManager gps = null;
    private boolean isAM;
    public static final Integer AM = 23623;
    public static final Integer PM = 64562;
    public static final Integer MONDAY = 0x0000001;
    public static final Integer TUESDAY = 0x0000010;
    public static final Integer WEDNESDAY = 0x0000100;
    public static final Integer THURSDAY = 0x0001000;
    public static final Integer FRIDAY = 0x0010000;
    public static final Integer SATURDAY = 0x0100000;
    public static final Integer SUNDAY = 0x1000000;
    public int timeHour;
    public int timeMinute;
    private boolean repeatingDays[];

    ArrayList<Integer> traffic_time_list =  new ArrayList<Integer>();

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mContext = this;
        mMapView = new TMapView(this);
        configureMapView();
        gps = new TMapGpsManager(BackAlarmManagerService.this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps(); //위치 탐색을 시작 합니다.

        Log.d("service2","onCreate 실행");
        final int time =10;//10초
//          Toast.makeText(this, "안녕~ 난 서비스 : "+time, 0).show();
        // handler 통한 Thread 이용
        new Thread(new Runnable() {

            @Override
            public void run() {
                mRunning = true;
                while(mRunning) {
                    Log.d("service2","onCreate Thread실행");
                    SystemClock.sleep(time*1000);
                    //mHandler.sendEmptyMessage(0);
                    RotationServie();

                }
            }

        }).start();

    }

    @Override
    public void onDestroy() {
        Log.d("service2","onDestroy 실행");
        mRunning = false;
    }
    private void configureMapView() {
        mMapView.setSKPMapApiKey(mApiKey);
    }
    public boolean getRepeatingDay(int dayOfWeek) {
        return repeatingDays[dayOfWeek];
    }
    long beforeTime =0;
    protected boolean mRunning;
    public void timeMachine( HashMap <String,String> path_info ) {
        TMapData tmapdata = new TMapData();

        //길 안내의 기준이 되는 출발 혹은 도착시간
        Date currentTime = new Date();

        tmapdata.findTimeMachineCarPath(path_info, currentTime, null,
                new TMapData.FindTimeMachineCarPathListenerCallback() {
                    @TargetApi(Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onFindTimeMachineCarPath(Document document) {
                        //Document는 XML, JSON처럼 해당 노드와 아이템을 파싱하시면 됩니다.
                        document.getDocumentElement().normalize();

                        String pointType ;
                        int totalDistance ;
                        int totalTime;

                        /////////tttt
                        TransformerFactory tf = TransformerFactory.newInstance();
                        Transformer transformer = null;
                        try {
                            transformer = tf.newTransformer();
                        } catch (TransformerConfigurationException e) {
                            e.printStackTrace();
                        }
                        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                        StringWriter writer = new StringWriter();
                        try {
                            transformer.transform(new DOMSource(document), new StreamResult(writer));
                        } catch (TransformerException e) {
                            e.printStackTrace();
                        }
                        String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
                        //LogManager.printLog("tttt timemachine xml\n"+output);
                        //////t지울거
                        NodeList totalDistanceList   = document.getElementsByTagName("tmap:totalDistance");
                        totalDistance = Integer.parseInt(totalDistanceList.item(0).getFirstChild().getNodeValue());
                        NodeList totalTimeList   = document.getElementsByTagName("tmap:totalTime");
                        totalTime = Integer.parseInt(totalTimeList.item(0).getFirstChild().getNodeValue());
                        NodeList pointTypeList   = document.getElementsByTagName("tmap:pointType");
                        pointType = pointTypeList.item(0).getFirstChild().getNodeValue();

                        LogManager.printLog("tttt timemachine pointType:"+pointType);
                        LogManager.printLog("tttt timemachine totalDistance:"+totalDistance);
                        LogManager.printLog("tttt timemachine totalTime:"+totalTime);
                        LogManager.printLog("service2 timemachine traffictime:"+totalTime);
                        traffic_time_list.add(totalTime);

                    }
                }

        );
        //미래의 특정시간에 경로 탐색 결과를 가져온다
        //  tmapdata.findTimeMachineCarPath(path_info,  currentTime, null);


    }
    public  void RotationServie() {

        ArrayList<Alarm_DTO> alarm_list = new ArrayList<Alarm_DTO>();
        DBHelper helper = new DBHelper(this);
        traffic_time_list.clear();

        Calendar cal = Calendar.getInstance();
        int strWeek = 0; //오늘요일
        int strNextWeek = 0;//내일 요일
        final int nowHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        final int nowMinute = Calendar.getInstance().get(Calendar.MINUTE);
        boolean alarmSet = false;

        int nWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (nWeek == 1) {
            strWeek = SUNDAY;
        } else if (nWeek == 2) {
            strWeek = MONDAY;
        } else if (nWeek == 3) {
            strWeek = TUESDAY;
        } else if (nWeek == 4) {
            strWeek = WEDNESDAY;
        } else if (nWeek == 5) {
            strWeek = THURSDAY;
        } else if (nWeek == 6) {
            strWeek = FRIDAY;
        } else if (nWeek == 7) {
            strWeek = SATURDAY;
        }


        //오늘 요일 알람 불러오기
        Cursor mycursor = helper.getTodayAlarmTable(strWeek);
        mycursor.moveToFirst();
        while(!mycursor.isAfterLast()){
            Alarm_DTO inform = new Alarm_DTO();
            int no = mycursor.getInt(mycursor.getColumnIndex("no"));
            int ready_time = mycursor.getInt(mycursor.getColumnIndex("ready_time"));
            String alarm_time2 = mycursor.getString(mycursor.getColumnIndex("alarm_time"));
            int MtoS = mycursor.getInt(mycursor.getColumnIndex("MtoS"));
            int isAM = mycursor.getInt(mycursor.getColumnIndex("is_AM"));
            int bell_type =  mycursor.getInt(mycursor.getColumnIndex("bell_type"));

            String arrive_place = mycursor.getString(mycursor.getColumnIndex("arrive_place"));
            String arrive_lat = mycursor.getString(mycursor.getColumnIndex("arrive_lat"));
            String arrive_lon = mycursor.getString(mycursor.getColumnIndex("arrive_lon"));

            String start_place = mycursor.getString(mycursor.getColumnIndex("start_place"));
            String start_lat = mycursor.getString(mycursor.getColumnIndex("start_lat"));
            String start_lon = mycursor.getString(mycursor.getColumnIndex("start_lon"));
            Log.d("service2","요일별db 불러오기 no "+no+" "+ready_time+" "+alarm_time2+" "+MtoS+" "+isAM+" "+arrive_place+" "+start_place+" "+" "+" ");
            inform.setNo(no);
            inform.setReady_time(ready_time);
            inform.setAlarm_time(alarm_time2);
            inform.setMtoS(MtoS);
            inform.setIs_AM(isAM);
            inform.setArrive_place(arrive_place);
            inform.setArrive_lat(arrive_lat);
            inform.setArrive_lon(arrive_lon);
            inform.setStart_place(start_place);
            inform.setStart_lat(start_lat);
            inform.setStart_lon(start_lon);
            inform.setBell_type(bell_type);


            HashMap<String,String> path_info = new HashMap<String, String>();
            path_info.put("rStName",inform.getStart_place());
            path_info.put("rStlat",inform.getStart_lat());
            path_info.put("rStlon",inform.getStart_lon());

            path_info.put("place_name", inform.getStart_place());  //출발지 명칭
            path_info.put("rGoName",inform.getArrive_place());//도착지 명칭
            path_info.put("rGolat",inform.getArrive_lat());//도착지 위도
            path_info.put("rGolon",inform.getArrive_lon());//도착지 경도
            path_info.put("type", "arrival"); //경로 서비스 구분으로 departure
            timeMachine(path_info);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            alarm_list.add(inform);
            mycursor.moveToNext();
       }
        // 내일 알람 일정 불러오기
    /*    nWeek = nWeek +1;

        if(nWeek >7 ){
            nWeek = 1;
        }
        if (nWeek == 1) {
            strNextWeek = SUNDAY;
        } else if (nWeek == 2) {
            strNextWeek = MONDAY;
        } else if (nWeek == 3) {
            strNextWeek = TUESDAY;
        } else if (nWeek == 4) {
            strNextWeek = WEDNESDAY;
        } else if (nWeek == 5) {
            strNextWeek = THURSDAY;
        } else if (nWeek == 6) {
            strNextWeek = FRIDAY;
        } else if (nWeek == 7) {
            strNextWeek = SATURDAY;
        }

        Cursor mycursor2 = helper.getTodayAlarmTable(strNextWeek);
        mycursor2.moveToFirst();
        while(!mycursor2.isAfterLast()){
            Alarm_DTO inform = new Alarm_DTO();
            int no = mycursor2.getInt(mycursor2.getColumnIndex("no"));
            int ready_time = mycursor2.getInt(mycursor2.getColumnIndex("ready_time"));
            String alarm_time = mycursor2.getString(mycursor2.getColumnIndex("alarm_time"));
            int MtoS = mycursor2.getInt(mycursor2.getColumnIndex("MtoS"));
            int isAM = mycursor2.getInt(mycursor2.getColumnIndex("is_AM"));

            String arrive_place = mycursor2.getString(mycursor2.getColumnIndex("arrive_place"));
            String arrive_lat = mycursor2.getString(mycursor2.getColumnIndex("arrive_lat"));
            String arrive_lon = mycursor2.getString(mycursor2.getColumnIndex("arrive_lon"));

            String start_place = mycursor2.getString(mycursor2.getColumnIndex("start_place"));
            String start_lat = mycursor2.getString(mycursor2.getColumnIndex("start_lat"));
            String start_lon = mycursor2.getString(mycursor2.getColumnIndex("start_lon"));

            inform.setNo(no);
            inform.setReady_time(ready_time);
            inform.setAlarm_time(alarm_time);
            inform.setMtoS(MtoS);
            inform.setIs_AM(isAM);
            inform.setArrive_place(arrive_place);
            inform.setArrive_lat(arrive_lat);
            inform.setArrive_lon(arrive_lon);
            inform.setStart_place(start_place);
            inform.setStart_lat(start_lat);
            inform.setStart_lon(start_lon);


            HashMap<String,String> path_info = new HashMap<String, String>();
            path_info.put("rStName",inform.getStart_place());
            path_info.put("rStlat",inform.getStart_lat());
            path_info.put("rStlon",inform.getStart_lon());

            path_info.put("place_name", inform.getStart_place());  //출발지 명칭
            path_info.put("rGoName",inform.getArrive_place());//도착지 명칭
            path_info.put("rGolat",inform.getArrive_lat());//도착지 위도
            path_info.put("rGolon",inform.getArrive_lon());//도착지 경도
            path_info.put("type", "arrival"); //경로 서비스 구분으로 departure
            timeMachine(path_info);
            Thread.sleep(1000);
            alarm_list.add(inform);
        }
*/

        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        ArrayList<Alarm_DTO> dto_list = new ArrayList<Alarm_DTO>();

        for(int i = 0; i<alarm_list.size(); i++){
            Alarm_DTO mydto = alarm_list.get(i);
            int traffic_time = traffic_time_list.get(i);//초
            int readyindex = mydto.getReady_time();
            int readytime = 0;
            switch (readyindex){
                case 0:
                    readytime = 20;
                    break;
                case 1:
                    readytime = 30;
                    break;
                case 2:
                    readytime = 40;
                    break;
                case 3:
                    readytime = 50;
                    break;
                case 4:
                    readytime = 60;
                    break;
                case 5:
                    readytime = 70;
                    break;

            }
            String alarm_time = mydto.getAlarm_time();
            StringTokenizer mytoken = new StringTokenizer(alarm_time,":");
            int alarm_hour = Integer.parseInt(mytoken.nextToken());
            int alarm_min = Integer.parseInt(mytoken.nextToken());
            boolean is_AM = mydto.getis_AM();
            if(!is_AM){//오후일경우
                alarm_hour = alarm_hour + 12;
            }
            alarm_min = alarm_hour*60+ alarm_min;//시간을 전부 분으로

            int traffic_min = traffic_time /60; //초를 분으로


            alarm_min = alarm_min - ( readytime + traffic_min ) ;
            alarm_hour = alarm_min/60;
            alarm_min = alarm_min%60;
            if(alarm_hour>=12){
                alarm_hour = alarm_hour- 12;
                is_AM = false;
            }else{
                is_AM = true;
            }
            //알람시간 계산


            String result_time = alarm_hour+":"+alarm_min+":00" ;
            mydto.setResult_time(result_time);
            if(!is_AM){
                alarm_hour = alarm_hour + 12;
            }

            //redto 를 사용해서 alarm manager 등록
            final Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, alarm_hour);
            calendar.set(Calendar.MINUTE, alarm_min);
            // create an intent to the Alarm Receiver class
           final Intent my_intent = new Intent(this, AlarmReceiver.class);
            //final Intent my_intent = new Intent(this, AlarmRing.class);
            my_intent.putExtra("extra", "alarm on");
            // put in an extra int into my_intent
            // tells the clock that you want a certain value from the drop-down menu/spinner
            my_intent.putExtra("whale_choice", mydto.getBell_type());
            //my_intent.putExtra("weekday",)
            Context context = this;
            PendingIntent pIntent =  PendingIntent.getBroadcast(this, 0,
                    my_intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Calendar current_cal = Calendar.getInstance();


            Log.d("service2"," if 현재시간- 이전시간 : "+ (System.currentTimeMillis() - beforeTime));
           // if ( (System.currentTimeMillis() - beforeTime) > 60252) {

                if(current_cal.getTimeInMillis()-calendar.getTimeInMillis()<0){
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent);
                    }
                    Log.d("service2"," 알람 등록 ");
                    beforeTime = System.currentTimeMillis();
                }

            //}

            //Log.d("service2"," current_cal.getTimeInMillis() : "+ current_cal.getTimeInMillis());
            Log.d("service2", "calcresulttime "+(current_cal.getTimeInMillis()-calendar.getTimeInMillis()));
            Log.d("service2","no : "+mydto.getNo());
            Log.d("service2","alarm time: "+mydto.getAlarm_time());

            Log.d("service2","ready time: "+mydto.getReady_time());
            Log.d("service2","trrafic time(min): "+traffic_min);
            Log.d("service2","result time: "+is_AM+" "+alarm_hour+":"+alarm_min);
            dto_list.add(mydto);
        }

        //db result_time update
        for(int i = 0; i<dto_list.size(); i++){
            //db update result_time
            Alarm_DTO redto = dto_list.get(i);
            helper.update_ResultTime(redto);

        }

    }

    // 제일 중요한 메서드! (서비스 작동내용을 넣어준다.)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service2","onStartCommand 실행");
        // final int time = intent.getIntExtra("time", 0);

        return START_STICKY_COMPATIBILITY;
    }
}
