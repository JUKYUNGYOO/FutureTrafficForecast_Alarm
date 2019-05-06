package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jukyungyoo.myapplication_0525.R;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by kyungeun on 2016-06-01.
 */
public class AlarmList extends Activity  {

    private ListView alarmListview;

    private ArrayList<String> items2 = new ArrayList<String>();
    private  ArrayList<Integer> no_list = new ArrayList<Integer>();
    private ArrayAdapter<String> alarmadapter;
    private View m_View;

    DBHelper helper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_list);

        // SQLITE DB 생성 부분
        helper = new DBHelper(this);


        alarmadapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, items2);

        Button btnMove_AlarmSetting = (Button) findViewById(R.id.alarmPlusBtn);
        btnMove_AlarmSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(),AlarmActivity.class);

                startActivityForResult(intent1,101010);
            }
        });


        // listview에 있는 모든 알람 삭제
        Button btnDelete_Allalarm = (Button) findViewById(R.id.alarmDeleteBtn);
        btnDelete_Allalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder reInfo = new AlertDialog.Builder(AlarmList.this);
                reInfo.setTitle("메뉴");
                reInfo.setMessage("알람을 전부 삭제 하시겠습니까?");

                reInfo.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        helper.delete_All_AlarmTable();
                        onResume();

                        Toast.makeText(getApplicationContext(),"전부 삭제되었습니다",Toast.LENGTH_SHORT).show();
                    }
                });


                reInfo.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                reInfo.create();
                reInfo.show();


            }
        });

        alarmListview = (ListView) findViewById(R.id.alarmList1);
        alarmListview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        alarmListview.setAdapter(alarmadapter);
        alarmListview.setOnItemLongClickListener(onItemLongCLickEvent);
        alarmListview.setOnItemClickListener(onItemClickEvent);

    }




    //롱클릭 했을 때 알람리스트와 DB에서 알람삭제
    OnItemLongClickListener onItemLongCLickEvent = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            ShowMenu(arg2, alarmadapter.getItem(arg2));
            return true;
        }

    };


    // Item 클릭했을 때 AlarmActivity로 화면 이동
    AdapterView.OnItemClickListener onItemClickEvent = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parentView, View clickedView, int position, long id)
        {
            Intent intent1 = new Intent(getApplicationContext(),AlarmActivity.class);
            int no = no_list.get(position);
            intent1.putExtra("no",no);
            startActivityForResult(intent1,101010);

        }
    };

    private void ShowMenu(final int arrayadapterindex ,final String _alarmlist) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("메뉴");
        alert.setMessage("이 알람을 삭제 하시겠습니까?");
        alert.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // alarmadapter.remove(_alarmlist);
                int no = no_list.get(arrayadapterindex);
                helper.delete_selected(no);
                Log.i("tttt delete ", String.valueOf(no));
                onResume();
            }
        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.create();
        alert.show();
    }

    @Override
    protected void onResume(){
        super.onResume();

        Cursor mycursor = helper.getAlarmTableAll();
        mycursor.moveToFirst();
        alarmadapter.clear();
        no_list.clear();

        while(!mycursor.isAfterLast()){
            String AM_PM = new String();
            int no = mycursor.getInt(mycursor.getColumnIndex("no"));
            int mtos  = mycursor.getInt(mycursor.getColumnIndex("MtoS")); //MtoS는 요일
            int bell_type = mycursor.getInt(mycursor.getColumnIndex("bell_type"));
            int ready_time = mycursor.getInt(mycursor.getColumnIndex("ready_time"));

            int isAM = mycursor.getInt(mycursor.getColumnIndex("is_AM"));
            String alarm_time = mycursor.getString(mycursor.getColumnIndex("alarm_time"));

            String arrive_place = mycursor.getString(mycursor.getColumnIndex("arrive_place"));
            float arrive_lat = mycursor.getFloat(mycursor.getColumnIndex("arrive_lat"));
            float arrive_lon = mycursor.getFloat(mycursor.getColumnIndex("arrive_lon"));

            String start_place = mycursor.getString(mycursor.getColumnIndex("start_place"));
            float start_lat = mycursor.getFloat(mycursor.getColumnIndex("start_lat"));
            float start_lon = mycursor.getFloat(mycursor.getColumnIndex("start_lon"));
            String result_time = mycursor.getString(mycursor.getColumnIndex("result_time"));
            if(mycursor.getInt(mycursor.getColumnIndex("is_AM"))==0)
                AM_PM = "오후";
            else
                AM_PM = "오전";

            Integer week = mycursor.getInt(1);
            StringBuilder strWeek = new StringBuilder();

            if((week & AlarmActivity.MONDAY) != 0)
                strWeek.append("월");
            if((week & AlarmActivity.TUESDAY) != 0)
                strWeek.append("화");
            if((week & AlarmActivity.WEDNESDAY) != 0)
                strWeek.append("수");
            if((week & AlarmActivity.THURSDAY) != 0)
                strWeek.append("목");
            if((week & AlarmActivity.FRIDAY) != 0)
                strWeek.append("금");
            if((week & AlarmActivity.SATURDAY) != 0)
                strWeek.append("토");
            if((week & AlarmActivity.SUNDAY) != 0)
                strWeek.append("일");
            StringTokenizer alarm_time_token = new StringTokenizer(alarm_time,":");

            int hour = Integer.parseInt(alarm_time_token.nextToken());
            if(hour ==0){
                hour =12 ;
            }
            String strMin =  alarm_time_token.nextToken();

            String result_alarmtime = hour + ":"+strMin ;
            alarm_time = result_alarmtime;


            if(result_time != null){
                StringTokenizer st = new StringTokenizer(result_time,":");
                int result_hour = Integer.parseInt(st.nextToken());
                int result_min = Integer.parseInt(st.nextToken());
                result_time = result_hour + ":" + result_min;
            }else{
                result_time = "Load";
            }


            String form = new String(AM_PM + "  " +alarm_time +"   요일: "+ strWeek +" "+ "알람시간" + result_time);
            //items2.add(form);
            alarmadapter.add(form);
            no_list.add(no);

            mycursor.moveToNext();
            Log.i("tttt",no+" "+mtos+" 시간"+alarm_time+"   오전?오후?  " + AM_PM + " 도착장소명:"+arrive_place);
        }
        mycursor.close();




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101010){
            /*Cursor mycursor = helper.getAlarmTableAll();
            mycursor.moveToFirst();

            while(!mycursor.isAfterLast()){
                String AM_PM = new String();
                int no = mycursor.getInt(0);
                int a1  = mycursor.getInt(1);

                String alarm_time = mycursor.getString(2);
                String arrive_place = mycursor.getString(6);
                ArrayList<String> temps = new ArrayList<String>();
                temps.add(mycursor.getString(3));
                items.put(mycursor.getInt(0), temps);
                if(mycursor.getInt(2)==0)
                    AM_PM = "오후";
                else
                    AM_PM = "오전";

                Integer week = mycursor.getInt(1);
                StringBuilder strWeek = new StringBuilder();

                if((week & AlarmActivity.MONDAY) != 0)
                    strWeek.append("월");
                if((week & AlarmActivity.TUESDAY) != 0)
                    strWeek.append("화");
                if((week & AlarmActivity.WEDNESDAY) != 0)
                    strWeek.append("수");
                if((week & AlarmActivity.THURSDAY) != 0)
                    strWeek.append("목");
                if((week & AlarmActivity.FRIDAY) != 0)
                    strWeek.append("금");
                if((week & AlarmActivity.SATURDAY) != 0)
                    strWeek.append("토");
                if((week & AlarmActivity.SUNDAY) != 0)
                    strWeek.append("일");

                String form = new String(AM_PM + "  " +mycursor.getString(3)+ strWeek);
                items2.add(form);
                alarmadapter.add(form);

                mycursor.moveToNext();
                Log.i("tttt",no+" "+a1+" 시간"+alarm_time+" 도착장소명:"+arrive_place);
            }
            mycursor.close();
            //alarmadapter.add(data.getExtras().getString("item"));
            Log.i("tttt", data.getExtras().getString("item"));
            //items2.add(data.getExtras().getString("item"));

            alarmadapter.notifyDataSetChanged();
*/
        }


        Log.i("tttt","hello");
    }
}
