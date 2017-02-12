package com.sujityadav.smstest.BroadcastReceiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.sujityadav.smstest.Activity.MainActivity;
import com.sujityadav.smstest.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by sujit yadav on 2/12/2017.
 */

public class SMSBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      String str1 = null,str2=null;
        NotificationCompat.Builder mBuilder = null;
        Bundle bundle = intent.getExtras();
        SmsMessage[] msgs = null;
        String str = "no message received";
        if(bundle != null) {

            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i = 0; i < msgs.length; i++) {

                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                str1 = msgs[i].getOriginatingAddress();

                str2 = msgs[i].getMessageBody().toString();

            }

            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(str1)
                            .setContentText(str2);
            Intent resultIntent = new Intent(context, MainActivity.class);
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            context,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );

            mBuilder.setContentIntent(resultPendingIntent);
            int mNotificationId = 001;
// Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        }
    }}

