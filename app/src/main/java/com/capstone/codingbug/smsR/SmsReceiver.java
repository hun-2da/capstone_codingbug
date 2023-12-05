package com.capstone.codingbug.smsR;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.capstone.codingbug.MainActivity;
import com.capstone.codingbug.R;
import com.capstone.codingbug.localdb.LocalDataBaseHelper;
import com.capstone.codingbug.pagerFragments.ReadLocation_Fragment;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {
    //private TMapView tMapView;
    int count = 0;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        SmsMessage[] messages = parseSmsMessage(bundle);

        if(messages != null && messages.length > 0){
            String contents = messages[0].getMessageBody();
            set_location(context,contents);
            Log.d("sms확인",contents);
        }
    }

    private SmsMessage[] parseSmsMessage(Bundle bundle) {
        Object[] objs = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[objs.length];

        for (int i = 0; i < objs.length; i++) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                String format = bundle.getString("format");
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i], format);
            } else {
                messages[i] = SmsMessage.createFromPdu((byte[]) objs[i]);
            }
        }

        return messages;
    }
    private void set_location(Context context,String location) {

        Pattern p = Pattern.compile("안심귀갓길 : \\((.*),(.*)\\)");
        Matcher m = p.matcher(location);

        if (m.find()) {
            String slatitude = m.group(1);
            String slongitude = m.group(2);

            double latitude = Double.parseDouble(slatitude);
            double longitude = Double.parseDouble(slongitude);

            Log.e("위치확인 latitude",String.valueOf(latitude));
            //addLocation(context,latitude,longitude);//데이터베이스 저장용
            new_marker(context,latitude,longitude);
        }
    }
    public void addLocation(Context context, double latitude, double longitude) {
        LocalDataBaseHelper dbHelper = new LocalDataBaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.now().toString();
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            date = sdf.format(new Date());
        }

        String INSERT_LOCATION = "INSERT INTO " + LocalDataBaseHelper.CTABLENAME +
                " (" + LocalDataBaseHelper.CDATE + ", " + LocalDataBaseHelper.CLATITUDE + ", " + LocalDataBaseHelper.CLOGITUDE + ")" +
                " VALUES ('" + date + "', " + latitude + ", " + longitude + ")";
        db.execSQL(INSERT_LOCATION);
        db.close();
    }

    public void new_marker(Context context, double latitude, double longitude){
        TMapMarkerItem markerItem1 = new TMapMarkerItem();

        TMapPoint tMapPoint1 = new TMapPoint(latitude, longitude);

        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon);
        int width = 120;
        int height = 120;
        boolean filter = true;  // 필터링. true로 설정하면 스무딩 효과를 줍니다.
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, filter);

        markerItem1.setIcon(resizedBitmap); // 마커 아이콘 지정
        markerItem1.setPosition(0.5f, 1.0f); // 마커의 중심점을 중앙, 하단으로 설정
        markerItem1.setTMapPoint( tMapPoint1 ); // 마커의 좌표 지정
        markerItem1.setName("피보호자 위치" + count++); // 마커의 타이틀 지정
        ReadLocation_Fragment.tMapView2.addMarkerItem("markerItem"+count, markerItem1); // 지도에 마커 추가

        ReadLocation_Fragment.tMapView2.setCenterPoint(tMapPoint1.getLongitude(),tMapPoint1.getLatitude());
    }
}