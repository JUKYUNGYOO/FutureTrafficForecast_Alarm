
package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import com.example.jukyungyoo.myapplication_0525.Alarm.Alarm.TMap.LogManager;
import com.example.jukyungyoo.myapplication_0525.Alarm.Alarm.TMap.PathInfo;
import com.example.jukyungyoo.myapplication_0525.R;
import com.skp.Tmap.TMapData;
import com.skp.Tmap.TMapGpsManager;
import com.skp.Tmap.TMapPOIItem;
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

public class AlarmActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        TMapGpsManager.onLocationChangedCallback {

    //to make our alarm manager
    AlarmManager alarm_manager;
    TimePicker alarm_timepicker;
    TextView update_text;
    Context context;
    PendingIntent pending_intent;
    SQLiteDatabase db;

    int choose_whale_sound;
    ToggleButton toggle1,toggle2,toggle3,toggle4,toggle5,toggle6,toggle7;
    ListView alarmList1;
    EditText depart_edit;
    EditText dest_edit;
    private String mHour_string;
    private String mMinute_string;
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
    private Integer oxWeek;

    DBHelper helper;

    //TMap

    ListView listView;
    ArrayAdapter<String> mAdapter;
    Handler mHandler = null;




    ArrayList<PathInfo> start_path_list  = new ArrayList<PathInfo>();//춞발지 리스트
    ArrayList<PathInfo> depart_path_list  = new ArrayList<PathInfo>();//도착지 리스트
    TMapData tmapdata = new TMapData();

    EditText inputView;
    TextView messageView;

    PathInfo start_pathinfo = new PathInfo("s","1","3");//출발지 정보
    PathInfo depart_pathinfo  = new PathInfo("s","1","3");//도착지 정보

    HashMap <String,String> path_info = new HashMap<String, String>();

    private TMapView mMapView = null;

    private Context mContext;
    private ArrayList<Bitmap> mOverlayList;

   // public static String mApiKey = "e4e99ef1-e667-3e9a-a3d9-d063695b97fb";//주경
    public static String mApiKey = "8d6f8a0e-7556-327f-8f2e-36afe868c882";

    private static final int[] mArrayMapButton = {
            R.id.btnFindAllPoi_departure,
            R.id.btnFindAllPoi_destination,
            R.id.btnTimeMachine
    };
    long totalTIme;
    private    int       m_nCurrentZoomLevel = 0;
    private    double       m_Latitude  = 0;
    private     double     m_Longitude = 0;
    private    boolean    m_bShowMapIcon = false;
    private    boolean    m_bTrafficeMode = false;
    private    boolean    m_bTrackingMode = false;
    TMapGpsManager gps = null;
    public boolean isEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        //db
        // SQLITE DB 생성 부분
        helper = new DBHelper(this);

        Intent myintent = getIntent();
        final int no = myintent.getIntExtra("no", -1);

        listView =  (ListView)findViewById(R.id.listView1);
        depart_edit = (EditText)findViewById(R.id.depart_editText);
        dest_edit = (EditText)findViewById(R.id.dest_editText);
        mHandler = new Handler();
        mContext = this;
        mMapView = new TMapView(this);



        configureMapView();



        gps = new TMapGpsManager(AlarmActivity.this);
        gps.setMinTime(1000);
        gps.setMinDistance(5);
        gps.setProvider(gps.NETWORK_PROVIDER);
        gps.OpenGps(); //위치 탐색을 시작 합니다.

        final ToggleButton tBtn_Monday = (ToggleButton) findViewById(R.id.toggle1);
        final ToggleButton tBtn_Tuesday = (ToggleButton) findViewById(R.id.toggle2);
        final ToggleButton tBtn_Wednesday = (ToggleButton) findViewById(R.id.toggle3);
        final ToggleButton tBtn_Thursday = (ToggleButton) findViewById(R.id.toggle4);
        final ToggleButton tBtn_Friday = (ToggleButton) findViewById(R.id.toggle5);
        final ToggleButton tBtn_Saturday = (ToggleButton) findViewById(R.id.toggle6);
        final ToggleButton tBtn_Sunday = (ToggleButton) findViewById(R.id.toggle7);

