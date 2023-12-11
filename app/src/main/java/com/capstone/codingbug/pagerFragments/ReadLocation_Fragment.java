package com.capstone.codingbug.pagerFragments;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;

import com.capstone.codingbug.MainActivity;
import com.capstone.codingbug.R;
import com.capstone.codingbug.localdb.LocalDataBaseHelper;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import java.util.ArrayList;


public class ReadLocation_Fragment extends Fragment {
    public static TMapView tMapView2;
    LinearLayout linearLayoutTmap;
    Button date_button;
    FrameLayout date_fragment;
    Spinner phone_spinner,date_spinner;
    SQLiteDatabase sqLiteDatabase;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_read_location_, container, false);




        linearLayoutTmap = (LinearLayout) view.findViewById(R.id.read_llt);
        tMapView2 = new TMapView(getActivity().getApplicationContext());

        tMapView2.setSKTMapApiKey("6QqIU9fnZUao65WJCM7ptafry6XfQovT1PoVoB4a");
        linearLayoutTmap.addView(tMapView2);

        makerItem();


        date_fragment = view.findViewById(R.id.date_f);
        date_button = view.findViewById(R.id.db_date_button);
        phone_spinner = view.findViewById(R.id.Phonespinner);
        date_spinner = view.findViewById(R.id.Datespinner);


        //local db connect
        LocalDataBaseHelper dbHelper = new LocalDataBaseHelper(getContext());
        sqLiteDatabase = dbHelper.getReadableDatabase();

        date_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(date_fragment.getVisibility() == View.INVISIBLE){
                 date_fragment.setVisibility(View.VISIBLE);
                    set_Spinner_phone();
                    set_Spinner_date();
                    Log.e("date_button","클릭");
                }else{
                    date_fragment.setVisibility(View.INVISIBLE);
                }

            }
        });

        date_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                tMapView2.removeAllMarkerItem();

                String selectedValue_phone = phone_spinner.getSelectedItem().toString();
                String selectedValue_date = date_spinner.getSelectedItem().toString();
                //Log.e("selcet 확인", selectedValue_phone +"    "+selectedValue_date);
                //if(!selectedValue_date.equals("") && !selectedValue_date.equals("")) {
                    Log.e("selcet 확인", selectedValue_phone +"    "+selectedValue_date);
                    //SQLiteDatabase db = dbHelper.getWritableDatabase(); // dbHelper는 LocalDataBaseHelper의 인스턴스입니다.
                    String query = "SELECT " + LocalDataBaseHelper.CLATITUDE + ", " + LocalDataBaseHelper.CLOGITUDE +
                            " FROM " + LocalDataBaseHelper.CTABLENAME +
                            " WHERE " + LocalDataBaseHelper.PHONE_NUMBER + " = ? AND " + LocalDataBaseHelper.CDATE + " = ?";
                    String[] selectionArgs = {selectedValue_phone, selectedValue_date};
                    Cursor cursor = sqLiteDatabase.rawQuery(query, selectionArgs);

                    while (cursor.moveToNext()) {
                        double latitude = cursor.getDouble(0);
                        double longitude = cursor.getDouble(1);

                        Log.e("location 확인", Double.toString(latitude) + Double.toString(longitude));

                        add_marker(latitude,longitude,selectedValue_phone,selectedValue_date);

                    }
                //}


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tMapView2.setOnMarkerClickEvent(new TMapView.OnCalloutMarker2ClickCallback() {
            @Override
            public void onCalloutMarker2ClickEvent(String s, TMapMarkerItem2 tMapMarkerItem2) {

            }
        });



        return view;
    }
    private void set_Spinner_phone(){

// 전화번호를 가져오는 쿼리
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT DISTINCT " + LocalDataBaseHelper.PHONE_NUMBER + " FROM " + LocalDataBaseHelper.CTABLENAME, null);
        ArrayList<String> phoneList = new ArrayList<>();
        while(cursor.moveToNext()) {
            phoneList.add(cursor.getString(0));
        }
        cursor.close();
// ArrayAdapter 생성
        ArrayAdapter<String> phoneAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, phoneList);
// Spinner에 ArrayAdapter 연결
        phone_spinner.setAdapter(phoneAdapter);
    }
    private void set_Spinner_date(){
        phone_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedPhone = parent.getItemAtPosition(position).toString();
                // 선택된 전화번호에 해당하는 날짜를 가져오는 쿼리
                //LocalDataBaseHelper dbHelper = new LocalDataBaseHelper(getActivity().getApplicationContext());
                //SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
                Cursor cursor = sqLiteDatabase.rawQuery("SELECT " + LocalDataBaseHelper.CDATE +
                        " FROM " + LocalDataBaseHelper.CTABLENAME +
                        " WHERE " + LocalDataBaseHelper.PHONE_NUMBER +
                        " = ? GROUP BY " + LocalDataBaseHelper.CDATE, new String[]{selectedPhone});


                /**날짜가 들어있는 arrayList _*/
                ArrayList<String> dateList = new ArrayList<>();
                while (cursor.moveToNext()) {
                    dateList.add(cursor.getString(0));
                }
                cursor.close();
                // ArrayAdapter 생성
                ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, dateList);
                // Spinner에 ArrayAdapter 연결
                date_spinner.setAdapter(dateAdapter);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 아무것도 선택되지 않았을 때의 처리
            }
        });
    }


    static int count = 0;
    Bitmap resizedBitmap;
    private void makerItem()
    {
        Bitmap bitmap = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.marker_icon);
        int width = 120;
        int height = 120;
        boolean filter = true;
        resizedBitmap = Bitmap.createScaledBitmap(bitmap, width, height, filter);
    }
    public void add_marker(double latitude, double longitude, String phone, String date) {

        TMapMarkerItem markerItem = new TMapMarkerItem();

        markerItem.setIcon(resizedBitmap);
        markerItem.setPosition(0.5f, 1.0f);

        TMapPoint tMapPoint = new TMapPoint(latitude, longitude);
        String markerId = phone + date + System.currentTimeMillis(); // 타임스탬프 추가

        markerItem.setTMapPoint(tMapPoint);
        markerItem.setName(phone + " 휴대폰의 피보호자 위치");

        // 마커 추가
        tMapView2.addMarkerItem(markerId, markerItem);

        // 지도의 중심점을 마지막 마커의 위치로 설정
        tMapView2.setCenterPoint(tMapPoint.getLongitude(), tMapPoint.getLatitude());
    }
}