package com.example.jukyungyoo.myapplication_0525.Alarm.Alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by jukyungyoo on 2016-05-17.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.e("We are in the receiver.", "Yay!");

        // fetch extra strings from the intent
        // tells the app whether the user pressed the alarm on button or the alarm off button
        String get_your_string = intent.getExtras().getString("extra");

        Log.e("What is the key? ", get_your_string);

        // fetch the extra longs from the intent
        // tells the app which value the user picked from the drop down menu/spinner
        Integer get_your_whale_choice = intent.getExtras().getInt("whale_choice");

        Log.e("The whale choice is ", get_your_whale_choice.toString());

        // create an intent to the ringtone service
        Intent service_intent = new Intent(context, AlarmRing.class);

        // pass the extra string from Receiver to the Ringtone Playing Service
        service_intent.putExtra("extra", get_your_string);
        // pass the extra integer from the Receiver to the Ringtone Playing Service
        service_intent.putExtra("whale_choice", get_your_whale_choice);

        switch (get_your_string) {
            case "alarm on":
                Log.d("tttt","alarm off!!!!");
                Bundle extra = intent.getExtras();
                if (extra != null)
                {
                    boolean[] week = extra.getBooleanArray("weekday");
                    Calendar cal = Calendar.getInstance();

                    //if (!week[cal.get(Calendar.DAY_OF_WEEK)])
                     // return;

                    // start the ringtone service
                    //context.startService(service_intent);
                    service_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(service_intent);
                }
                break;
            case "alarm off":
                Log.d("tttt","alarm off!!!!");

                break;
            default:

                break;
        }



    }
}