        final Button departure = (Button)findViewById(R.id.btnFindAllPoi_departure);
        departure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAllPoi();

            }
        });
        final Button destination = (Button)findViewById(R.id.btnFindAllPoi_destination);
        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findAllPoi_destination();


            }
        });
        final Button TimeMachine = (Button)findViewById(R.id.btnTimeMachine);
        TimeMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeMachine();
            }
        });

        tBtn_Monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tBtn_Monday.isChecked()){
                    tBtn_Monday.setTextColor(Color.RED);
                } else {
                    tBtn_Monday.setTextColor(Color.DKGRAY);
                }
            }
        });

        tBtn_Tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tBtn_Tuesday.isChecked()){
                    tBtn_Tuesday.setTextColor(Color.RED);
                } else {
                    tBtn_Tuesday.setTextColor(Color.DKGRAY);
                }
            }
        });

        tBtn_Wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tBtn_Wednesday.isChecked()){
                    tBtn_Wednesday.setTextColor(Color.RED);
                } else {
                    tBtn_Wednesday.setTextColor(Color.DKGRAY);
                }
            }
        });

        tBtn_Thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tBtn_Thursday.isChecked()){
                    tBtn_Thursday.setTextColor(Color.RED);
                } else {
                    tBtn_Thursday.setTextColor(Color.DKGRAY);
                }
            }
        });

        tBtn_Friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tBtn_Friday.isChecked()){
                    tBtn_Friday.setTextColor(Color.RED);
                } else {
                    tBtn_Friday.setTextColor(Color.DKGRAY);
                }
            }
        });

        tBtn_Saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tBtn_Saturday.isChecked()){
                    tBtn_Saturday.setTextColor(Color.RED);
                } else {
                    tBtn_Saturday.setTextColor(Color.DKGRAY);
                }
            }
        });

        tBtn_Sunday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tBtn_Sunday.isChecked()){
                    tBtn_Sunday.setTextColor(Color.RED);
                } else {
                    tBtn_Sunday.setTextColor(Color.DKGRAY);
                }
            }
        });


        this.context = this;

        // initialize our alarm manager
        alarm_manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        //initialize our timepicker
        alarm_timepicker = (TimePicker) findViewById(R.id.timePicker);

        //initialize our text update box
        //update_text = (TextView) findViewById(R.id.update_text);

        // create an instance of a calendar
        final Calendar calendar = Calendar.getInstance();

        // create an intent to the Alarm Receiver class
        final Intent my_intent = new Intent(this.context, AlarmReceiver.class);


        // create the spinner in the main UI
        final Spinner bell_type_spinner  = (Spinner) findViewById(R.id.bell_spinner );
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.whale_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        bell_type_spinner .setAdapter(adapter);
        // Set an onclick listener to the onItemSelected method
        bell_type_spinner .setOnItemSelectedListener(this);

        final Spinner ready_time_spinner  = (Spinner)findViewById(R.id.ready_time_spinner );
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.number, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ready_time_spinner .setAdapter(adapter1);


        if( no != -1){
            Alarm_DTO mydto = helper.getAlarmTable(no);
            depart_edit.setText(mydto.getStart_place());
            dest_edit.setText(mydto.getArrive_place());

            //DB에서 받아온 int요일 --> toggleButton 체크로..
            int MtoS = mydto.getMtoS();
            if ( (MtoS & MONDAY) == MONDAY){
                tBtn_Monday.setChecked(true);
                tBtn_Monday.setTextColor(Color.RED);
            }
            if ( (MtoS & TUESDAY) == TUESDAY) {
                tBtn_Tuesday.setChecked(true);
                tBtn_Tuesday.setTextColor(Color.RED);
            }
            if ( (MtoS & WEDNESDAY) == WEDNESDAY) {
                tBtn_Wednesday.setChecked(true);
                tBtn_Wednesday.setTextColor(Color.RED);
            }
            if ( (MtoS & THURSDAY) == THURSDAY) {
                tBtn_Thursday.setChecked(true);
                tBtn_Thursday.setTextColor(Color.RED);
            }
            if ( (MtoS & FRIDAY) == FRIDAY) {
                tBtn_Friday.setChecked(true);
                tBtn_Friday.setTextColor(Color.RED);
            }
            if ( (MtoS & SATURDAY) == SATURDAY) {
                tBtn_Saturday.setChecked(true);
                tBtn_Saturday.setTextColor(Color.RED);
            }
            if ( (MtoS & SUNDAY) == SUNDAY) {
                tBtn_Sunday.setChecked(true);
                tBtn_Sunday.setTextColor(Color.RED);
            }
            String mytime = mydto.getAlarm_time();
            StringTokenizer st = new StringTokenizer(mytime,":");

            int myhour = Integer.parseInt(st.nextToken());
            int myminute = Integer.parseInt(st.nextToken());
            if(!mydto.getis_AM()){
                myhour = myhour+12;
            }
            alarm_timepicker.setCurrentHour(myhour);
            alarm_timepicker.setCurrentMinute(myminute);
            // alarm_timepicker.setMinute(myminute);
            alarm_timepicker.setIs24HourView(false);

            bell_type_spinner.setSelection(mydto.getBell_type());
            ready_time_spinner.setSelection(mydto.getReady_time());
            start_pathinfo.setPlace_name(mydto.getStart_place());
            start_pathinfo.setPlace_lat(mydto.getStart_lat());
            start_pathinfo.setPlace_lon(mydto.getStart_lon());
            depart_pathinfo.setPlace_name(mydto.getArrive_place());
            depart_pathinfo.setPlace_lon(mydto.getArrive_lon());
            depart_pathinfo.setPlace_lat(mydto.getArrive_lat());

        }

        //alarm on
        final Button save_button = (Button )findViewById(R.id.btn_save);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DB에서 getReadable로 최신 정보 갱신

                // 갱신된 totaltime을 DB에 넣기
                // 오늘 울릴 Alarm 뽑기
                //PendingIntent 알람 등록

                // setting calendar instance with the hour and minute that we picked
                // on the time picker
                calendar.set(Calendar.HOUR_OF_DAY, alarm_timepicker.getCurrentHour());
                calendar.set(Calendar.MINUTE, alarm_timepicker.getCurrentMinute());

                // get the int values of the hour and minute
                int hour = alarm_timepicker.getCurrentHour();
                int minute = alarm_timepicker.getCurrentMinute();

                boolean[] week = {false,  tBtn_Sunday.isChecked() , tBtn_Monday.isChecked(), tBtn_Tuesday.isChecked(), tBtn_Wednesday.isChecked(),
                        tBtn_Thursday.isChecked(), tBtn_Friday.isChecked(), tBtn_Saturday.isChecked()};//일 월 화 수 목 금 토
                oxWeek = (tBtn_Monday.isChecked()?MONDAY:0) | (tBtn_Tuesday.isChecked()?TUESDAY:0) | (tBtn_Wednesday.isChecked()?WEDNESDAY:0) |
                        (tBtn_Thursday.isChecked()?THURSDAY:0) | (tBtn_Friday.isChecked()?FRIDAY:0) | (tBtn_Saturday.isChecked()?SATURDAY:0) |
                        (tBtn_Sunday.isChecked()?SUNDAY:0);
                long oneday = 24*60*60*1000; //24시간

                // convert the int values to strings
                mHour_string = String.valueOf(hour);
                isAM = true;
                mMinute_string = String.valueOf(minute);

                // convert 24-hour time to 12-hour time
                if (hour >= 12) {
                    mHour_string = String.valueOf(hour - 12);
                    isAM = false;
                }

                if (minute < 10) {
                    //10:7 --> 10:07
                    mMinute_string = "0" + String.valueOf(minute);
                }


                Alarm_DTO myadto = new Alarm_DTO();

                myadto.setAlarm_time(mHour_string+":"+mMinute_string+":00");
                myadto.setIs_AM(isAM);
                myadto.setMtoS(oxWeek);

                myadto.setBell_type(bell_type_spinner.getSelectedItemPosition());

                int ready_time_choice = ready_time_spinner.getSelectedItemPosition();
                myadto.setReady_time(ready_time_choice);//값 가져오기기
                //출발지의 위도와 경도를 DB에 저장하기
                myadto.setArrive_place(depart_pathinfo.getPlace_name());
                myadto.setArrive_lat(depart_pathinfo.getPlace_lat());
                myadto.setArrive_lon(depart_pathinfo.getPlace_lon());
                myadto.setStart_place(start_pathinfo.getPlace_name());
                myadto.setStart_lon(start_pathinfo.getPlace_lon());
                myadto.setStart_lat(start_pathinfo.getPlace_lat());

                if(no!=-1){
                    myadto.setNo(no);
                    helper.update_selected(myadto);

                }else{
                    helper.insertAlarmTable(myadto);
                }



                StringBuilder strWeek = new StringBuilder();

                if((oxWeek & MONDAY) != 0)
                    strWeek.append("월");
                if((oxWeek & TUESDAY) != 0)
                    strWeek.append("화");
                if((oxWeek & WEDNESDAY) != 0)
                    strWeek.append("수");
                if((oxWeek & THURSDAY) != 0)
                    strWeek.append("목");
                if((oxWeek & FRIDAY) != 0)
                    strWeek.append("금");
                if((oxWeek & SATURDAY) != 0)
                    strWeek.append("토");
                if((oxWeek & SUNDAY) != 0)
                    strWeek.append("일");
                String AM_PM = new String();
                if(isAM)
                    AM_PM = "오전";
                else
                    AM_PM = "오후";
                String item = new String(AM_PM + "  " + mHour_string + ":" + mMinute_string + "  " + strWeek);
                Intent intent = new Intent();
                intent.putExtra("item", item);
                setResult(111000, intent);
                finish();

                //Pendingintent 로 AlarmReceiver에 메시지 전송 알람음울리기

            }
        });



    }

    private void findAllPoi() {


        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);

        builder.setTitle("POI 통합 검색");

        final EditText input = new EditText(this);

        builder.setView(input);

        builder.setPositiveButton("출발", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strData = input.getText().toString();

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AlarmActivity.this);
                TMapData tmapdata = new TMapData();
                start_path_list.clear();
                tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        mAdapter = new ArrayAdapter<String>(AlarmActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem item = poiItem.get(i);

                            mAdapter.add("POI Name: " + item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", ""));

                            LogManager.printLog("POI Name: " + item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                                    "Point: " + item.getPOIPoint().toString());
                            PathInfo myinfo = new PathInfo(item.getPOIName().toString(), item.noorLat.toString(),item.noorLon.toString());
                            start_path_list.add(myinfo);

                            //HashMap<String, String> path_info = new HashMap<>();
                            path_info.put("rStName",item.getPOIName().toString());
                            path_info.put("rStlat",item.noorLat.toString());
                            path_info.put("rStlon",item.noorLon.toString());

                            path_info.put("place_name", item.getPOIName().toString());  //출발지 명칭

                        }
                        mAdapter.notifyDataSetChanged();

                    }
                });
                try {
                    Thread.sleep(1000); //findAllPOI로 데이터 정보를 얻어오는데 1초 기다려줌
                    alertBuilder.setTitle("항목중에 하나를 선택하세요.");
                    alertBuilder.setAdapter(mAdapter,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // AlertDialog 안에 있는 AlertDialog
                            String strName = mAdapter.getItem(id);
                            LogManager.printLog("tttt id"+id);
                            //start_pathinfo = start_path_list.get(id);
                            PathInfo tempinfo = start_path_list.get(id);
                            start_pathinfo.setPlace_name(tempinfo.getPlace_name());
                            start_pathinfo.setPlace_lon(tempinfo.getPlace_lon());
                            start_pathinfo.setPlace_lat(tempinfo.getPlace_lat());
                            LogManager.printLog("tttt start_path "+start_pathinfo.getPlace_name()+" lat:"+start_pathinfo.getPlace_lat()+" lon:"+start_pathinfo.getPlace_lon());

                            depart_edit.setText(start_pathinfo.getPlace_name());
                            AlertDialog.Builder innBuilder = new AlertDialog.Builder(
                                    AlarmActivity.this);
                            innBuilder.setMessage(strName);
                            innBuilder.setTitle("당신이 선택한 것은 ");
                            innBuilder.setPositiveButton(
                                    "확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            innBuilder.show();

                        }}
                    );
                    alertBuilder.create().show();
                }catch(InterruptedException e){
                    LogManager.printLog("tttt InterruptedException");
                }

            }
        });


        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        builder.show();

    }



    //도착지의 위도와 경도를 얻어오기
    private void findAllPoi_destination(){
        TMapData tmapdata = new TMapData();
        AlertDialog.Builder builder = new AlertDialog.Builder(AlarmActivity.this);

        builder.setTitle("POI 통합 검색");

        final EditText input = new EditText(this);

        builder.setView(input);


        builder.setPositiveButton("도착", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strData = input.getText().toString();

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AlarmActivity.this);
                final TMapData tmapdata = new TMapData();
                tmapdata.findAllPOI(strData, new TMapData.FindAllPOIListenerCallback() {
                    @Override
                    public void onFindAllPOI(ArrayList<TMapPOIItem> poiItem) {
                        mAdapter = new ArrayAdapter<String>(AlarmActivity.this, android.R.layout.simple_list_item_1, new ArrayList<String>());
                        for (int i = 0; i < poiItem.size(); i++) {
                            TMapPOIItem item = poiItem.get(i);

                            mAdapter.add("POI Name: " + item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", ""));

                            LogManager.printLog("POI Name: " + item.getPOIName().toString() + ", " +
                                    "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                                    "Point: " + item.getPOIPoint().toString());


                            PathInfo myinfo = new PathInfo(item.getPOIName().toString(), item.noorLat.toString(),item.noorLon.toString());
                            depart_path_list.add(myinfo);


                            path_info.put("rGoName",item.getPOIName().toString());//도착지 명칭
                            path_info.put("rGolat",item.noorLat.toString());//도착지 위도
                            path_info.put("rGolon",item.noorLon.toString());//도착지 경도
                            path_info.put("type", "arrival"); //경로 서비스 구분으로 departure






                        }


                    }
                });
                try {
                    Thread.sleep(1000);
                    alertBuilder.setTitle("항목중에 하나를 선택하세요.");
                    alertBuilder.setAdapter(mAdapter,new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // AlertDialog 안에 있는 AlertDialog
                            String strName = mAdapter.getItem(id);
                            PathInfo tempinfo = depart_path_list.get(id);
                            depart_pathinfo.setPlace_name(tempinfo.getPlace_name());
                            depart_pathinfo.setPlace_lat(tempinfo.getPlace_lat());
                            depart_pathinfo.setPlace_lon(tempinfo.getPlace_lon());
                            LogManager.printLog("tttt depart_path "+depart_pathinfo.getPlace_name()+" lat:"+depart_pathinfo.getPlace_lat()+" lon:"+
                                    depart_pathinfo.getPlace_lon());
                            dest_edit.setText(depart_pathinfo.getPlace_name());
                            AlertDialog.Builder innBuilder = new AlertDialog.Builder(
                                    AlarmActivity.this);
                            innBuilder.setMessage(strName);
                            innBuilder.setTitle("당신이 선택한 것은 ");
                            innBuilder.setPositiveButton(
                                    "확인",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            innBuilder.show();

                        }}
                    );
                    alertBuilder.create().show();
                }catch(InterruptedException e){
                    LogManager.printLog("tttt InterruptedException");
                }

            }
        });


        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }
    //ArrayInfo<PathInfo>에 저장된 출발지,도착지의 위도 경도를 HashMap에 저장 해주기


    public void timeMachine( ) {
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
                        LogManager.printLog("tttt timemachine xml\n"+output);
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



                    }
                }

        );
        //미래의 특정시간에 경로 탐색 결과를 가져온다
        //  tmapdata.findTimeMachineCarPath(path_info,  currentTime, null);


    }



    private void configureMapView() {
        mMapView.setSKPMapApiKey(mApiKey);
    }
    private void set_alarm_text(String output) {
        update_text.setText(output);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)

        // outputting whatever id the user has selected
        //Toast.makeText(parent.getContext(), "the spinner item is "
        //        + id, Toast.LENGTH_SHORT).show();
        choose_whale_sound = (int) id;


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback

    }


    @Override
    public void onLocationChange(Location location) {

    }
}
