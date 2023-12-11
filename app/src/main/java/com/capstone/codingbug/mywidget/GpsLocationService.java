package com.capstone.codingbug.mywidget;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.capstone.codingbug.MainActivity;
import com.capstone.codingbug.R;
import com.capstone.codingbug.localdb.LocalDataBaseHelper;

public class GpsLocationService extends Service implements LocationListener {
    private static final String TAG = "GpsLocationService";
    /**서비스에서 실행될 스위치 (여러번 실행되는 것을 막기 위함)*/
    private static boolean w_switch = false;
    private static final long LOCATION_UPDATE_INTERVAL = 2000; // 2 seconds

    private double latitude, longitude;

    private LocationManager locationManager;

    String p_phone = "";
    Handler handler = new Handler(Looper.getMainLooper());


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private static String CHANNEL_ID ="channel1";
    private static String CHANNEL_NAME ="Channel1";

   /* public void onCreate() {
        //super.onCreate();
        *//*NotificationManager manager;
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                manager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT
                ));
                builder = new NotificationCompat.Builder(this,CHANNEL_ID);

            }else
                builder = new NotificationCompat.Builder(this);

            builder.setContentTitle("실행 중");
            builder.setContentText("현재 위치정보를 전송 중입니다.");
            builder.setSmallIcon(android.R.drawable.ic_menu_view);
            Notification noti = builder.build();

            manager.notify(1,noti);*//*

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        Log.e("실행중","hi-3   " + w_switch);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        Log.e("실행중","hi-4   " + w_switch);
        builder.setContentTitle("실행 중");
        builder.setContentText("현재 위치정보를 전송 중입니다.");
        builder.setSmallIcon(android.R.drawable.ic_menu_view);
        Notification noti = builder.build();
        Log.e("실행중","hi-5   " + w_switch);
// 알림 표시

        manager.notify(1,noti);
        //startForeground(1, noti);


        Log.e("실행중","hi-6   " + w_switch);
    }*/
    @Override
    public void onCreate() {
        super.onCreate();

        // 알림 생성 및 표시
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            manager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setContentTitle("실행 중");
        builder.setContentText("현재 위치정보를 전송 중입니다.");
        builder.setSmallIcon(android.R.drawable.ic_menu_view);
        Notification notification = builder.build();

        // Foreground Service 표시
        startForeground(1, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("실행중","onStartCommand" + w_switch);

        if(!w_switch){
            Log.e("실행중","hi-1   " + w_switch);
            startLocationUpdates();
            Log.e("실행중","hi-2   " + w_switch);


            w_switch = true;

            LocalDataBaseHelper localdb = new LocalDataBaseHelper(getApplicationContext());// 로컬 데이터베이스 생성
            SQLiteDatabase ldb = localdb.getReadableDatabase();

            String[] columns = {LocalDataBaseHelper.parent_phone};
            Cursor cursor = ldb.query(LocalDataBaseHelper.table, columns, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                p_phone = cursor.getString(0);
            }

            Log.e("실행중","hi-7   " + w_switch);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.e("실행중", "service sleep");
                    while(w_switch){
                        location_widget(p_phone);
                        handler.postDelayed(this, 100000); // 5초마다 반복
                    }
                }
            }).start();

        }
        else w_switch = false;

        return super.onStartCommand(intent, flags, startId);
    }

    private void startLocationUpdates() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        LOCATION_UPDATE_INTERVAL, 0, this);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Error requesting location updates: " + e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.e("service에서","location change 일어남");
        latitude = location.getLatitude();
        longitude = location.getLongitude();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        stopForeground(true);
        stopSelf();
    }

    private void stopLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    public void location_widget(String phone) {
        String message = "안심귀갓길: (" + latitude + "," + longitude + ")";
        sendSMS(getApplicationContext(), phone, message);
    }

    public void sendSMS(Context context, String phoneNumber, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        PendingIntent sentPI;
        String SENT = "SMS_SENT";

        sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT), PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        smsManager.sendTextMessage(phoneNumber, null, message, sentPI, null);
    }
}