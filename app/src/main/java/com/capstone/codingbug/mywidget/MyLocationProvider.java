package com.capstone.codingbug.mywidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import com.capstone.codingbug.R;


public class MyLocationProvider extends AppWidgetProvider {

        @Override
        public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
            super.onUpdate(context, appWidgetManager, appWidgetIds);
            int number = 0;
            RemoteViews r_view = new RemoteViews(context.getPackageName(), R.layout.location_widget);

            for(int widgetIds : appWidgetIds){
                Intent intent = new Intent(context, MyLocationProvider.class);
                intent.setAction("button1onclick");
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                r_view.setOnClickPendingIntent(R.id.widget_button,pendingIntent);  //클릭이벤트 발생시 pendingIntent실행
                appWidgetManager.updateAppWidget(appWidgetIds[number++], r_view);

            }

        }
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);

            if(intent.getAction().equals("button1onclick")){
                Log.e("실행중","service sleep");
                RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.location_widget);
                remoteViews.setTextViewText(R.id.widget_button,"도착");
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);


                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MyLocationProvider.class));
                appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);


                        Intent serviceIntent = new Intent(context, GpsLocationService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            Log.e("실행중","foregroundService0");

                            context.startForegroundService(serviceIntent);

                            Log.e("실행중","foregroundService1");
                        } else {

                            context.startService(serviceIntent);


                            Log.e("실행중","foregroundService2");
                        }
                        //context.startService(serviceIntent);
                        //context.stopService(serviceIntent);



            }
        }


    }
